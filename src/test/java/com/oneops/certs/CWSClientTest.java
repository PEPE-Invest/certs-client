package com.oneops.certs;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.oneops.certs.model.CertFormat;
import com.oneops.certs.model.RevokeReason;
import com.oneops.certs.model.RevokeRes;
import com.oneops.certs.model.ViewRes;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CWS Client Tests")
class CWSClientTest {

  private static CwsClient client;

  @BeforeAll
  static void setUp() {
    assumeTrue(CwsConfig.isValid(), CwsConfig::errMsg);
    client =
        CwsClient.builder()
            .endPoint(CwsConfig.host())
            .appId(CwsConfig.appId())
            .teamDL(CwsConfig.teamDL())
            .keystore(CwsConfig.keystore())
            .storePassword(CwsConfig.password())
            .build();
  }

  @Test
  void certApis() throws IOException, InterruptedException {

    String cn = "tekton-test1.dev." + CwsConfig.domain();
    String teamDL = "dev-1234";
    List<String> sans =
        Arrays.asList(
            "tekton-test-san1.dev." + CwsConfig.domain(),
            "tekton-test-san2.dev." + CwsConfig.domain());

    System.out.println("Creating the policy DN for " + teamDL);
    assertTrue(client.createPolicyDN(""), "Can't create policy for teamDL: " + teamDL);

    if (client.certExists(cn, teamDL)) {
      System.out.println("Cert: " + cn + " exists. Deleting...");
      client.obsoleteCert(cn, teamDL);
    }

    System.out.println("Creating cert: " + cn);
    String cert = client.createCert(cn, sans, teamDL);
    assertNotNull(cert);
    System.out.println("Waiting until cert is available for download...");
    Thread.sleep(45 * 1000);

    System.out.println("Downloading and decoding the cert: " + cn);
    String certContent = client.downloadCert(cn, teamDL, "test1@Eeweweesd", CertFormat.PKCS12);
    assertTrue(
        Base64.getDecoder().decode(certContent).length > 0,
        "Can't decode the downloaded cert content");

    System.out.println("Getting cert expiration date.");
    LocalDateTime date = client.getCertExpirationDate(cn, teamDL);
    assertTrue(
        date.isAfter(LocalDateTime.now().plusDays(30)),
        "Certs expiration date should be > 30 days.");

    System.out.println("Getting cert serial number.");
    String serialNumber = client.getCertSerialNumber(cn, teamDL);
    assertNotNull(serialNumber);

    System.out.println("Checking cert expiry.");
    boolean certExpiring = client.certExpiring(cn, teamDL, 30);
    assertFalse(certExpiring, "By default cert shouldn't expire in 30 days.");

    certExpiring = client.certExpiring(cn, teamDL, 1000);
    assertTrue(certExpiring, "By default cert should expire in 1000 days.");

    System.out.println("Checking cert exists or not.");
    assertTrue(client.certExists(cn, teamDL));
    assertFalse(client.certExists(cn + "xxx", teamDL));

    System.out.println("Viewing cert details.");
    ViewRes viewRes = client.viewCert(cn, teamDL);
    assertTrue(viewRes.certificateName().contains(cn));

    System.out.println("Revoking and disabling the cert: " + cn);
    RevokeRes revokeRes = client.revokeCert(cn, teamDL, RevokeReason.NONE, true);
    assertTrue(revokeRes.requested());

    System.out.println("Deleting the cert: " + cn);
    assertTrue(client.obsoleteCert(cn, teamDL));
    assertThrows(IOException.class, () -> client.viewCert(cn, teamDL));
  }
}
