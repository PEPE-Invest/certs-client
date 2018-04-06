package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import javax.annotation.Nullable;

@AutoValue
public abstract class ViewRes extends GenericResponse {

  @Nullable
  @Json(name = "Certificate Authority")
  public abstract String certificateAuthority();

  @Nullable
  @Json(name = "SubjectAltName(s)")
  public abstract String SubjectAltNames();

  @Nullable
  @Json(name = "Certificate Name")
  public abstract String certificateName();

  public static JsonAdapter<ViewRes> jsonAdapter(Moshi moshi) {
    return new AutoValue_ViewRes.MoshiJsonAdapter(moshi);
  }
}
