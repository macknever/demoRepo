package com.lawrence.greenmail;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.vertx.core.json.JsonObject;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;

class GreenMailIT {
    private static final Logger LOG = LoggerFactory.getLogger(GreenMailIT.class);

    private static final String APP_URL = "http://localhost:8181/api/report";
    private static final String MINIKUBE_IP = "192.168.59.100";
    private static final int IMAP_PORT = 30143;

    private static final String NOT_ALLOWED_DOMAIN_TO_1 = "non@coldmail.org";
    private static final String NOT_ALLOWED_DOMAIN_TO_2 = "lawrence.l@globalrelay.net";

    private static final String DELIVERABLE_ADDRESS = "nucleusgreenmailtest@gmail.com";
    private static final String NON_EXISTENT_ADDRESS_1 = "non-existent-gmail-address1@gmail.com";
    private static final String NON_EXISTENT_ADDRESS_2 = "non-existent-gmail-address2@gmail.com";

    private static final String VALID_FROM = "lawrence.li@globalrelay.net";
    private static final String EXIST_REMOTE_FROM = "macknever@gmail.com";
    private static final String FAKE_DOMAIN_FROM = "non-existent-from-email-9999@coldmail.org";
    private static final String MAL_FORMAT_ADDRESS = "LEVON";
    private static final String NON_EX_IN_HOUSE_DOMAIN_FROM = "non-existent-gmail-address@globalrelay.net";

    private static final String VALID_CC = String.join(",", List.of(DELIVERABLE_ADDRESS));
    private static final String INVALID_CC = String.join(",",
            List.of(NOT_ALLOWED_DOMAIN_TO_1, NOT_ALLOWED_DOMAIN_TO_1));
    private static final String HALF_VALID_CC = String.join(",",
            List.of(DELIVERABLE_ADDRESS, NOT_ALLOWED_DOMAIN_TO_1));

    private static final int VALID_FILE_SIZE = 0;
    private static final int INVALID_FILE_SIZE = 22020096; //21 MB
    private static final int INVALID_GMAIL_FILE_SIZE = 524200000; //500 mb

    private static final int OVER_SIZE_FILES = 524288001; // smart host limit 512 MB 524288000 bytes

    private static GreenMailUtil mailUtil;

    @BeforeAll
    static void setup() {
        mailUtil = new GreenMailUtil(MINIKUBE_IP, IMAP_PORT);
    }

    @Test
    void testReportSubmissionAndEmailDelivery() throws Exception {
        String recipient = "lawrence.li@globalrelay.net";
        String uniqueSubject = "Perf Report " + System.currentTimeMillis();
        String bodyText = "The soak test finished with 0% error rate.";
        String title = this.getClass().getSimpleName() + System.currentTimeMillis();

        final String requestBody = compose(VALID_FROM, VALID_FROM, NOT_ALLOWED_DOMAIN_TO_1, uniqueSubject, title, bodyText,
                null, 0);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(APP_URL)
                .then()
                .statusCode(250);

        String actualBody = mailUtil.getEmailBody(recipient, "password", uniqueSubject);

        LOG.info("Verified Email Body: {}", actualBody);

        assertThat(actualBody).contains(bodyText);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("providesRecipientAndResponse")
    void testSmartHost(String desc, String bounceFrom, String from, String to, String cc, int size, int statusCode)
            throws Exception {
        String bodyText = desc + System.currentTimeMillis();

        final String req = compose(bounceFrom, from, to, desc, desc, bodyText, cc, size);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(req)
                .when()
                .post(APP_URL)
                .then()
                .statusCode(statusCode)
                .extract().body().asString();
    }

    private static Stream<Arguments> providesRecipientAndResponse() {
        return Stream.of(
                Arguments.of("550 MAIL FROM: NON-EX, REMOTE DOMAIN",
                        EXIST_REMOTE_FROM, EXIST_REMOTE_FROM, DELIVERABLE_ADDRESS, VALID_CC,
                        VALID_FILE_SIZE, 550),
                Arguments.of("501 MAIL FROM: MAL-FORMAT",
                        MAL_FORMAT_ADDRESS, MAL_FORMAT_ADDRESS, DELIVERABLE_ADDRESS, VALID_CC, VALID_FILE_SIZE, 421),
                Arguments.of("550 MAIL FROM: EXISTING REMOTE DOMAIN",
                        EXIST_REMOTE_FROM, EXIST_REMOTE_FROM, DELIVERABLE_ADDRESS, VALID_CC, VALID_FILE_SIZE, 550),
                Arguments.of("550 MAIL FROM: FAKE DOMAIN",
                        FAKE_DOMAIN_FROM, FAKE_DOMAIN_FROM, DELIVERABLE_ADDRESS, VALID_CC, VALID_FILE_SIZE, 550),
                Arguments.of(" 250 MAIL FROM: NON-EX, IN-HOUSE DOMAIN",
                        NON_EX_IN_HOUSE_DOMAIN_FROM, NON_EX_IN_HOUSE_DOMAIN_FROM, DELIVERABLE_ADDRESS, VALID_CC,
                        VALID_FILE_SIZE, 250),

                Arguments.of("550/503 RCPT TO: NOT ALLOWED DOMAIN",
                        VALID_FROM, VALID_FROM, NOT_ALLOWED_DOMAIN_TO_1, null, VALID_FILE_SIZE, 503),

                Arguments.of("501 RCPT TO: MAL-FORMATTED",
                        VALID_FROM, VALID_FROM, MAL_FORMAT_ADDRESS, null, VALID_FILE_SIZE, 421),
                Arguments.of("250 RCPT TO: ALLOWED DOMAIN",
                        VALID_FROM, VALID_FROM, DELIVERABLE_ADDRESS, null, VALID_FILE_SIZE, 250),
                Arguments.of("250 RCPT TO: NON-EX, ALLOWED DOMAIN",
                        VALID_FROM, VALID_FROM, NON_EXISTENT_ADDRESS_1, null, VALID_FILE_SIZE, 250),

                Arguments.of("501 RCPT TO: ANY one MAL-FORMAT",
                        VALID_FROM, VALID_FROM, MAL_FORMAT_ADDRESS, VALID_CC, VALID_FILE_SIZE, 421),

                Arguments.of("250 RCPT TO: NO MAL, ONE or More is ALLOWED DOMAIN",
                        VALID_FROM, VALID_FROM, NOT_ALLOWED_DOMAIN_TO_1, VALID_CC, VALID_FILE_SIZE, 250),
                Arguments.of("503 RCPT TO: ALL NOT-ALLOWED DOMAIN",
                        VALID_FROM, VALID_FROM, NOT_ALLOWED_DOMAIN_TO_1, INVALID_CC, VALID_FILE_SIZE, 503),

                Arguments.of("OVER SIZE: OVER SMART HOST LIMIT",
                        VALID_FROM, VALID_FROM, DELIVERABLE_ADDRESS, VALID_CC, OVER_SIZE_FILES, 421),
                Arguments.of("250 OVER SIZE: OVER RECIPIENT LIMIT",
                        VALID_FROM, VALID_FROM, DELIVERABLE_ADDRESS, VALID_CC, INVALID_FILE_SIZE, 250)

        );
    }

    private String compose(String bounceFrom, String from, String to, String subject, String title, String body,
            String cc, int size) {
        return new JsonObject()
                .put("bounceFrom", bounceFrom)
                .put("from", from)
                .put("to", to)
                .put("subject", subject)
                .put("title", title)
                .put("body", body)
                .put("cc", cc)
                .put("size", size)
                .encode();
    }
}