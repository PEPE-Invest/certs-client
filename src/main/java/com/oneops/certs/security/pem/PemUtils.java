package com.oneops.certs.security.pem;

import static com.oneops.certs.security.pem.PemLabel.CERTIFICATE;
import static com.oneops.certs.security.pem.PemLabel.PRIVATE_KEY;
import static com.oneops.certs.security.pem.PemLabel.PUBLIC_KEY;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Base64.getMimeDecoder;
import static java.util.Base64.getMimeEncoder;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Some utility methods to deal with private key, client cert and cacerts pem files.
 *
 * <p>PEM or Privacy Enhanced Mail is a file format for storing and sending X.509v3 certificates.
 * The PEM file contains Base64 encoded DER certificate data prefixed with a "-----BEGIN.." header
 * and "-----END..." footer.
 */
public class PemUtils {

  /** Private key PEM file pattern. */
  private static final Pattern PRIVATE_KEY_PATTERN =
      Pattern.compile(
          "-+BEGIN\\s+.*PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+" // Header
              + "([a-z0-9+/=\\r\\n]+)" // Base64 text
              + "-+END\\s+.*PRIVATE\\s+KEY[^-]*-+", // Footer
          CASE_INSENSITIVE);

  /**
   * PasswordBasedEncryption algorithm used for encrypting the private key, you may use any common
   * algorithm supported by openssl, you can check them in the openssl documentation. By default we
   * are using <b>Triple-DES/CBCMode/PKCS5Padding</b>.
   */
  private static final String PBE_ALGO = "PBEWithSHA1AndDESede";

  private PemUtils() {}

  /**
   * Returns {@link PublicKey} as PEM encoded string.
   *
   * @param publicKey Keystore public key.
   * @return PEM encoded string.
   */
  public static String writePublicKey(PublicKey publicKey) {
    return encodePem(PUBLIC_KEY, publicKey.getEncoded());
  }

  /**
   * Returns {@link PrivateKey} as PEM encoded string.
   *
   * @param privateKey Keystore private key.
   * @return PEM encoded string.
   */
  public static String writePrivateKey(PrivateKey privateKey) {
    return encodePem(PRIVATE_KEY, privateKey.getEncoded());
  }

  /**
   * Returns {@link X509Certificate} as PEM encoded string.
   *
   * @param certificate Keystore certificate.
   * @return PEM encoded string.
   */
  public static String writeCertificate(X509Certificate certificate)
      throws CertificateEncodingException {
    return encodePem(CERTIFICATE, certificate.getEncoded());
  }

  /**
   * Returns {@link X509Certificate} chain as PEM encoded string.
   *
   * @param certificates certificate chain.
   * @return PEM encoded string.
   */
  public static String writeCertificates(List<X509Certificate> certificates)
      throws CertificateEncodingException {
    StringBuilder buf = new StringBuilder();
    for (X509Certificate cert : certificates) {
      buf.append(writeCertificate(cert));
    }
    return buf.toString();
  }

  /**
   * Returns a PEM encoded string from the DER encoded data.
   *
   * @param label {@link PemLabel}
   * @param encoded DER encoded string.
   * @return PEM encoded string.
   */
  public static String encodePem(PemLabel label, byte[] encoded) {
    String cert = getMimeEncoder(64, new byte[] {'\n'}).encodeToString(encoded);
    return "-----BEGIN "
        + label.labelName()
        + "-----\n"
        + cert
        + '\n'
        + "-----END "
        + label.labelName()
        + "-----\n";
  }

  /**
   * Decode the base64 pem content to DER.
   *
   * @param base64 base64 encoded pem string.
   * @return DER encode string.
   */
  private static byte[] decodeBase64Pem(String base64) {
    return getMimeDecoder().decode(base64.getBytes(US_ASCII));
  }

  /**
   * Loads the private key in pem format. Provide a key password if the private key is encrypted.
   *
   * <p>If the private key is encrypted, make sure the key is in PKCS#8 wrapped format which is not
   * the default openssl format. To convert to the PKCS#8 format, use : <b>cat enc-key.pem | openssl
   * pkcs8 -topk8 -inform pem -outform pem [-nocrypt] </b>
   *
   * @param privateKeyPem private key in Base64 encoded pem format.
   * @param keyPassword optional private key password.
   * @return {@link PrivateKey}
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public static PrivateKey loadPrivateKey(String privateKeyPem, Optional<String> keyPassword)
      throws IOException, GeneralSecurityException {

    Matcher matcher = PRIVATE_KEY_PATTERN.matcher(privateKeyPem);
    if (!matcher.find()) {
      throw new IllegalArgumentException("Did not find a private key in pem format!");
    }
    byte[] derKey = decodeBase64Pem(matcher.group(1));

    PKCS8EncodedKeySpec encodedKeySpec;
    if (keyPassword.isPresent()) {
      EncryptedPrivateKeyInfo epkInfo = new EncryptedPrivateKeyInfo(derKey);
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(epkInfo.getAlgName());
      SecretKey secretKey =
          keyFactory.generateSecret(new PBEKeySpec(keyPassword.get().toCharArray()));

      Cipher cipher = Cipher.getInstance(epkInfo.getAlgName());
      cipher.init(Cipher.DECRYPT_MODE, secretKey, epkInfo.getAlgParameters());

      encodedKeySpec = epkInfo.getKeySpec(cipher);
    } else {
      encodedKeySpec = new PKCS8EncodedKeySpec(derKey);
    }

    try {
      return KeyFactory.getInstance("RSA").generatePrivate(encodedKeySpec);
    } catch (InvalidKeySpecException ignore) {
    }

    try {
      return KeyFactory.getInstance("EC").generatePrivate(encodedKeySpec);
    } catch (InvalidKeySpecException ignore) {
    }

    return KeyFactory.getInstance("DSA").generatePrivate(encodedKeySpec);
  }

  /**
   * Encrypt the private using a {@link #PBE_ALGO} algorithm.
   *
   * @param privateKey {@link PrivateKey}
   * @param password private key password to encrypt.
   * @return Encrypted PEM formatted private key.
   * @throws GeneralSecurityException
   * @throws IOException
   * @see <a href="http://www.openssl.org/docs/apps/pkcs8.html">Openssl PKCS#8</a>
   * @see <a
   *     href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/SunProviders.html">Cipher
   *     Algos</a>
   */
  public static String encryptPrivateKey(PrivateKey privateKey, String password)
      throws GeneralSecurityException, IOException {

    int count = 20; // hash iteration count
    byte[] salt = new byte[8];
    SecureRandom random = new SecureRandom();
    random.nextBytes(salt);

    // Create PBE parameter set
    PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, count);

    SecretKeyFactory keyFac = SecretKeyFactory.getInstance(PBE_ALGO);
    SecretKey pbeKey = keyFac.generateSecret(new PBEKeySpec(password.toCharArray()));

    Cipher pbeCipher = Cipher.getInstance(PBE_ALGO);
    pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
    byte[] cipherText = pbeCipher.doFinal(privateKey.getEncoded());

    // Now construct  PKCS#8 EncryptedPrivateKeyInfo object
    AlgorithmParameters algparms = AlgorithmParameters.getInstance(PBE_ALGO);
    algparms.init(pbeParamSpec);
    EncryptedPrivateKeyInfo encInfo = new EncryptedPrivateKeyInfo(algparms, cipherText);

    // DER encoded PKCS#8 encrypted key.
    byte[] encPrivateKey = encInfo.getEncoded();
    // Convert it to PEM format.
    return PemUtils.encodePem(PemLabel.ENCRYPTED_PRIVATE_KEY, encPrivateKey);
  }
}
