package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import javax.annotation.Nullable;

@AutoValue
public abstract class RevokeRes extends GenericResponse {

  @Nullable
  @Json(name = "Revoked")
  public abstract Boolean revoked();

  @Nullable
  @Json(name = "Requested")
  public abstract Boolean requested();

  public static JsonAdapter<RevokeRes> jsonAdapter(Moshi moshi) {
    return new AutoValue_RevokeRes.MoshiJsonAdapter(moshi);
  }
}
