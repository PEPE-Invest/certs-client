package com.oneops.certs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.oneops.certs.model.CertBundle;
import com.oneops.certs.model.CertFormat;
import com.oneops.certs.model.RevokeReason;
import com.oneops.certs.model.RevokeRes;
import com.oneops.certs.model.ViewRes;
import com.oneops.certs.security.PasswordGen;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
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
            .keystorePassword(CwsConfig.password())
            .debug(false)
            .build();
  }

  @Test
  void certApis() throws Exception {

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

    System.out.println("Downloading and decoding the cert: " + cn);
    String ksPasswd = PasswordGen.builder().build().generate(20);
    String certContent = client.downloadCert(cn, teamDL, ksPasswd, CertFormat.PKCS12);
    assertTrue(
        Base64.getDecoder().decode(certContent).length > 0,
        "Can't decode the downloaded cert content");

    System.out.println("Downloading cert bundle with encrypted private key for " + cn);
    CertBundle certBundle = client.downloadCert(cn, teamDL, Optional.of("test"));
    System.out.println(certBundle);
    assertNotNull(certBundle.cacert());
    assertNotNull(certBundle.key());
    assertEquals(Optional.of("test"), certBundle.keyPassword());

    System.out.println("Downloading cert bundle with un-encrypted private key for " + cn);
    certBundle = client.downloadCert(cn, teamDL, Optional.empty());
    assertNotNull(certBundle.key());
    assertEquals(Optional.empty(), certBundle.keyPassword());

    System.out.println("Downloading an invalid cert.");
    assertThrows(
        CwsException.class, () -> client.downloadCert(cn + "xxxx", teamDL, Optional.empty()));

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
