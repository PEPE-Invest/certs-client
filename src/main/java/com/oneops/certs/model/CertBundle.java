package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import java.util.Optional;
import javax.annotation.Nullable;

/**
 * A data class to hold the extracted PEM encoded private key, client certificate and cacerts chain.
 */
@AutoValue
public abstract class CertBundle {

  /** PEM encoded certificate private key */
  @Redacted
  public abstract String key();

  /** Private key password (Optional) if the key is encrypted. */
  @Redacted
  public abstract Optional<String> keyPassword();

  /** PEM encoded client certificate. */
  public abstract String cert();

  /** PEM encoded CA cert chain. */
  @Nullable
  public abstract String cacert();

  public static Builder builder() {
    return new AutoValue_CertBundle.Builder();
  }

  /**
   * Creates a new cert bundle with encrypted private key.
   *
   * @param key PEM encoded certificate private key.
   * @param keyPassword private key password.
   * @param cert PEM encoded client certificate.
   * @param cacert PEM encoded CA cert chain.
   * @return {@link CertBundle}
   */
  public static CertBundle create(String key, String keyPassword, String cert, String cacert) {
    return builder().key(key).keyPassword(keyPassword).cert(cert).cacert(cacert).build();
  }

  /**
   * Creates a new cert bundle with un-encrypted private key.
   *
   * @param key PEM encoded certificate private key.
   * @param cert PEM encoded client certificate.
   * @param cacert PEM encoded CA cert chain.
   * @return {@link CertBundle}
   */
  public static CertBundle create(String key, String cert, String cacert) {
    return builder().key(key).cert(cert).cacert(cacert).build();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder key(String key);

    public abstract Builder keyPassword(String keyPassword);

    public abstract Builder cert(String cert);

    public abstract Builder cacert(String cacert);

    public abstract CertBundle build();
  }
}
