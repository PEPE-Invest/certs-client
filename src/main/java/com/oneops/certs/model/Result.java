package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.lang.reflect.Type;
import javax.annotation.Nullable;

/**
 * Result is kind of an "Either" construct (a disjoint union) to return <b>T</b> or {@link Error}
 * depending on the CWS response.
 */
@AutoValue
public abstract class Result<T> {

  public abstract T body();

  @Nullable
  public abstract Throwable error();

  public abstract boolean success();

  public static <T> Result<T> create(T body, Throwable error, boolean success) {
    return Result.<T>builder().body(body).error(error).success(success).build();
  }

  public static <T> Builder<T> builder() {
    return new AutoValue_Result.Builder<T>();
  }

  @AutoValue.Builder
  public abstract static class Builder<T> {

    public abstract Builder<T> body(T body);

    public abstract Builder<T> error(Throwable error);

    public abstract Builder<T> success(boolean success);

    public abstract Result<T> build();
  }

  /** Json adapter for {@link Result} type, used by Moshi for JSON [de]serialization. */
  public static <T> JsonAdapter<Result<T>> jsonAdapter(Moshi moshi, Type[] types) {
    return new AutoValue_Result.MoshiJsonAdapter<>(moshi, types);
  }
}
