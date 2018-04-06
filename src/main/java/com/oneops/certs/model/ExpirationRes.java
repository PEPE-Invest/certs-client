package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.time.LocalDateTime;
import javax.annotation.Nullable;

@AutoValue
public abstract class ExpirationRes extends GenericResponse {

  @Nullable
  @Json(name = "Certificate Expiration Date")
  public abstract LocalDateTime expirationDate();

  public static JsonAdapter<ExpirationRes> jsonAdapter(Moshi moshi) {
    return new AutoValue_ExpirationRes.MoshiJsonAdapter(moshi);
  }
}
