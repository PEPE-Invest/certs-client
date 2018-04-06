package com.oneops.certs.model;

import com.squareup.moshi.Json;
import javax.annotation.Nullable;

/**
 * An abstract CWS success/failure response message.
 *
 * @author Suresh
 */
public abstract class GenericResponse {

  @Json(name = "Success")
  public abstract boolean success();

  @Nullable
  @Json(name = "Error")
  public abstract String error();

  @Nullable
  @Json(name = "ErrorMessage")
  public abstract String errorMsg();
}
