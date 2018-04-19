package com.oneops.certs.security.pem;

/**
 * Common PEM format labels.
 *
 * @author Suresh
 */
public enum PemLabel {
  PRIVATE_KEY("PRIVATE KEY"),
  PUBLIC_KEY("PUBLIC KEY"),
  CERTIFICATE("CERTIFICATE"),
  CERT_REQUEST("CERTIFICATE REQUEST");

  private final String label;

  PemLabel(String label) {
    this.label = label;
  }

  /**
   * Returns the PEM header label.
   *
   * @return label string.
   */
  public String labelName() {
    return label;
  }
}
