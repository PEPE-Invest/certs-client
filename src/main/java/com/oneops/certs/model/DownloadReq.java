package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import javax.annotation.Nullable;

/** Download certificate request. */
@AutoValue
public abstract class DownloadReq extends GenericRequest {

  /**
   * The format in which the certificate should be returned.
   *
   * <pre>
   *   Accepted format values:
   *
   *   - pkcs#12
   *   - base64
   *   - der
   *   - pkcs#7
   *   - base64pkcs#8
   * </pre>
   */
  public abstract CertFormat format();

  /**
   * Field’s value can be “1” or “0”. Value “1” includes the chain in the data and “0” doesn’t
   * include chain in the certificate data.
   */
  public abstract String includeChain();

  /**
   * Field’s value can be either “1” or “0”. includePrivateKey value “1” includes the private key
   * and “0” value doesn’t include private key with certificate data.
   */
  public abstract String includePrivateKey();

  /**
   * The password to protect the private key. This field is mandatory if the private key is
   * requested. Password complexity requires between 12 and 100 characters and at least one of each
   * the following:
   *
   * <pre>
   *
   *   - Uppercase character
   *   - Lowercase character
   *   - Special character
   *   - Number
   * </pre>
   *
   * Note: Security recommends at least 15 character passwords, uniquely randomly generated for each
   * certificate download.
   */
  @Redacted
  public abstract String password();

  /** Contains the requested certificate in BASE64 encoded format. */
  @Nullable
  public abstract String certificateData();

  /**
   * This is an optional field which can specify who specifically made the request if the appId is
   * owned by a team or group.
   */
  @Nullable
  public abstract String alternateId();

  /**
   * This is an optional boolean field, that when set to true will allow for more informative
   * responses.
   */
  @Nullable
  public abstract Boolean verbose();

  public abstract Builder toBuilder();

  public static Builder builder() {
    return new AutoValue_DownloadReq.Builder()
        .format(CertFormat.PKCS12)
        .includeChain("1")
        .includePrivateKey("1")
        .verbose(true);
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder appId(String appId);

    public abstract Builder commonName(String commonName);

    public abstract Builder teamDL(String teamDL);

    public abstract Builder format(CertFormat format);

    public abstract Builder includeChain(String includeChain);

    public abstract Builder includePrivateKey(String includePrivateKey);

    public abstract Builder password(String password);

    public abstract Builder certificateData(String certificateData);

    public abstract Builder alternateId(String alternateId);

    public abstract Builder verbose(Boolean verbose);

    abstract DownloadReq autoBuild();

    public DownloadReq build() {
      DownloadReq req = autoBuild();
      PreConditions.checkPassword(req.password());
      return req;
    }
  }

  public static JsonAdapter<DownloadReq> jsonAdapter(Moshi moshi) {
    return new AutoValue_DownloadReq.MoshiJsonAdapter(moshi);
  }
}
