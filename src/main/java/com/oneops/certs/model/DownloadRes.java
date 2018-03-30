package com.oneops.certs.model;


import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class DownloadRes {

  public abstract String Success();

  public abstract String CertificateData();

  public static JsonAdapter<DownloadRes> jsonAdapter(Moshi moshi) {
    return new AutoValue_DownloadRes.MoshiJsonAdapter(moshi);
  }

}
