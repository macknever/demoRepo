package com.lawrence.greenmail.verticle;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.lawrence.greenmail.model.ReportRequest;
import com.lawrence.greenmail.util.MailSender;
import com.lawrence.greenmail.util.ReportEmailComposer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.SMTPException;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class ReportVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(ReportVerticle.class);

    private final ReportEmailComposer composer;
    private final MailSender mailSender;

    @Inject
    public ReportVerticle(ReportEmailComposer composer, MailSender mailSender) {
        this.composer = composer;
        this.mailSender = mailSender;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.post("/api/report").handler(ctx -> {
            ReportRequest req;
            try {
                req = ctx.body().asJsonObject().mapTo(ReportRequest.class);
            } catch (Exception e) {
                ctx.response()
                        .setStatusCode(400)
                        .end("Invalid JSON body: " + e.getMessage());
                return;
            }

            String validationError = validate(req);
            if (validationError != null) {
                ctx.response().setStatusCode(400).end(validationError);
                return;
            }

            mailSender.send(composer.compose(req))
                    .onSuccess(resJson -> {
                        ctx.response()
                                .setStatusCode(250)
                                .putHeader("content-type", "application/json")
                                .end("response from SMTP server: " + resJson.encodePrettily());
                            })
                    .onFailure(err -> {
                                if (err instanceof SMTPException se) {
                                    LOG.error("SMTP replyCode: {}", se.getReplyCode());
                                    LOG.error("SMTP replyMessages: {}", se.getReplyMessages());
                                    ctx.response()
                                            .setStatusCode(se.getReplyCode())
                                            .end(String.join(",",se.getReplyMessages()));
                                } else {
                                    LOG.error("Exception TYPE: {}", err.getClass().getSimpleName(), err);
                                    LOG.error(err.getMessage(), err);
                                    ctx.response()
                                            .setStatusCode(421)
                                            .end("NON SMTP failure: " + err.getMessage());
                                }

                            }
                    );
        });

        int port = config().getInteger("http.port", 8181);

        vertx.createHttpServer(new HttpServerOptions().setSsl(false))
                .requestHandler(router)
                .listen(port)
                .onSuccess(s -> {
                    LOG.info("Server listening on port {}", port);
                    startPromise.complete();
                })
                .onFailure(err -> {
                    LOG.error("Server listening on port {} failed", port, err);
                    startPromise.fail(err);
                });
    }

    private static String validate(ReportRequest req) {
        if (isBlank(req.from())) {
            return "`from` is required";
        }
        if (isBlank(req.to())) {
            return "`to` is required";
        }
        if (isBlank(req.subject())) {
            return "`subject` is required";
        }
        return null;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
