package com.oneops.certs;

import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

import com.google.auto.value.AutoValue;
import com.oneops.certs.model.CertFormat;
import com.oneops.certs.model.CreateReq;
import com.oneops.certs.model.CreateRes;
import com.oneops.certs.model.CwsRequest;
import com.oneops.certs.model.CwsResponse;
import com.oneops.certs.model.DateAdapter;
import com.oneops.certs.model.DownloadReq;
import com.oneops.certs.model.DownloadRes;
import com.oneops.certs.model.ExistsRes;
import com.oneops.certs.model.ExpirationRes;
import com.oneops.certs.model.ExpiringReq;
import com.oneops.certs.model.ExpiringRes;
import com.oneops.certs.model.GenericResponse;
import com.oneops.certs.model.JsonAdapterFactory;
import com.oneops.certs.model.Redacted;
import com.oneops.certs.model.RenewReq;
import com.oneops.certs.model.RevokeReason;
import com.oneops.certs.model.RevokeReq;
import com.oneops.certs.model.RevokeRes;
import com.oneops.certs.model.SerialNumberRes;
import com.oneops.certs.model.ViewRes;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import javax.annotation.Nullable;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Certificate Web Service client, used to create, renew, and download X509 certificates.
 * Authentication to Certificate Web Services is performed via certificate based mutual TLS.
 *
 * @author Suresh
 */
@AutoValue
public abstract class CwsClient {

  private final Logger log = LoggerFactory.getLogger(getClass());

  /** CWS end point name. */
  public abstract String endPoint();

  /** Application id requesting certificate management system to perform certificate operations. */
  public abstract String appId();

  /**
   * Team email distribution list set up in the certificate management system, which owns the
   * certificate. CWS uses a hierarchical structure like "Org\\Team\\Project\\....".
   */
  public abstract String teamDL();

  /**
   * Domain name for requesting certificate in format like “your-company.com”. Domain field is
   * required when requesting internet facing (external) certificate. It is optional field if
   * requesting internal certificate.
   */
  @Nullable
  public abstract String domain();

  /**
   * PKCS#12 (.p12) keystore file path used for authentication to Certificate Web Services, which is
   * performed via certificate based mutual TLS. If the path starts with <b>classpath:</b>, it will
   * be loaded from classpath.
   */
  public abstract String keystore();

  /** Keystore password. */
  @Redacted
  public abstract String storePassword();

  /** Enable http logging for debugging. */
  public abstract boolean debug();

  /** Connection/read/write timeout. */
  public abstract int timeout();

  private CertWebService certWebService;

  /**
   * Initializes the TLS retrofit client.
   *
   * @throws GeneralSecurityException if any error initializing the TLS context.
   */
  private void init() throws GeneralSecurityException {
    log.info("Initializing " + toString());
    Moshi moshi =
        new Moshi.Builder()
            .add(JsonAdapterFactory.create())
            .add(new DateAdapter())
            .add(new RevokeReason.Adapter())
            .build();

    KeyStore keystore = loadKeystore();
    TrustManager[] trustManagers = initTrustManager(keystore);
    KeyManager[] keyManagers = initKeyManager(keystore);

    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
    sslContext.init(keyManagers, trustManagers, new SecureRandom());
    SSLSocketFactory socketFactory = sslContext.getSocketFactory();

    OkHttpClient.Builder okBuilder =
        new OkHttpClient()
            .newBuilder()
            .sslSocketFactory(socketFactory, (X509TrustManager) trustManagers[0])
            .connectionSpecs(singletonList(ConnectionSpec.MODERN_TLS))
            .followSslRedirects(false)
            .retryOnConnectionFailure(false)
            .connectTimeout(timeout(), SECONDS)
            .readTimeout(timeout(), SECONDS)
            .writeTimeout(timeout(), SECONDS)
            .addInterceptor(
                chain -> {
                  Request req =
                      chain
                          .request()
                          .newBuilder()
                          .addHeader("Content-Type", "application/json")
                          .build();
                  return chain.proceed(req);
                });

    if (debug()) {
      HttpLoggingInterceptor logIntcp = new HttpLoggingInterceptor(log::info);
      logIntcp.setLevel(BODY);
      okBuilder.addNetworkInterceptor(logIntcp);
    }
    OkHttpClient okHttp = okBuilder.build();

    Retrofit retrofit =
        new Retrofit.Builder()
            .baseUrl(endPoint())
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build();

    certWebService = retrofit.create(CertWebService.class);
  }

