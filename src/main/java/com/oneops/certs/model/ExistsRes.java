package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import javax.annotation.Nullable;

@AutoValue
public abstract class ExistsRes extends GenericResponse {

  @Nullable
  @Json(name = "CertificateExists")
  public abstract Boolean certificateExists();

  public static JsonAdapter<ExistsRes> jsonAdapter(Moshi moshi) {
    return new AutoValue_ExistsRes.MoshiJsonAdapter(moshi);
  }
}
