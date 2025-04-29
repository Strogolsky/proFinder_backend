package fit.biejk.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class MailService {

    @Inject
    private Mailer mailer;

    public void send(String to, String subject, String body) {
        log.info("Sending mail to " + to);
        mailer.send(Mail.withText(to, subject, body));
    }
}
