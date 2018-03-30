package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class Error {

  public abstract String Success();

  public abstract String Error();

  public static Error create(String Success, String Error) {
    return new AutoValue_Error(Success, Error);
  }

  public static JsonAdapter<Error> jsonAdapter(Moshi moshi) {
    return new AutoValue_Error.MoshiJsonAdapter(moshi);
  }
}