  /**
   * Load the keystore (PKCS12) from the given file/classpath resource.
   *
   * @throws IllegalStateException if the file/classpath resource doesn't exist.
   */
  private KeyStore loadKeystore() {
    String ksPath = keystore().toLowerCase();
    boolean fileResource = true;

    if (ksPath.startsWith("classpath:")) {
      ksPath = ksPath.replace("classpath:", "");
      fileResource = false;
    }

    try {
      try (InputStream ins =
          fileResource
              ? Files.newInputStream(Paths.get(ksPath))
              : getClass().getResourceAsStream(ksPath)) {

        log.info("Loading the keystore: {}", keystore());
        if (ins == null) {
          throw new IllegalStateException("Can't find the keystore: " + keystore());
        }
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(ins, storePassword().toCharArray());
        return ks;
      }
    } catch (IOException | GeneralSecurityException ex) {
      throw new IllegalStateException("Can't load the keystore: " + keystore(), ex);
    }
  }

  /**
   * Initialize new trust managers from the trust-store.
   *
   * @param keystore PKCS12 keystore
   */
  private TrustManager[] initTrustManager(KeyStore keystore) throws GeneralSecurityException {
    final TrustManagerFactory trustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(keystore);
    return trustManagerFactory.getTrustManagers();
  }

  /**
   * Initialize new key managers from the keystore.
   *
   * @param keystore PKCS12 keystore
   */
  private KeyManager[] initKeyManager(KeyStore keystore) throws GeneralSecurityException {
    final KeyManagerFactory kmfactory =
        KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    kmfactory.init(keystore, storePassword().toCharArray());
    return kmfactory.getKeyManagers();
  }

  /**
   * Helper method to execute {@link Call} object and returns the proper response or throws {@link
   * CwsException}.
   *
   * <p>CWS has some crappy way of handling error responses and it won't honour any standard HTTP
   * error codes.
   */
  private <T extends GenericResponse> T exec(Call<T> call) throws IOException {
    Response<T> res = call.execute();
    T body = res.body();

    if (body != null) {
      if (body.success()) {
        return body;
      } else {
        StringBuilder buf = new StringBuilder();
        buf.append(body.error());
        if (body.errorMsg() != null) {
          buf.append(", ").append(body.errorMsg());
        }
        throw new CwsException(buf.toString());
      }
    } else {
      throw new CwsException("Null response from Certificate Web service.");
    }
  }

  public static Builder builder() {
    return new AutoValue_CwsClient.Builder().debug(false).timeout(10);
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder endPoint(String endPoint);

    public abstract Builder appId(String appId);

    public abstract Builder teamDL(String teamDL);

    public abstract Builder domain(String domain);

    public abstract Builder keystore(String keystore);

    public abstract Builder storePassword(String password);

    public abstract Builder debug(boolean debug);

    public abstract Builder timeout(int timeout);

    public abstract CwsClient autoBuild();

    /**
     * Build and initialize CWS client.
     *
     * @return {@link CwsClient}.
     */
    public CwsClient build() {
      CwsClient client = autoBuild();
      try {
        client.init();
      } catch (GeneralSecurityException ex) {
        throw new IllegalArgumentException("CwsClient init failed.", ex);
      }
      return client;
    }
  }

  /**
   * Create requests certificate from Certificate Management system. This call supports explicitly
   * providing the certificate fields and letting certificate management system centrally create the
   * key pair and CSR. If the certificate being requested already exists, the call will fail. Caller
   * application or app id must have ‘create’ rights to the policy where the certificate will be
   * created.
   *
   * @param req {@link CreateReq}
   * @return {@link CreateRes}
   * @throws IOException throws if certificate already exists or PolicyDN doesn't exist or any
   *     communication error to the service.
   */
  public CreateRes createCert(CreateReq req) throws IOException {
    return exec(certWebService.create(req));
  }

