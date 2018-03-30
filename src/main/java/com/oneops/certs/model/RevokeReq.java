package com.oneops.certs.model;


import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * appId: Application id requesting certificate management system to perform create certificate rest web service call.
 * commonName: Common name of the certificate to renew.
 * teamDL: Team email distribution list set up in the certificate management system, which owns the certificate.
 * disable: (Optional) Boolean value determining whether to disable the certificate.
 * reason: (Optional) Integer value giving the reason for the revoke.
 *
 * Accepted format values:
 *
 * 0 = None
 * 1 = User key compromised
 * 2 = CA key compromised
 * 3 = User changed affiliation
 * 4 = Certificate superseded
 * 5 = Original use no longer valid
 *
 * verbose: This is an optional boolean field, that when set to true will allow for more informative responses.
 */
@AutoValue
public abstract class RevokeReq {

  public abstract String appId();

  public abstract String commonName();

  public abstract String teamDL();

  public abstract String diable();

  public abstract String reason();

  public static Builder builder() {
    return new AutoValue_RevokeReq.Builder();
  }


  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder appId(String appId);

    public abstract Builder commonName(String commonName);

    public abstract Builder teamDL(String teamDL);

    public abstract Builder diable(String diable);

    public abstract Builder reason(String reason);

    public abstract RevokeReq build();
  }

  public static JsonAdapter<RevokeReq> jsonAdapter(Moshi moshi) {
    return new AutoValue_RevokeReq.MoshiJsonAdapter(moshi);
  }
}
