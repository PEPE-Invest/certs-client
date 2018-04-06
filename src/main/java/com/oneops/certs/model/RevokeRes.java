package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class RevokeRes {

  public abstract String Success();

  public abstract String Revoked();

  public abstract String Requested();

  public static JsonAdapter<RevokeRes> jsonAdapter(Moshi moshi) {
    return new AutoValue_RevokeRes.MoshiJsonAdapter(moshi);
  }

  public static Builder builder() {
    return new AutoValue_RevokeRes.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder Success(String Success);

    public abstract Builder Revoked(String Revoked);

    public abstract Builder Requested(String Requested);

    public abstract RevokeRes build();
  }
}
