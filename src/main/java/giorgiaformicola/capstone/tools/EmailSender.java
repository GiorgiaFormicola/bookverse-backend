package giorgiaformicola.capstone.tools;

import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.payloads.users.EmailSendingResponseDTO;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class EmailSender {
    private final String domainName;
    private final String apiKey;
    private String frontendUrl;

    public EmailSender(@Value("${mailgun.domainName}") String domainName, @Value("${mailgun.apiKey}") String apiKey, @Value("${frontend.url}") String frontendUrl) {
        this.domainName = domainName;
        this.apiKey = apiKey;
        this.frontendUrl = frontendUrl;
    }

    public EmailSendingResponseDTO sendRegistrationEmail(User recipient) {
        HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + this.domainName + "/messages")
                .basicAuth("api", this.apiKey)
                .queryString("from", "BookVerse <noreply@bookverse.com>")
                .queryString("to", recipient.getEmail()) // <-- VERIFIED RECIPIENT
                .queryString("subject", "User registration")
                .queryString("text", "Hello, " + recipient.getDisplayName() + "! You've been successfully registered! Welcome aboard!")
                .asJson();
        System.out.println(response.getBody());
        return new EmailSendingResponseDTO("Email successfully sent to " + recipient.getEmail(), LocalDateTime.now());
    }

    public EmailSendingResponseDTO sendEmailAfterEmailUpdate(User recipient) {
        HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + this.domainName + "/messages")
                .basicAuth("api", this.apiKey)
                .queryString("from", "BookVerse <noreply@bookverse.com>")
                .queryString("to", recipient.getEmail()) // <-- VERIFIED RECIPIENT
                .queryString("subject", "User email update")
                .queryString("text", "Hello, " + recipient.getDisplayName() + "! You're email has been successfully updated!")
                .asJson();
        System.out.println(response.getBody());
        return new EmailSendingResponseDTO("Email successfully sent to " + recipient.getEmail(), LocalDateTime.now());
    }

    public EmailSendingResponseDTO sendReactivationRequestToAdmin(User recipient) {
        HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + this.domainName + "/messages")
                .basicAuth("api", this.apiKey)
                .queryString("from", "BookVerse <noreply@bookverse.com>")
                .queryString("to", "giorgia.formicola97@gmail.com") // ← la tua email admin
                .queryString("subject", "Reactivation request from " + recipient.getEmail())
                .queryString("text", "User '" + recipient.getUsername() + "' (" + recipient.getEmail() + ") has requested an account reactivation.")
                .asJson();
        System.out.println(response.getBody());
        return new EmailSendingResponseDTO("Reactivation request sent for " + recipient.getEmail(), LocalDateTime.now());
    }

    public EmailSendingResponseDTO sendReactivationConfirmationToUser(User recipient) {
        HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + this.domainName + "/messages")
                .basicAuth("api", this.apiKey)
                .queryString("from", "BookVerse <noreply@bookverse.com>")
                .queryString("to", recipient.getEmail())
                .queryString("subject", "Reactivation request received")
                .queryString("text", "Hello, " + recipient.getDisplayName() + "! We have received your reactivation request. We will get back to you as soon as possible.")
                .asJson();
        System.out.println(response.getBody());
        return new EmailSendingResponseDTO("Confirmation email sent to " + recipient.getEmail(), LocalDateTime.now());
    }

    public EmailSendingResponseDTO sendResetPasswordEmail(User recipient, UUID token) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;
        HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + this.domainName + "/messages")
                .basicAuth("api", this.apiKey)
                .queryString("from", "BookVerse <noreply@bookverse.com>")
                .queryString("to", recipient.getEmail())
                .queryString("subject", "Reset your password")
                .queryString("text", "Hello " + recipient.getDisplayName() + "!\n\nClick the link below to reset your password:\n\n" + resetLink + "\n\nThe link expires in 1 hour.\n\nIf you didn't request this, you can safely ignore this email.")
                .asJson();
        System.out.println(response.getBody());
        return new EmailSendingResponseDTO("Reset password email sent to " + recipient.getEmail(), LocalDateTime.now());
    }
}
