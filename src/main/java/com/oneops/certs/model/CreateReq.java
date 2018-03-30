package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.List;

/**
 * appId: Application id requesting certificate management system to perform create certificate rest web service call.
 * commonName: Common name of the certificate to create.
 * teamDL: Team email distribution list set up in the certificate management system, which owns the certificate.
 * subjectAltName: An array of subject alternative names for the certificate.
 * external: External field’s value can be “0” or “1”.  “0” represents internal certificate request and “1” means request is for internet facing certificate.
 * domain: Domain name for requesting certificate in format like “walmart.com” or “samsclub.com”.  Domain field is required when requesting internet facing (external) certificate. It is optional field if requesting internal certificate.
 * csr: This is an optional filed if the key pair has already been created and the csr needs to be signed.
 * createPolicy: This is an optional field which will create a new policy if specified and the policy does not already exist.
 * alternateId: This is an optional field which can specify who specifically made the request if the appId is owned by a team or group.
 * verbose: This is an optional boolean field, that when set to true will allow for more informative responses.
 */
@AutoValue
public abstract class CreateReq {

  public abstract String appId();

  public abstract String commonName();

  public abstract String teamDL();

  public abstract List<String> subjectAltName();

  public abstract String external();

  public abstract String domain();

  public abstract String alternateId();

  public abstract boolean verbose();

  public static Builder builder() {
    return new AutoValue_CreateReq.Builder();
  }


  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder appId(String appId);

    public abstract Builder commonName(String commonName);

    public abstract Builder teamDL(String teamDL);

    public abstract Builder subjectAltName(List<String> subjectAltName);

    public abstract Builder external(String external);

    public abstract Builder domain(String domain);

    public abstract Builder alternateId(String alternateId);

    public abstract Builder verbose(boolean verbose);

    public abstract CreateReq build();
  }


  public static JsonAdapter<CreateReq> jsonAdapter(Moshi moshi) {
    return new AutoValue_CreateReq.MoshiJsonAdapter(moshi);
  }
}
