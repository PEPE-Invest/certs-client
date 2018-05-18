package com.oneops.certs.model;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class CreateReqTest {

  @Test
  void create() {
    CreateReq req =
        CreateReq.builder()
            .appId("testAppId")
            .teamDL("testTeamDL")
            .commonName("testapp.mycompany.com")
            .build();
    assertNotNull(req);

    req =
        req.toBuilder()
            .subjectAltName(asList("san1.test.com", "san2.test.com"))
            .external("0")
            .domain("test.com")
            .build();
    assertNotNull(req);

    req = CreateReq.create("testAppId", "testapp.mycompany.com", "testTeamDL", emptyList());
    assertNotNull(req);

    // Mac=x common name us 64 chars
    char[] longCN = new char[65];
    Arrays.fill(longCN, 'x');
    assertThrows(
        IllegalArgumentException.class,
        () -> CreateReq.create("testAppId", new String(longCN), "testTeamDL", emptyList()));
  }
}
