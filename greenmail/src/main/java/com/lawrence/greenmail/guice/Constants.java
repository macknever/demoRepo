package com.lawrence.greenmail.guice;

public class Constants {
    private Constants() {
        throw new  AssertionError("Should not reach this");
    }

    public static final String SMTP_SERVER_HOST_PROP = "smtp.server.host";
    public static final String SMTP_SERVER_PORT_PROP = "smtp.server.port";
    public static final String SMTP_SERVER_USERNAME_PROP = "smtp.server.username";
    public static final String SMTP_SERVER_PASSWORD_PROP = "smtp.server.password";
}
