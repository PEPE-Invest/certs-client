package com.oneops.certs;


import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;

import com.google.auto.value.AutoValue;
import com.oneops.certs.curl.CurlLoggingInterceptor;
import com.oneops.certs.model.Error;
import com.oneops.certs.model.JsonAdapterFactory;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.ConnectionSpec;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

@AutoValue
public abstract class CertsClient {

  private final Logger log = Logger.getLogger(getClass().getSimpleName());

  public abstract String endPoint();


  public abstract String appId();

  public abstract String teamDL();

  public abstract String domain();

  public abstract byte[] certs();

  /**
   * Checks if TLS certificate validation is enabled for communicating with CWS.
   */
  public abstract boolean tlsVerify();

  /**
   * Enable http curl logging for debugging.
   */
  public abstract boolean debug();

  /**
   * Connection/read/write timeout.
   */
  public abstract int timeout();

  private CertWebService certWebService;

  private Converter<ResponseBody, Error> errResConverter;

  /**
   * Initializes the TLS retrofit client.
   *
   * @throws GeneralSecurityException if any error initializing the TLS context.
   */
  private void init() throws GeneralSecurityException {
    log.info("Initializing " + toString());
    Moshi moshi = new Moshi.Builder()
        .add(JsonAdapterFactory.create())
        .build();

    TrustManager[] trustManagers = getTrustManagers();
    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
    sslContext.init(null, trustManagers, new SecureRandom());
    SSLSocketFactory socketFactory = sslContext.getSocketFactory();

    //String basicCreds = Credentials.basic(userName(), password());
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
                  HttpUrl origUrl = chain.request().url();
                  HttpUrl url =
                      origUrl
                          .newBuilder()
                          .build();
                  Request req =
                      chain
                          .request()
                          .newBuilder()
                          .addHeader("Content-Type", "application/json")
                          // .addHeader("Authorization", basicCreds)
                          .url(url)
                          .build();
                  return chain.proceed(req);
                });

    if (!tlsVerify()) {
      okBuilder.hostnameVerifier((host, session) -> true);
    }

    if (debug()) {
      CurlLoggingInterceptor logIntcp = new CurlLoggingInterceptor(log::info);
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
    errResConverter = retrofit.responseBodyConverter(Error.class, new Annotation[0]);
  }

  /**
   * Returns the trust-store manager. It's uses jdk trust-store if {@link #tlsVerify()} is enabled
   * and can customize using the following environment variables.
   *
   * <p>- javax.net.ssl.trustStore - Default trust-store , - javax.net.ssl.trustStoreType - Default
   * trust-store type, - javax.net.ssl.trustStorePassword - Default trust-store password
   *
   * <p>If the {@link #tlsVerify()} is disabled, it trusts all certs using a custom trust manager.
   *
   * @return trust managers.
   * @throws GeneralSecurityException if any error initializing trust store.
   */
  private TrustManager[] getTrustManagers() throws GeneralSecurityException {

    final TrustManager[] trustMgrs;
    if (tlsVerify()) {
      log.info("Using JDK trust-store for TLS check.");
      TrustManagerFactory trustManagerFactory =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init((KeyStore) null); // Uses JDK trust-store.
      trustMgrs = trustManagerFactory.getTrustManagers();
    } else {
      log.info("Skipping TLS certs verification.");
      trustMgrs =
          new TrustManager[]{
              new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                  return new java.security.cert.X509Certificate[]{};
                }
              }
          };
    }
    return trustMgrs;
  }

  /**
   * Helper method to handle {@link Call} object and return the execution result(s). The error
   * handling is done as per the response content-type.
   *
   * @see <a href="https://ipam.illinois.edu/wapidoc/#error-handling">WAPI error-handling</a>
   */
  private <T> T exec(Call<T> call) throws IOException {
    Response<T> res = call.execute();
    if (res.isSuccessful()) {
      return res.body();
    } else {
      Error err;
      String contentType = res.headers().get("Content-Type");
      if ("application/json".equalsIgnoreCase(contentType)) {
        err = errResConverter.convert(requireNonNull(res.errorBody()));
      } else {
        err = Error.create("Error", "Request failed, " + res.message());
      }
      throw new CWSException(res.message());
    }
  }

  public static Builder builder() {
    return new AutoValue_CertsClient.Builder();
  }


  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder endPoint(String endPoint);

    public abstract Builder appId(String appId);

    public abstract Builder teamDL(String teamDL);

    public abstract Builder domain(String domain);

    public abstract Builder certs(byte[] certs);

    public abstract Builder tlsVerify(boolean tlsVerify);

    public abstract Builder debug(boolean debug);

    public abstract Builder timeout(int timeout);

    public abstract CertsClient build();
  }

  public CertWebService service() {
    return certWebService;
  }

}
