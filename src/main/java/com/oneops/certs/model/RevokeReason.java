package com.oneops.certs.model;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

/**
 * Reasons for the cert revoke.
 *
 * @author Suresh
 */
public enum RevokeReason {
  NONE(0),
  USER_KEY_COMPROMISED(1),
  CA_KEY_COMPROMISED(2),
  USER_CHANGED_AFFILIATION(3),
  CERTIFICATE_SUPERSEDED(4),
  ORIGINAL_USE_NO_LONGER_VALID(5);

  private final int code;

  RevokeReason(int code) {
    this.code = code;
  }

  /** Integer value giving the reason for the revoke. */
  public int code() {
    return code;
  }

  /** Moshi JSON adapter for {@link RevokeReason} enum. */
  public static class Adapter {

    @ToJson
    int toJson(RevokeReason reason) {
      return reason.code();
    }

    @FromJson
    RevokeReason fromJson(int code) {
      return RevokeReason.values()[code];
    }
  }
}
