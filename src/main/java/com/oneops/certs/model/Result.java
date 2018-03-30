package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.lang.reflect.Type;


@AutoValue
public abstract class Result<T> {

  public abstract T body();

  public abstract Error err();

  public abstract boolean isSuccessful();

  public static <T> Result<T> create(T body, Error err, boolean isSuccessful) {
    return Result.<T>builder()
        .body(body)
        .err(err)
        .isSuccessful(isSuccessful)
        .build();
  }


  public static <T> Builder<T> builder() {
    return (Builder<T>) new AutoValue_Result.Builder<>();
  }


  @AutoValue.Builder
  public abstract static class Builder<T> {

    public abstract Builder<T> body(T body);

    public abstract Builder<T> err(Error err);

    public abstract Builder<T> isSuccessful(boolean isSuccessful);

    public abstract Result<T> build();
  }

  /**
   * Json adapter for {@link Result} type, used by Moshi for JSON [de]serialization.
   */
  public static <T> JsonAdapter<Result<T>> jsonAdapter(Moshi moshi, Type[] types) {
    return new AutoValue_Result.MoshiJsonAdapter<>(moshi, types);
  }
}
