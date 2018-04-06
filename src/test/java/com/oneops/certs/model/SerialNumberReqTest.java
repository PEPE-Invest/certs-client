package com.oneops.certs.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class CwsRequestTest {

  @Test
  void create() {
    CwsRequest req =
        CwsRequest.builder()
            .appId("testAppId")
            .teamDL("testTeamDL")
            .commonName("testapp.mycompany.com")
            .build();
    assertNotNull(req);
  }
}
