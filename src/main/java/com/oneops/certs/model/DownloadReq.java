package com.oneops.certs.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * appId: Application id requesting certificate management system to perform create certificate rest web service call.
 * commonName: Common name of the certificate to renew.
 * teamDL: Team email distribution list set up in the certificate management system, which owns the certificate.
 * format: The format in which the certificate should be returned.
 *
 * Accepted format values:
 *
 * pkcs#12
 * base64
 * der
 * pkcs#7
 * base64pkcs#8
 *
 * includeChain: Field’s value can be “1” or “0”. Value “1” includes the chain in the data and “0” doesn’t include chain in the certificate data.
 * includePrivateKey: Field’s value can be either “1” or “0”. includePrivateKey value “1” includes the private key and “0” value doesn’t include private key with certificate data.
 * password: The password to protect the private key. This field is mandatory if the private key is requested. Password complexity requires between 12 and 100 characters and at least one of each the following:
 * Uppercase character
 * Lowercase character
 * Special character
 * Number
 *
 * Security recommends at least 15 character passwords, uniquely randomly generated for each certificate download.
 *
 * certificateData: Contains the requested certificate in base64 encoded format.
 * alternateId: This is an optional field which can specify who specifically made the request if the appId is owned by a team or group.
 * verbose: This is an optional boolean field, that when set to true will allow for more informative responses.
 */
@AutoValue
public abstract class DownloadReq {

  public abstract String appId();

  public abstract String commonName();

  public abstract String teamDL();

  public abstract String format();

  public abstract String includeChain();

  public abstract String includePrivateKey();

  public abstract String password();

  public static Builder builder() {
    return new AutoValue_DownloadReq.Builder();
  }


  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder appId(String appId);

    public abstract Builder commonName(String commonName);

    public abstract Builder teamDL(String teamDL);

    public abstract Builder format(String format);

    public abstract Builder includeChain(String includeChain);

    public abstract Builder includePrivateKey(String includePrivateKey);

    public abstract Builder password(String password);

    public abstract DownloadReq build();
  }

  public static JsonAdapter<DownloadReq> jsonAdapter(Moshi moshi) {
    return new AutoValue_DownloadReq.MoshiJsonAdapter(moshi);
  }
}