  /**
   * Normalize the teamDL name if it's relative.
   *
   * @param teamDLName team DL name.
   * @return normalized teamDL name
   */
  private String normalizeTeamDL(String teamDLName) {
    StringBuilder buf = new StringBuilder();
    if (!teamDLName.startsWith(teamDL())) {
      buf.append(teamDL()).append("\\");
    }
    buf.append(teamDLName);
    return buf.toString();
  }

  /**
   * Creates a new certificate for the given common name and SANs under given email.
   *
   * @param commonName cert common name
   * @param subjectAltNames Subject Alternative Name (SAN). This can be email, IP address, DNS name,
   *     URI etc.
   * @param teamDLName The team DL, which can be absolute/relative to the base {@link #teamDL()}.
   * @return certificate DN of the generated cert.
   * @throws IOException throws if certificate already exists or PolicyDN doesn't exist or any *
   *     communication error to the service.
   */
  public String createCert(String commonName, List<String> subjectAltNames, String teamDLName)
      throws IOException {
    CreateReq req =
        CreateReq.builder()
            .appId(appId())
            .teamDL(normalizeTeamDL(teamDLName))
            .subjectAltName(subjectAltNames)
            .commonName(commonName)
            .domain(domain())
            .build();
    return createCert(req).certificateDN();
  }

  /**
   * Creates a new policy for the specified teamDL if not exists.
   *
   * @param teamDLName The team DL, which can be absolute/relative to the base {@link #teamDL()}.
   * @return <code>true</code> if policy is created or already exists.
   * @throws IOException throws if can't create the PolicyDN or any communication error to the
   *     service.
   */
  public boolean createPolicyDN(String teamDLName) throws IOException {
    CreateReq req =
        CreateReq.builder()
            .appId(appId())
            .teamDL(normalizeTeamDL(teamDLName))
            .commonName("")
            .domain("")
            .createPolicy("1")
            .build();
    return createCert(req).success();
  }

  /**
   * Renew an existing certificate. This call marks a certificate for immediate renewal. The
   * certificate must not be in error or processing state in order for it to be renewable.
   *
   * @param req {@link RenewReq}
   * @return {@link CwsResponse}
   * @throws IOException throws if certificate/PolicyDN doesn't exist or any communication error to
   *     the service.
   */
  public CwsResponse renewCert(RenewReq req) throws IOException {
    return exec(certWebService.renew(req));
  }

  /**
   * Renew an existing certificate with the given common name.
   *
   * @param commonName cert common name
   * @param teamDLName The team DL, which can be absolute/relative to the base {@link #teamDL()}.
   * @return <code>true</code> if the cert renew is successful.
   * @throws IOException throws if certificate/PolicyDN doesn't exist or any communication error to
   *     the service.
   */
  public boolean renewCert(String commonName, String teamDLName) throws IOException {
    RenewReq req =
        RenewReq.builder()
            .appId(appId())
            .teamDL(normalizeTeamDL(teamDLName))
            .commonName(commonName)
            .build();
    return renewCert(req).success();
  }

  /**
   * Obsoletes an existing certificate.
   *
   * @param req {@link CwsRequest}
   * @return {@link CwsResponse}
   * @throws IOException throws if certificate/PolicyDN doesn't exist or any communication error to
   *     the service.
   */
  public CwsResponse obsoleteCert(CwsRequest req) throws IOException {
    return exec(certWebService.obsolete(req));
  }

  /**
   * Obsoletes an existing certificate with the given common name.
   *
   * @param commonName cert common name
   * @param teamDLName The team DL, which can be absolute/relative to the base {@link #teamDL()}.
   * @return <code>true</code> if the operation is successful.
   * @throws IOException throws if certificate/PolicyDN doesn't exist or any communication error to
   *     the service.
   */
  public boolean obsoleteCert(String commonName, String teamDLName) throws IOException {
    CwsRequest req =
        CwsRequest.builder()
            .appId(appId())
            .teamDL(normalizeTeamDL(teamDLName))
            .commonName(commonName)
            .build();
    return obsoleteCert(req).success();
  }

