package com.example.kiki.service;

import com.example.kiki.entity.User;
import com.example.kiki.exception.UpstreamServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;

@Component
@Primary
@RequiredArgsConstructor
public class MailTrapPasswordResetSender implements PasswordResetSender {
    private final JavaMailSender mailSender;

    @Override
    public void send(User user, String rawToken) {
        String resetLink = "https://kiki-project.vercel.app/reset-password.html?token=" + rawToken;

        String htmlBody = """
                <div style="font-family: Arial, sans-serif; max-width: 480px; margin: auto;">
                    <h2>Reset your Kiki password</h2>
                    <p>Hi %s,</p>
                    <p>We received a request to reset your password. This link is valid for 10 minutes.</p>
                    <p>
                        <a href="%s" style="background-color:#000; color:#fff; padding:10px 20px; text-decoration:none; border-radius:5px;">
                            Reset Password
                        </a>
                    </p>
                    <p>If you didn't request this, you can safely ignore this email.</p>
                </div>
                """.formatted(user.getFirstName(), resetLink);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getEmail());
            helper.setSubject("Reset your Kiki password");
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new UpstreamServiceException("Failed to send password reset email", e);
        }
    }
}
