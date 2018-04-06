package com.oneops.certs.model;

import javax.annotation.Nullable;

/**
 * An abstract CWS request common to most of the operations.
 *
 * @author Suresh
 */
public abstract class GenericRequest {

  /**
   * Application id requesting certificate management system to perform the certificate rest web
   * service call.
   */
  public abstract String appId();

  /** Common name of the certificate. */
  public abstract String commonName();

  /**
   * Team email distribution list set up in the certificate management system, which owns the
   * certificate.
   */
  public abstract String teamDL();

  /**
   * This is an optional field which can specify who specifically made the request if the appId is
   * owned by a team or group.
   */
  @Nullable
  public abstract String alternateId();

  /**
   * This is an optional boolean field, that when set to true will allow for more informative
   * responses.
   */
  @Nullable
  public abstract Boolean verbose();
}
