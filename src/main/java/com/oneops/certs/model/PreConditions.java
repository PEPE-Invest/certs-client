package com.oneops.certs.model;

/**
 * Static convenience methods to check whether the CWS request was invoked correctly (whether its
 * preconditions have been met) else throws an unchecked exception.
 */
public class PreConditions {

  /**
   * Checks CWS common name.85 chars is the CWS limit for common name. As per the DNS RFC, an FQDN
   * has a size # limit of 255 chars https://tools.ietf.org/html/rfc1035#section-2.3.4
   *
   * @param cn common name
   */
  public static void checkCN(String cn) {
    if (cn.length() > 85) {
      throw new IllegalArgumentException(
          "Common name (" + cn + ") can not be greater than 85 chars");
    }
  }

  /**
   * Checks the CWS password requirements.
   *
   * <p>Regex explanation
   *
   * <pre>
   *
   *   ^                   # start-of-string
   * (?=.*[0-9])           # a digit must occur at least once
   * (?=.*[a-z])           # a lower case letter must occur at least once
   * (?=.*[A-Z])           # an upper case letter must occur at least once
   * (?=.*[@#$%^&amp;+=])  # a special character must occur at least once
   * (?=\S+$)              # no whitespace allowed in the entire string
   * .{11,}                # anything, at least 15 places though
   * $                     # end-of-string
   *
   * </pre>
   *
   * @param password password string.
   */
  public static void checkPassword(String password) {
    if (!password.matches(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[~!@#$%^&*+=])(?=\\S+$).{15,}$")) {
      throw new IllegalArgumentException(
          "Password must be at least 15 chars with upper case and lower case letter, number and special character");
    }
  }
}
