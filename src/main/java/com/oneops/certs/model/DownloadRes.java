package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import javax.annotation.Nullable;

@AutoValue
public abstract class DownloadRes extends GenericResponse {

  @Nullable
  @Json(name = "CertificateData")
  public abstract String certificateData();

  @Nullable
  @Json(name = "Filename")
  public abstract String fileName();

  public static JsonAdapter<DownloadRes> jsonAdapter(Moshi moshi) {
    return new AutoValue_DownloadRes.MoshiJsonAdapter(moshi);
  }
}
