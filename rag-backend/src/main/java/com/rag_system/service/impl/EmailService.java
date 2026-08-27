package com.rag_system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Async
    public void sendVerificationEmail(String toEmail, String otp) {
        String subject = "Verify Your Email Address";
        String htmlContent = buildVerificationEmailHtml(otp);

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Async
    public void send2FAOtpEmail(String toEmail, String otp) {
        String subject = "Your Two-Factor Authentication Code";
        String htmlContent = build2FAOtpHtml(otp);

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        if (mailSender == null) {
            logger.warn("JavaMailSender not configured. Email to {} not sent.\nSubject: {}\nContent: {}",
                    to, subject, htmlContent);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("noreply@ragsystem.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            logger.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    private String buildVerificationEmailHtml(String token) {
        return """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"></head>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #333;">Verify Your Email Address</h2>
                    <p>Thank you for registering. Your verification token is:</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <span style="background-color: #f4f4f4; padding: 15px 30px; font-size: 32px;
                                     font-weight: bold; letter-spacing: 8px; border-radius: 5px;
                                     color: #333; display: inline-block;">
                            %s
                        </span>
                    </div>
                    <p style="color: #666; font-size: 14px;">Use this token to verify your email.</p>
                    <p style="color: #999; font-size: 12px; margin-top: 30px;">This token will expire in 3 Minutes.</p>
                </body>
                </html>
                """.formatted(token);
    }

    private String build2FAOtpHtml(String otp) {
        return """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"></head>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #333;">Two-Factor Authentication</h2>
                    <p>Your verification code is:</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <span style="background-color: #f4f4f4; padding: 15px 30px; font-size: 32px;
                                     font-weight: bold; letter-spacing: 8px; border-radius: 5px;
                                     color: #333; display: inline-block;">
                            %s
                        </span>
                    </div>
                    <p style="color: #666; font-size: 14px;">This code will expire in 10 minutes.</p>
                    <p style="color: #999; font-size: 12px; margin-top: 30px;">If you did not request this code, please ignore this email.</p>
                </body>
                </html>
                """.formatted(otp);
    }
}
