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

  /** Error code is optional and may not present in all the error responses. */
  @Nullable
  @Json(name = "ErrorCode")
  public abstract Integer errorCode();

  @Nullable
  @Json(name = "ErrorMessage")
  public abstract String errorDetails();

  /**
   * Returns the full error message. This is mainly used in exception and logging.
   *
   * @return full error message string.
   */
  public String errorMsg() {
    StringBuilder buf = new StringBuilder();
    buf.append(error());
    if (errorDetails() != null) {
      buf.append(", ").append(errorDetails());
    }
    return buf.toString();
  }
}
