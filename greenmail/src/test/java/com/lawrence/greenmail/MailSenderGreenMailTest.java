package com.lawrence.greenmail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.lawrence.greenmail.model.ReportRequest;
import com.lawrence.greenmail.util.MailSender;
import com.lawrence.greenmail.util.ReportEmailComposer;
import com.lawrence.greenmail.util.RestClient;
import com.lawrence.greenmail.verticle.ReportVerticle;

import io.restassured.builder.RequestSpecBuilder;
import io.vertx.core.Vertx;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.StartTLSOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import jakarta.mail.SendFailedException;
import jakarta.mail.internet.MimeMessage;

@ExtendWith(VertxExtension.class)
class MailSenderGreenMailTest {
    private static final Logger LOG = LoggerFactory.getLogger(MailSenderGreenMailTest.class);

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP);

    private MailClient mailClient;
    private RestClient restClient;

    @BeforeEach
    void setup(Vertx vertx) {
        // GreenMail is started now (extension beforeEach already ran)
        int smtpPort = greenMail.getSmtp().getPort();
        LOG.info("Starting GreenMail Server on port {}", smtpPort);

        MailConfig config = new MailConfig()
                .setHostname("127.0.0.1")
                .setPort(smtpPort)
                .setSsl(false)
                .setStarttls(StartTLSOptions.DISABLED)
                .setKeepAlive(false);

        mailClient = MailClient.createShared(vertx, config);
        restClient = new RestClient(() -> new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8181));
    }

    @AfterEach
    void teardown(VertxTestContext tc) {
        mailClient.close().onComplete(tc.succeedingThenComplete());
    }

    @Test
    void exampleUndeliverable(Vertx vertx, VertxTestContext ctx) {
        ReportRequest req = getReportRequest();

        ReportEmailComposer composer = new ReportEmailComposer();
        MailSender sender = new MailSender(mailClient);
        vertx.deployVerticle(new ReportVerticle(composer, sender));

        restClient.postCall("/api/report", req).then().statusCode(421);
        ctx.completeNow();
    }

    private static ReportRequest getReportRequest() {
        var userManager = greenMail.getManagers().getUserManager();

        // keep default behavior for valid users
        var defaultHandler = userManager.getMessageDeliveryHandler();

        userManager.setMessageDeliveryHandler((msg, mailAddress) -> {
            String to = mailAddress.getEmail();
            var user = userManager.getUser(to);

            // If no mailbox/user exists for the recipient -> reject
            if (userManager.getUserByEmail(to) == null) { //
                // This will surface to the client as a send failure, and you can assert "550"
                throw new SendFailedException("550 5.1.1 <" + to + ">: User unknown");
            }

            defaultHandler.handle(msg, mailAddress);

            return user;
        });

        final String bounceFrom = "Lawrence@mss";
        final String from = "Lawrence@mss";
        final String to = "from@domain.net";
        final String subject = "Incident Report";
        final String body = "Indentity exposed";
        final String cc = "from@domain.net, from@domain.net";
        final int size = 0;

        return new ReportRequest(bounceFrom, from, to, subject, body, cc, size);
    }

    @Test
    void shouldSendEmailToGreenMail_andWeCanAssertContent(VertxTestContext ctx) {
        final String bounceFrom = "Lawrence@mss";
        final String from = "Lawrence@mss";
        final String to = "KimJongun@mss";
        final String subject = "Incident Report";
        final String body = "Indentity exposed";
        final String cc = "from@domain.net, from@domain.net";
        final int size = 0;

        ReportRequest req = new ReportRequest(bounceFrom, from, to, subject, body, cc, size);

        ReportEmailComposer composer = new ReportEmailComposer();
        MailSender sender = new MailSender(mailClient);

        MailMessage msg = composer.compose(req);

        sender.send(msg)
                .onFailure(ctx::failNow)
                .onSuccess(res -> ctx.verify(() -> {
                    assertTrue(greenMail.waitForIncomingEmail(5000, 1), "GreenMail did not receive email");

                    MimeMessage[] received = greenMail.getReceivedMessages();
                    assertEquals(1, received.length);

                    MimeMessage r = received[0];
                    assertEquals(subject, r.getSubject());
                    assertEquals(from, GreenMailUtil.getAddressList(r.getFrom()));
                    assertEquals(to, GreenMailUtil.getAddressList(r.getAllRecipients()));

                    String receivedBody = GreenMailUtil.getBody(r);
                    assertTrue(receivedBody.contains(body));

                    ctx.completeNow();
                }));
    }
}
