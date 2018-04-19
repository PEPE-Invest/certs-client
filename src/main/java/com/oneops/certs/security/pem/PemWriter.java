package com.oneops.certs.security.pem;

import static java.util.Base64.getMimeEncoder;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * PEM or Privacy Enhanced Mail is a file format for storing and sending X.509v3 certificates. The
 * PEM file contains Base64 encoded DER certificate data prefixed with a "-----BEGIN.." header and
 * "-----END..." footer.
 */
public class PemWriter {

  private PemWriter() {}

  /**
   * Returns {@link PublicKey} as PEM encoded string.
   *
   * @param publicKey Keystore public key.
   * @return PEM encoded string.
   */
  public static String writePublicKey(PublicKey publicKey) {
    return encodePem(PemLabel.PUBLIC_KEY, publicKey.getEncoded());
  }

  /**
   * Returns {@link PrivateKey} as PEM encoded string.
   *
   * @param privateKey Keystore private key.
   * @return PEM encoded string.
   */
  public static String writePrivateKey(PrivateKey privateKey) {
    return encodePem(PemLabel.PRIVATE_KEY, privateKey.getEncoded());
  }

  /**
   * Returns {@link X509Certificate} as PEM encoded string.
   *
   * @param certificate Keystore certificate.
   * @return PEM encoded string.
   */
  public static String writeCertificate(X509Certificate certificate)
      throws CertificateEncodingException {
    return encodePem(PemLabel.CERTIFICATE, certificate.getEncoded());
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
    for (Certificate cert : certificates) {
      buf.append(writeCertificate((X509Certificate) cert));
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
    StringBuilder buf = new StringBuilder();
    return buf.append("-----BEGIN ")
        .append(label.labelName())
        .append("-----\n")
        .append(cert)
        .append('\n')
        .append("-----END ")
        .append(label.labelName())
        .append("-----\n")
        .toString();
  }
}
