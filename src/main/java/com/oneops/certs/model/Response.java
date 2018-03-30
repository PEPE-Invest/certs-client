package com.oneops.certs.model;


import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class Response {

  public abstract String Success();

  public static Response create(String Success) {
    return new AutoValue_Response(Success);
  }

  public static JsonAdapter<Response> jsonAdapter(Moshi moshi) {
    return new AutoValue_Response.MoshiJsonAdapter(moshi);
  }
}
