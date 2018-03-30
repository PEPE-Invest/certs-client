package com.oneops.certs.model;


import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class ExpirationRes {

  public abstract String Success();

  @Json(name = "Certificate Expiration Date")
  public abstract String expirationDate();

  public static JsonAdapter<ExpirationRes> jsonAdapter(Moshi moshi) {
    return new AutoValue_ExpirationRes.MoshiJsonAdapter(moshi);
  }

}