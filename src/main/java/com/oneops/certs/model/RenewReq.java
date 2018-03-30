package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * appId: Application id requesting certificate management system to perform create certificate rest web service call.
 * commonName: Common name of the certificate to renew.
 * teamDL: Team email distribution list set up in the certificate management system, which owns the certificate.
 * csr: This is an optional filed if the key pair has already been created and the csr needs to be signed.
 * alternateId: This is an optional field which can specify who specifically made the request if the appId is owned by a team or group.
 */
@AutoValue
public abstract class RenewReq {

  public abstract String appId();

  public abstract String commonName();

  public abstract String teamDL();

  public static Builder builder() {
    return new AutoValue_RenewReq.Builder();
  }


  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder appId(String appId);

    public abstract Builder commonName(String commonName);

    public abstract Builder teamDL(String teamDL);

    public abstract RenewReq build();
  }

  public static JsonAdapter<RenewReq> jsonAdapter(Moshi moshi) {
    return new AutoValue_RenewReq.MoshiJsonAdapter(moshi);
  }
}
