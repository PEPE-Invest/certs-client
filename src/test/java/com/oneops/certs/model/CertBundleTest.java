package com.oneops.certs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class CertBundleTest {

  @Test
  void create() {

    CertBundle certBundle = CertBundle.create("key", "cert", "cacert");
    assertNotNull(certBundle);

    certBundle = CertBundle.builder().key("key").cert("cert").cacert("cacert").build();
    assertEquals(Optional.empty(), certBundle.keyPassword());

    certBundle = CertBundle.create("key", Optional.of("pass"), "cert", "cacert");
    assertEquals(certBundle.keyPassword(), Optional.of("pass"));
  }
}
