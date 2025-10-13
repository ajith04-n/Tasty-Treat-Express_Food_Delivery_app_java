package com.tastytreat.backend.tasty_treat_express_backend.services;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.InternetAddress;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.EmailSendingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Send plain text email
     */
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(new InternetAddress("najithunnikrishnan@gmail.com", "Tasty Treat Express"));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false);
            mailSender.send(message);
            System.out.println("✅ Plain text email sent to: " + to);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EmailSendingException("Failed to send email: " + e.getMessage());
        }
    }

    /**
     * Send beautiful HTML email
     */
    public void sendHtmlMessage(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            try {
				helper.setFrom(new InternetAddress("najithunnikrishnan@gmail.com", "Tasty Treat Express"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            System.out.println("✅ HTML email sent to: " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new EmailSendingException("Failed to send HTML email: " + e.getMessage());
        }
    }

    /**
     * Send an email with attachment
     */
    public void sendReportByEmail(String recipientEmail, String subject, String message, byte[] attachmentData,
            String fileName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            try {
				helper.setFrom(new InternetAddress("najithunnikrishnan@gmail.com", "Tasty Treat Express"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(message, false);
            helper.addAttachment(fileName, new ByteArrayResource(attachmentData));
            mailSender.send(mimeMessage);
            System.out.println("✅ Email with attachment sent to: " + recipientEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new EmailSendingException("Failed to send attachment email: " + e.getMessage());
        }
    }

    /**
     * Utility method: send registration success email
     */
    public void sendRegistrationSuccessEmail(String toEmail, String userName) {
        String htmlBody = """
                <div style='font-family: Arial, sans-serif; line-height: 1.6;'>
                    <h2 style='color:#ff6600;'>Welcome to Tasty Treat Express 🍰</h2>
                    <p>Hi <b>%s</b>,</p>
                    <p>Your account has been created successfully!</p>
                    <p>Explore our delicious menu and enjoy your treats 🎉</p>
                    <hr/>
                    <p style='font-size: 12px; color: gray;'>This is an automated email from Tasty Treat Express.</p>
                </div>
                """.formatted(userName);

        sendHtmlMessage(toEmail, "Welcome to Tasty Treat Express!", htmlBody);
    }
}
