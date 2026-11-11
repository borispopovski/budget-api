package mk.mdt.budgetapi.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mk.mdt.budgetapi.exception.EmailSendException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final long RESET_TOKEN_VALID_MINUTES = 30;
    private static final String PASSWORD_RESET_SUBJECT = "Reset your Budget App password";
    private static final String PASSWORD_RESET_TEMPLATE = "templates/email/password-reset.html";

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.mail.from-name:Budget App}")
    private String fromName;

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        String link = frontendUrl + "/reset-password?token=" + token;

        String html = loadTemplate(PASSWORD_RESET_TEMPLATE)
                .replace("{{link}}", link)
                .replace("{{minutes}}", String.valueOf(RESET_TOKEN_VALID_MINUTES));

        send(to, PASSWORD_RESET_SUBJECT, html);
    }

    private String loadTemplate(String path) {
        try {
            return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Cannot load email template {}", path, e);
            throw new UncheckedIOException("Cannot load email template " + path, e);
        }
    }

    private void send(String to, String subject, String htmlBody) {
        log.debug("Sending email '{}' to {}", subject, to);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(from, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email '{}' sent to {}", subject, to);
        } catch (MessagingException | UnsupportedEncodingException | MailException e) {
            log.error("Failed to send email '{}' to {}", subject, to, e);
            throw new EmailSendException("Failed to send email to " + to, e);
        }
    }
}