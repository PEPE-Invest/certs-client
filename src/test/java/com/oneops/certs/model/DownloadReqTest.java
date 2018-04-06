package com.oneops.certs.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class DownloadReqTest {

  @Test
  void create() {
    DownloadReq req =
        DownloadReq.builder()
            .appId("testAppId")
            .teamDL("testTeamDL")
            .commonName("testapp.mycompany.com")
            .format(CertFormat.BASE64)
            .password("@j2o@#JOwel")
            .build();
    assertNotNull(req);
  }
}
