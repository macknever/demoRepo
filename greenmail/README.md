# Validate mailbox delivery with JUnit

`ImapMailClient` reads mail using IMAPS (implicit TLS). `MailValidator` waits for
an exact subject and checks that the same email contains the expected plain-text
body substring and every expected attachment filename (case-sensitive). It does not send, delete, or mark mail read.
Polling uses a Failsafe retry policy bounded by the requested timeout. Only an
absent matching email is retried; mail/IO errors fail immediately.
Use a fresh UUID in the report subject to avoid matching an older message.

## Run from IntelliJ

Open `src/test/java/com/lawrence/greenmail/util/MailValidatorIT.java` and create a
JUnit run configuration for `receivesEmailWithExpectedBodyAndAttachments` using the
`greenmail` module. Under **Run → Edit Configurations → Environment variables**,
set:

```text
SMOKE_IMAP_PASSWORD=your-mailbox-password
```

Set `subject`, `expectedBody`, and `expectedAttachments` directly in the test
method to match your report. Send the report first, then run the test. It waits up to 60 seconds for delivery.
An absent email, mismatched body, or missing expected filename fails the test. Other attachments are
allowed. No program arguments or main class are needed.

Retrieve the password with your authenticated Secret Server CLI, then paste the
value into the local configuration. IntelliJ does not execute shell commands in
its environment-variable field. Do not share a run configuration containing the
password or store it as a project file.

| Environment variable | Default |
| --- | --- |
| `SMOKE_IMAP_HOST` | `carbonio.dev-globalrelay.net` |
| `SMOKE_IMAP_PORT` | `993` |
| `SMOKE_IMAP_USERNAME` | `nucleus.uc@dev-globalrelay.net` |
| `SMOKE_IMAP_PASSWORD` | Required |
| `SMOKE_IMAP_FOLDER` | `INBOX` |

Only IMAPS is supported. The client verifies the TLS server identity using the
JVM trust store. Username/password authentication needs no explicit mechanism.

## Run from a terminal

From the repository root, in an authenticated Secret Server shell:

```bash
(
  set -e
  set +x
  SMOKE_IMAP_PASSWORD="$(secretserver-tool --secret-id=YOUR_MAILBOX_SECRET_ID --field Password --get-field)"
  test -n "$SMOKE_IMAP_PASSWORD"
  export SMOKE_IMAP_PASSWORD
  mvn -pl greenmail -Dtest=MailValidatorIT test
)
```

The live test is explicitly selected; normal Surefire unit-test runs exclude
`*IT` classes. Run the isolated unit tests without credentials:

```bash
mvn -pl greenmail -Dtest=ImapMailClientTest,MailValidatorTest test
```

## Use the utility in another test

Create an injector with `ImapMailModule` and inject a `Provider<ImapMailClient>`.
Each test owns its connection:

```java
try (var client = clientProvider.get()) {
    new MailValidator(client).validate(subject, expectedBody,
            List.of("report.zip", "details.txt"), Duration.ofSeconds(60));
}
```

Expected message content belongs in the test; environment variables configure
only the mailbox connection. Body checks use decoded plain text, excluding
attachments. HTML-only body validation is not supported.

The logging server sends a plain-text body and named attachments with disposition
`attachment`. The parser collects filenames independently of the attachment MIME
type. A multipart containing only files produces an empty body, so a nonempty
expected-body assertion fails. The demo `ReportEmailComposer` currently creates
an unnamed attachment; use a message with named attachments for filename checks.
