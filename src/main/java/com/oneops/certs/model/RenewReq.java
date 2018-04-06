package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import javax.annotation.Nullable;

/**
 * Certs renew request.
 *
 * @author Suresh
 */
@AutoValue
public abstract class RenewReq extends GenericRequest {

  /**
   * This is an optional filed if the key pair has already been created and the csr needs to be
   * signed.
   */
  @Nullable
  public abstract String csr();

  public abstract Builder toBuilder();

  /**
   * Creates a cert renew request with the given details.
   *
   * @param appId Application id
   * @param commonName Cert common name
   * @param teamDL Team email DL.
   * @return {@link RenewReq}
   */
  public static RenewReq create(String appId, String commonName, String teamDL) {
    return builder().appId(appId).commonName(commonName).teamDL(teamDL).build();
  }

  public static Builder builder() {
    return new AutoValue_RenewReq.Builder().verbose(true);
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder appId(String appId);

    public abstract Builder commonName(String commonName);

    public abstract Builder teamDL(String teamDL);

    public abstract Builder csr(String csr);

    public abstract Builder alternateId(String alternateId);

    public abstract Builder verbose(Boolean verbose);

    public abstract RenewReq build();
  }

  public static JsonAdapter<RenewReq> jsonAdapter(Moshi moshi) {
    return new AutoValue_RenewReq.MoshiJsonAdapter(moshi);
  }
}
