package giorgiaformicola.capstone.tools;

import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.payloads.users.EmailSendingResponseDTO;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EmailSender {
    private final String domainName;
    private final String apiKey;

    public EmailSender(@Value("${mailgun.domainName}") String domainName, @Value("${mailgun.apiKey}") String apiKey) {
        this.domainName = domainName;
        this.apiKey = apiKey;
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

    public EmailSendingResponseDTO sendReactivationRequestToAdmin(User user) {
        HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + this.domainName + "/messages")
                .basicAuth("api", this.apiKey)
                .queryString("from", "BookVerse <noreply@bookverse.com>")
                .queryString("to", "giorgia.formicola97@gmail.com") // ← la tua email admin
                .queryString("subject", "Reactivation request from " + user.getEmail())
                .queryString("text", "User '" + user.getUsername() + "' (" + user.getEmail() + ") has requested an account reactivation.")
                .asJson();
        System.out.println(response.getBody());
        return new EmailSendingResponseDTO("Reactivation request sent for " + user.getEmail(), LocalDateTime.now());
    }

    public EmailSendingResponseDTO sendReactivationConfirmationToUser(User user) {
        HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + this.domainName + "/messages")
                .basicAuth("api", this.apiKey)
                .queryString("from", "BookVerse <noreply@bookverse.com>")
                .queryString("to", user.getEmail())
                .queryString("subject", "Reactivation request received")
                .queryString("text", "Hello, " + user.getDisplayName() + "! We have received your reactivation request. We will get back to you as soon as possible.")
                .asJson();
        System.out.println(response.getBody());
        return new EmailSendingResponseDTO("Confirmation email sent to " + user.getEmail(), LocalDateTime.now());
    }
}
