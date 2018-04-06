package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * The common response type from certificate web service
 *
 * @author Suresh
 */
@AutoValue
public abstract class CwsResponse extends GenericResponse {

  public static JsonAdapter<CwsResponse> jsonAdapter(Moshi moshi) {
    return new AutoValue_CwsResponse.MoshiJsonAdapter(moshi);
  }
}
