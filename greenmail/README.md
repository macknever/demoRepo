# Validate mailbox delivery

`ImapMailClient` reads a mailbox through Jakarta Mail. `MailValidator` waits up to
60 seconds for an **exact subject**, then optionally checks a plain-text body
substring. Send a report with a fresh UUID in its subject before running the
validator. The validator only reads mail; it does not send, delete, or mark it read.
Text attachments are excluded from body validation. HTML-only bodies are not
supported by the optional plain-text assertion.

From the repository root, run this in an authenticated Secret Server shell.
Replace the secret ID with your mailbox secret ID:

```bash
(
  set -e
  set +x
  SMOKE_IMAP_PASSWORD="$(secretserver-tool --secret-id=YOUR_MAILBOX_SECRET_ID --field Password --get-field)"
  test -n "$SMOKE_IMAP_PASSWORD"
  export SMOKE_IMAP_PASSWORD

  mvn -pl greenmail compile exec:java \
    -Dexec.mainClass=com.lawrence.greenmail.util.MailValidator \
    -Dexec.args='"YOUR EXACT SUBJECT WITH UUID" "expected body substring"'
)
```

Omit the second argument to check delivery and subject only. The password is
read from the environment, never a Maven argument or a committed file. No token
or explicit mail authentication mechanism is needed for mailbox password login.

| Environment variable | Default |
| --- | --- |
| `SMOKE_IMAP_HOST` | `carbonio.dev-globalrelay.net` |
| `SMOKE_IMAP_USERNAME` | `nucleus.uc@dev-globalrelay.net` |
| `SMOKE_IMAP_PASSWORD` | Required |
| `SMOKE_IMAP_MODE` | `imaps` |
| `SMOKE_IMAP_PORT` | `993` for IMAPS; `143` for STARTTLS |
| `SMOKE_IMAP_FOLDER` | `INBOX` |

For STARTTLS, add `export SMOKE_IMAP_MODE=starttls` inside the subshell before
Maven. Leave the port unset for port 143, or set it explicitly for your server.
Both modes verify the TLS server identity and use the JVM trust store. STARTTLS
is required in that mode; there is no fallback to an unencrypted connection.

`ImapMailModule` provides an unscoped client for Guice tests. Inject a
`Provider<ImapMailClient>` and close each connection with try-with-resources:

```java
try (var client = clientProvider.get()) {
    new MailValidator(client).validate(subject, expectedText, Duration.ofSeconds(60));
}
```

Run the isolated tests without mailbox credentials:

```bash
mvn -pl greenmail -Dtest=ImapMailClientTest,MailValidatorTest test
```

The existing Carbonio test is opt-in. Set `SMOKE_IMAP_SUBJECT` to the exact subject
and provide the same connection environment, then run:

```bash
mvn -pl greenmail '-Dtest=GreenMailIT#testCarbonio' test
```
