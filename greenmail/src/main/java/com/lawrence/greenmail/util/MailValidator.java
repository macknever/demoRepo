package com.lawrence.greenmail.util;

import java.io.IOException;
import java.util.Properties;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;

/**
 * Validates email content.
 */
public class MailValidator {
    private static final String carbonioHost = "carbonio.dev-globalrelay.net";
    private static final int carbonioIMAPSTARTTLS = 143;

    String username = "nucleus.uc@dev-globalrelay.net";
    String password = "(GxVA%WE)bN&s5L6pm)r";


    Store store = null;
    Folder inbox = null;

    public Message[] getEmails() throws MessagingException, IOException {
        Properties properties = new Properties();
       // properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", carbonioHost);
        properties.put("mail.imaps.port", 993);
        properties.put("mail.imaps.ssl.checkserveridentity", "true");
        properties.put("mail.imaps.ssl.enable", "true"); // Ensures a secure connection

        properties.setProperty("mail.imap.starttls.enable", "true");
        properties.put("mail.starttls.required", "true");


        // Optional timeouts to prevent hanging connections
        properties.put("mail.imaps.connectiontimeout", "5000");
        properties.put("mail.imaps.timeout", "5000");

        Session session = Session.getInstance(properties);
        Store store = session.getStore("imaps");
        store.connect(username, password);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        Message[] messages = inbox.getMessages();
        System.out.println("Total messages: " + messages.length);
        System.out.println(messages[0].getSubject());

        System.out.println(messages[0].getContent().toString());


        inbox.close(false);
        store.close();

        return messages;
    }
}
