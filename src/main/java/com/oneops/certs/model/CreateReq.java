package com.oneops.certs.model;

import static java.util.Collections.emptyList;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Create certificate request from Certificate Management system.The request supports explicitly
 * providing the certificate fields and letting certificate management system centrally create the
 * key pair and CSR.
 */
@AutoValue
public abstract class CreateReq extends GenericRequest {

  /** An array of subject alternative names for the certificate. */
  public abstract List<String> subjectAltName();

  /**
   * External field’s value can be “0” or “1”. “0” represents internal certificate request and “1”
   * means request is for internet facing certificate.
   */
  public abstract String external();

  /**
   * Domain name for requesting certificate in format like “your-company.com”. Domain field is
   * required when requesting internet facing (external) certificate. It is optional field if
   * requesting internal certificate.
   */
  @Nullable
  public abstract String domain();

  /**
   * This is an optional filed if the key pair has already been created and the csr needs to be
   * signed.
   */
  @Nullable
  public abstract String csr();

  /**
   * This is an optional field which will create a new policy if specified and the policy does not
   * already exist. “1” means to create new policy.
   */
  @Nullable
  public abstract String createPolicy();

  /**
   * Creates a new cert generation request with the given details.
   *
   * @param appId Application id
   * @param commonName Cert common name
   * @param teamDL Team email DL.
   * @param subjectAltName A list of of SAN. Provide an empty list if it's not required.
   * @return {@link CreateReq}
   */
  public static CreateReq create(
      String appId, String commonName, String teamDL, @Nonnull List<String> subjectAltName) {
    return builder()
        .appId(appId)
        .commonName(commonName)
        .teamDL(teamDL)
        .subjectAltName(subjectAltName)
        .build();
  }

  public abstract Builder toBuilder();

  public static Builder builder() {
    return new AutoValue_CreateReq.Builder()
        .external("0")
        .createPolicy("1")
        .subjectAltName(emptyList())
        .verbose(true);
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder appId(String appId);

    public abstract Builder commonName(String commonName);

    public abstract Builder teamDL(String teamDL);

    public abstract Builder subjectAltName(List<String> subjectAltName);

    public abstract Builder external(String external);

    public abstract Builder domain(String domain);

    public abstract Builder csr(String csr);

    public abstract Builder createPolicy(String createPolicy);

    public abstract Builder alternateId(String alternateId);

    public abstract Builder verbose(Boolean verbose);

    abstract CreateReq autoBuild();

    public CreateReq build() {
      CreateReq req = autoBuild();
      PreConditions.checkCN(req.commonName());
      return req;
    }
  }

  public static JsonAdapter<CreateReq> jsonAdapter(Moshi moshi) {
    return new AutoValue_CreateReq.MoshiJsonAdapter(moshi);
  }
}
