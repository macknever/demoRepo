package com.lawrence.greenmail;

import jakarta.mail.*;
import jakarta.mail.search.SubjectTerm;
import java.util.Properties;

public class GreenMailUtil {

    private final String host;
    private final int imapPort;

    public GreenMailUtil(String host, int imapPort) {
        this.host = host;
        this.imapPort = imapPort;
    }

    /**
     * Verifies if an email exists and returns its body content.
     */
    public String getEmailBody(String user, String password, String subject) throws Exception {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imap");
        props.put("mail.imap.host", host);
        props.put("mail.imap.port", String.valueOf(imapPort));
        props.put("mail.imap.ssl.enable", "false"); // Minikube GreenMail usually non-SSL

        Session session = Session.getInstance(props);
        Store store = session.getStore("imap");

        try {
            store.connect(user, password);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Search for messages with the specific subject
            Message[] messages = inbox.search(new SubjectTerm(subject));

            if (messages.length == 0) {
                throw new RuntimeException("No email found with subject: " + subject);
            }

            // Get the latest message
            Message latest = messages[messages.length - 1];

            // Note: For simple text, getContent().toString() works.
            // For multi-part/HTML, more complex parsing is needed.
            Object content = latest.getContent();
            return content != null ? content.toString() : "";

        } finally {
            if (store != null) store.close();
        }
    }
}
