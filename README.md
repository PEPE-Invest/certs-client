<img src="docs/images/tls-certificate-64.png" alt="Cert" width=25 height=25> Certificate Web Service Client 
----------
[![Maven Central][maven-svg]][maven-url] [![changelog][cl-svg]][cl-url] [![javadoc][javadoc-svg]][javadoc-url]  

A  java API for Certificate Web Service.


Download
--------

Download [the latest JAR][1] or grab via Maven:
```xml
<dependency>
  <groupId>com.oneops</groupId>
  <artifactId>certs-client</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Examples

#### Initializing CWS Client

```java
CwsClient client = CwsClient.builder()
            .endPoint("Api Endpoint")
            .appId("App ID")
            .teamDL("Base Team DL")
            .keystore("Keystore Path")
            .storePassword("Keystore password")
            .build();
```
For loading the keystore from classpath use, `classpath:/<your/cws/keystore/path>.p12`

#### Create new cert

```java
String cn = "test1.domain.com" ;
String teamDL = "test-teamDL"; // Relative to Base TeamDL.
List<String> sans = Arrays.asList("app1.domain.com","app2.domain.com");
    
String certName = client.createCert(cn,sans, teamDL);
```

#### Check cert exists

```java
boolean exists = client.certExists(cn, teamDL);
```

#### Download cert

```java
String base64Content = client.downloadCert(cn, teamDL, "test1@Eeweweesd", CertFormat.PKCS12);
```

#### Get cert expiration date

```java
LocalDateTime date = client.getCertExpirationDate(cn, teamDL);
```

#### View cert details

```java
ViewRes viewRes = client.viewCert(cn, teamDL);
```


#### Revoke and disable the cert

```java
RevokeRes revokeRes = client.revokeCert(cn, teamDL, RevokeReason.NONE, true);
```

#### Delete cert

```java
client.obsoleteCert(cn, teamDL);
```

## Testing

Set the following env variables and run the `./mvnw clean test` to execute the unit tests.

```bash
 export cws_host=...     
 export cws_app_id=...
 export cws_team_dl=....
 export cws_domain=...
 export cws_keystore=.....p12
 export cws_keystore_pass=....
```

## Dependencies

   - [Retrofit](https://github.com/square/retrofit/)
   - [OkHttp](https://github.com/square/okhttp)
   - [Moshi](https://github.com/square/Moshi/)

License
-------

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



<!-- Badges -->

[1]: https://search.maven.org/remote_content?g=com.oneops&a=certs-client&v=LATEST

[maven-url]: http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.oneops%22%20AND%20a%3A%22certs-client%22
[maven-svg]: https://img.shields.io/maven-central/v/com.oneops/certs-client.svg?label=Maven%20Central&style=flat-square

[cl-url]: https://github.com/oneops/certs-client/blob/master/CHANGELOG.md
[cl-svg]: https://img.shields.io/badge/change--log-latest-blue.svg?style=flat-square

[javadoc-url]: https://oneops.github.io/certs-client/javadocs/
[javadoc-svg]: https://img.shields.io/badge/api--doc-latest-ff69b4.svg.svg?style=flat-square

