package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import javax.annotation.Nullable;

/**
 * A data class to hold the extracted PEM encoded private key, client certificate and cacerts chain.
 */
@AutoValue
public abstract class CertBundle {

  /** PEM encoded certificate private key */
  @Redacted
  public abstract String key();

  /** Private key password */
  @Redacted
  public abstract String keyPassword();

  /** PEM encoded client certificate. */
  public abstract String cert();

  /** PEM encoded CA cert chain. */
  @Nullable
  public abstract String cacert();

  /**
   * Creates a new cert bundle.
   *
   * @param key PEM encoded certificate private key.
   * @param cert PEM encoded client certificate.
   * @param cacert PEM encoded CA cert chain.
   * @param keyPassword private key password.
   * @return {@link CertBundle}
   */
  public static CertBundle create(String key, String cert, String cacert, String keyPassword) {
    return builder().key(key).cert(cert).cacert(cacert).keyPassword(keyPassword).build();
  }

  public abstract Builder toBuilder();

  public static Builder builder() {
    return new AutoValue_CertBundle.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder key(String key);

    public abstract Builder cert(String cert);

    public abstract Builder cacert(String cacert);

    public abstract Builder keyPassword(String keyPassword);

    public abstract CertBundle build();
  }
}
