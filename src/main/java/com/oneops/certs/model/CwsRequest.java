package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * The common request type for certificate web service
 *
 * @author Suresh
 */
@AutoValue
public abstract class CwsRequest extends GenericRequest {

  public abstract Builder toBuilder();

  public static Builder builder() {
    return new AutoValue_CwsRequest.Builder().verbose(true);
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder appId(String appId);

    public abstract Builder commonName(String commonName);

    public abstract Builder teamDL(String teamDL);

    public abstract Builder alternateId(String alternateId);

    public abstract Builder verbose(Boolean verbose);

    public abstract CwsRequest build();
  }

  public static JsonAdapter<CwsRequest> jsonAdapter(Moshi moshi) {
    return new AutoValue_CwsRequest.MoshiJsonAdapter(moshi);
  }
}
