package com.oneops.certs;

/** Cws config. */
public class CwsConfig {

  /** Check if valid CWS env config available for testing. */
  static boolean isValid() {
    return host() != null
        && appId() != null
        && teamDL() != null
        && domain() != null
        && keystore() != null
        && password() != null;
  }

  static String host() {
    return getEnv("cws_host");
  }

  static String appId() {
    return getEnv("cws_app_id");
  }

  static String teamDL() {
    return getEnv("cws_team_dl");
  }

  static String domain() {
    return getEnv("cws_domain");
  }

  static String keystore() {
    return getEnv("cws_keystore");
  }

  static String password() {
    return getEnv("cws_keystore_pass");
  }

  /**
   * Returns the value of given env name by first looking into jvm system property with fall back to
   * system env.
   */
  private static String getEnv(String envName) {
    return System.getProperty(envName, System.getenv(envName));
  }

  private static String getEnv(String envName, String defValue) {
    String env = getEnv(envName);
    return env != null ? env : defValue;
  }

  /** Common error message for invalid env vars. */
  static String errMsg() {
    return "Cert Web Service env config not set.\n"
        + "In order to run the tests, set the following env vars\n"
        + " * cws_host           : CWS endpoint.\n"
        + " * cws_app_id         : CWS App ID.\n"
        + " * cws_team_dl        : CWS base teamDL.\n"
        + " * cws_domain         : Certificate CN base domain.\n"
        + " * cws_keystore       : CWS mTLS keystore.\n"
        + " * cws_keystore_pass  : CWS keystore password.";
  }
}