  /**
   * Returns the certificate data for certificate in the format requested in base64 encoded format.
   *
   * @param req {@link DownloadReq}
   * @return {@link DownloadRes}
   * @throws IOException throws if certificate/PolicyDN doesn't exist or any communication error to
   *     the service.
   */
  public DownloadRes downloadCert(DownloadReq req) throws IOException {
    return exec(certWebService.download(req));
  }

  /**
   * Returns the certificate data with given details in the format requested in base64 encoded
   * format.
   *
   * @param commonName cert common name
   * @param teamDLName The team DL, which can be absolute/relative to the base {@link #teamDL()}.
   * @param password The password to protect the private key. Password complexity requires between
   *     12 and 100 characters and at least one of each the following:
   *     <pre>
   *        * Uppercase character
   *        * Lowercase character
   *        * Special character
   *        * Number
   *      </pre>
   *
   * @param format {@link CertFormat} The format in which the certificate should be returned.
   * @return Certificate data in the format requested in base64 encoded format.
   * @throws IOException throws if certificate/PolicyDN doesn't exist or any communication error to
   *     the service.
   */
  public String downloadCert(
      String commonName, String teamDLName, String password, CertFormat format) throws IOException {
    DownloadReq req =
        DownloadReq.builder()
            .appId(appId())
            .teamDL(normalizeTeamDL(teamDLName))
            .commonName(commonName)
            .format(format)
            .password(password)
            .build();
    return downloadCert(req).certificateData();
  }

  /**
   * Returns the date which a certificate will expire.
   *
   * @param req {@link CwsRequest}
   * @return {@link ExpirationRes}
   * @throws IOException throws if certificate/PolicyDN doesn't exist or any communication error to
   *     the service.
   */
  public ExpirationRes getCertExpirationDate(CwsRequest req) throws IOException {
    return exec(certWebService.expirationDate(req));
  }

  /**
   * Returns the date which a certificate will expire.
   *
   * @param commonName cert common name
   * @param teamDLName The team DL, which can be absolute/relative to the base {@link #teamDL()}.
   * @return {@link LocalDateTime} which the cert will expire..
   * @throws IOException throws if certificate/PolicyDN doesn't exist or any communication error to
   *     the service.
   */
  public LocalDateTime getCertExpirationDate(String commonName, String teamDLName)
      throws IOException {
    CwsRequest req =
        CwsRequest.builder()
            .appId(appId())
            .teamDL(normalizeTeamDL(teamDLName))
            .commonName(commonName)
            .build();
    return getCertExpirationDate(req).expirationDate();
  }

  /**
   * Returns the serial number of a certificate.
   *
   * @param req {@link CwsRequest}
   * @return {@link SerialNumberRes}
   * @throws IOException throws if certificate/PolicyDN doesn't exist or any communication error to
   *     the service.
   */
  public SerialNumberRes getCertSerialNumber(CwsRequest req) throws IOException {
    return exec(certWebService.serialNumber(req));
  }

  /**
   * Returns the serial number of a certificate..
   *
   * @param commonName cert common name
   * @param teamDLName The team DL, which can be absolute/relative to the base {@link #teamDL()}.
   * @return Serial number of a certificate
   * @throws IOException throws if certificate/PolicyDN doesn't exist or any communication error to
   *     the service.
   */
  public String getCertSerialNumber(String commonName, String teamDLName) throws IOException {
    CwsRequest req =
        CwsRequest.builder()
            .appId(appId())
            .teamDL(normalizeTeamDL(teamDLName))
            .commonName(commonName)
            .build();
    return getCertSerialNumber(req).certSerialNumber();
  }

  /**
   * Returns whether a certificate will expire within a given number of days.
   *
   * @param req {@link ExpiringReq}
   * @return {@link ExpiringRes}
   * @throws IOException throws if certificate/PolicyDN doesn't exist or any communication error to
   *     the service.
   */
  public ExpiringRes certExpiring(ExpiringReq req) throws IOException {
    return exec(certWebService.expiring(req));
  }

