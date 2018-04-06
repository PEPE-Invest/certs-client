package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import javax.annotation.Nullable;

@AutoValue
public abstract class SerialNumberRes extends GenericResponse {

  @Nullable
  @Json(name = "Certificate Serial Number")
  public abstract String certSerialNumber();

  public static JsonAdapter<SerialNumberRes> jsonAdapter(Moshi moshi) {
    return new AutoValue_SerialNumberRes.MoshiJsonAdapter(moshi);
  }
}
