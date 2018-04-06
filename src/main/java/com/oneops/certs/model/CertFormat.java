package com.oneops.certs.model;

import com.squareup.moshi.Json;

/**
 * Download certificate format.
 *
 * @author Suresh
 */
public enum CertFormat {
  @Json(name = "pkcs#12")
  PKCS12,

  @Json(name = "base64")
  BASE64,

  @Json(name = "der")
  DER,

  @Json(name = "pkcs#7")
  PKCS7,

  @Json(name = "base64pkcs#8")
  BASE64_PKCS8;
}
