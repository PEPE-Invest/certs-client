package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import javax.annotation.Nullable;

@AutoValue
public abstract class ExpiringRes extends GenericResponse {

  @Nullable
  @Json(name = "CertificateExpiring")
  public abstract Boolean certificateExpiring();

  public static JsonAdapter<ExpiringRes> jsonAdapter(Moshi moshi) {
    return new AutoValue_ExpiringRes.MoshiJsonAdapter(moshi);
  }
}
