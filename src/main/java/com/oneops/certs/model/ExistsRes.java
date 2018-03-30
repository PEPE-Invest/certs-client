package com.oneops.certs.model;


import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class ExistsRes {

  public abstract String Success();

  public abstract String CertificateExists();


  public static JsonAdapter<ExistsRes> jsonAdapter(Moshi moshi) {
    return new AutoValue_ExistsRes.MoshiJsonAdapter(moshi);
  }

}