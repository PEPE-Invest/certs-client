package com.oneops.certs;

import com.oneops.certs.model.GenericResponse;
import java.io.IOException;
import javax.annotation.Nonnull;

/**
 * Exception thrown for any certificate web service or i/o errors.
 *
 * @author Suresh
 */
public class CwsException extends IOException {

  /**
   * Creates Cws exception from the failed {@link GenericResponse}
   *
   * @param err Cws failed response.
   */
  public CwsException(@Nonnull GenericResponse err) {
    super(err.errorMsg());
  }

  public CwsException(String message) {
    super(message);
  }

  public CwsException(String message, Throwable cause) {
    super(message, cause);
  }
}