  /**
   * Returns whether a certificate will expire within a given number of days.
   *
   * @param commonName cert common name
   * @param teamDLName The team DL, which can be absolute/relative to the base {@link #teamDL()}.
   * @param days Number used to check if a certificate will expire within that number of days.
   * @return <code>true</code> if cert expires within the given number of days.
   * @throws IOException throws if certificate/PolicyDN doesn't exist or any communication error to
   *     the service.
   */
  public boolean certExpiring(String commonName, String teamDLName, int days) throws IOException {
    ExpiringReq req =
        ExpiringReq.builder()
            .appId(appId())
            .teamDL(normalizeTeamDL(teamDLName))
            .commonName(commonName)
            .expirationWindow(String.valueOf(days))
            .build();
    return certExpiring(req).certificateExpiring();
  }

  /**
   * Checks whether the certificate exists.
   *
   * @param req {@link CwsRequest}
   * @return {@link ExistsRes}
   * @throws IOException throws if PolicyDN doesn't exist or any communication error to the service.
   */
  public ExistsRes certExists(CwsRequest req) throws IOException {
    return exec(certWebService.exists(req));
  }

  /**
   * Checks whether the certificate with given common name exists.
   *
   * @param commonName cert common name
   * @param teamDLName The team DL, which can be absolute/relative to the base {@link #teamDL()}.
   * @return <code>true</code> if cert exists, else return <code>false</code>.
   * @throws IOException throws if PolicyDN doesn't exist or any communication error to the service.
   */
  public boolean certExists(String commonName, String teamDLName) throws IOException {
    CwsRequest req =
        CwsRequest.builder()
            .appId(appId())
            .teamDL(normalizeTeamDL(teamDLName))
            .commonName(commonName)
            .build();
    return certExists(req).certificateExists();
  }

  /**
   * Returns information about the certificate if it exists.
   *
   * @param req {@link CwsRequest}
   * @return {@link ViewRes}
   * @throws IOException throws if certificate/PolicyDN doesn't exist or any communication error to
   *     the service.
   */
  public ViewRes viewCert(CwsRequest req) throws IOException {
    return exec(certWebService.view(req));
  }

  /**
   * Returns information about the given certificate if it exists.
   *
   * @param commonName cert common name
   * @param teamDLName The team DL, which can be absolute/relative to the base {@link #teamDL()}.
   * @return {@link ViewRes}
   * @throws IOException throws if PolicyDN doesn't exist or any communication error to the service.
   */
  public ViewRes viewCert(String commonName, String teamDLName) throws IOException {
    CwsRequest req =
        CwsRequest.builder()
            .appId(appId())
            .teamDL(normalizeTeamDL(teamDLName))
            .commonName(commonName)
            .build();
    return viewCert(req);
  }

  /**
   * Revoke and disables certificate under a certain team DL.
   *
   * @param req {@link RevokeReq}
   * @return {@link RevokeRes}
   * @throws IOException throws if certificate/PolicyDN doesn't exist or any communication error to
   *     the service.
   */
  public RevokeRes revokeCert(RevokeReq req) throws IOException {
    return exec(certWebService.revoke(req));
  }

  /**
   * Revokes certificate under a certain team DL.
   *
   * @param commonName cert common name
   * @param teamDLName The team DL, which can be absolute/relative to the base {@link #teamDL()}.
   * @param reason Reason for the revoke.
   * @param disable Also disable the certificate.
   * @return {@link ViewRes}
   * @throws IOException throws if PolicyDN doesn't exist or any communication error to the service.
   */
  public RevokeRes revokeCert(
      String commonName, String teamDLName, RevokeReason reason, boolean disable)
      throws IOException {
    RevokeReq req =
        RevokeReq.builder()
            .appId(appId())
            .teamDL(normalizeTeamDL(teamDLName))
            .commonName(commonName)
            .reason(reason)
            .disable(disable)
            .build();
    return revokeCert(req);
  }
}
