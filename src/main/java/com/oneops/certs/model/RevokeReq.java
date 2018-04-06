package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import javax.annotation.Nullable;

/**
 * Certificate revocation request.
 *
 * @author Suresh
 */
@AutoValue
public abstract class RevokeReq extends GenericRequest {

  @Nullable
  public abstract Boolean disable();

  @Nullable
  public abstract RevokeReason reason();

  public abstract Builder toBuilder();

  public static Builder builder() {
    return new AutoValue_RevokeReq.Builder().verbose(true).disable(true).reason(RevokeReason.NONE);
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder appId(String appId);

    public abstract Builder commonName(String commonName);

    public abstract Builder teamDL(String teamDL);

    public abstract Builder alternateId(String alternateId);

    public abstract Builder verbose(Boolean verbose);

    public abstract Builder disable(Boolean disable);

    public abstract Builder reason(RevokeReason reason);

    public abstract RevokeReq build();
  }

  public static JsonAdapter<RevokeReq> jsonAdapter(Moshi moshi) {
    return new AutoValue_RevokeReq.MoshiJsonAdapter(moshi);
  }
}
