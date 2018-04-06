package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * Certificate expiring request.
 *
 * @author Suresh
 */
@AutoValue
public abstract class ExpiringReq extends GenericRequest {

  /** Number used to check if a certificate will expire within that number of days. */
  public abstract String expirationWindow();

  public abstract Builder toBuilder();

  public static Builder builder() {
    return new AutoValue_ExpiringReq.Builder().verbose(true);
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder appId(String appId);

    public abstract Builder commonName(String commonName);

    public abstract Builder teamDL(String teamDL);

    public abstract Builder expirationWindow(String expirationWindow);

    public abstract Builder alternateId(String alternateId);

    public abstract Builder verbose(Boolean verbose);

    public abstract ExpiringReq build();
  }

  public static JsonAdapter<ExpiringReq> jsonAdapter(Moshi moshi) {
    return new AutoValue_ExpiringReq.MoshiJsonAdapter(moshi);
  }
}
