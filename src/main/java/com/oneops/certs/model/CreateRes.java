package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import javax.annotation.Nullable;

/**
 * Creating an internal/internet facing certificate response.
 *
 * @author Suresh
 */
@AutoValue
public abstract class CreateRes extends GenericResponse {

  @Nullable
  @Json(name = "CertificateDN")
  public abstract String certificateDN();

  @Nullable
  @Json(name = "CertificateExists")
  public abstract Boolean certificateExists();

  public static JsonAdapter<CreateRes> jsonAdapter(Moshi moshi) {
    return new AutoValue_CreateRes.MoshiJsonAdapter(moshi);
  }
}
