package com.chakri.fundly.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String email, String participantName, String fundraiserTitle,
                                      String verifiedBy, String amountPaid) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom("nexfund.app@gmail.com");
            helper.setTo(email);
            helper.setSubject("🎉 Payment Verified - NexFund");

            String htmlContent = buildVerificationEmailTemplate(participantName, fundraiserTitle,
                    verifiedBy, amountPaid);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            logger.info("✅ Verification email sent successfully to: {}", email);

        } catch (MessagingException e) {
            logger.error("❌ Failed to send verification email to {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    private String buildVerificationEmailTemplate(String participantName, String fundraiserTitle,
                                                  String verifiedBy, String amountPaid) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; color: #333; background-color: #f5f5f5; margin: 0; padding: 0; }\n" +
                "        .container { max-width: 600px; margin: 20px auto; padding: 0; background-color: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n" +
                "        .header { background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%); color: white; padding: 30px 20px; text-align: center; border-radius: 8px 8px 0 0; }\n" +
                "        .header h1 { margin: 0; font-size: 28px; font-weight: bold; }\n" +
                "        .content { padding: 30px 20px; }\n" +
                "        .greeting { font-size: 16px; margin: 0 0 20px 0; }\n" +
                "        .info-box { background-color: #f0f8f0; padding: 20px; border-left: 4px solid #4CAF50; margin: 20px 0; border-radius: 4px; }\n" +
                "        .info-row { margin: 10px 0; font-size: 14px; }\n" +
                "        .label { font-weight: bold; color: #4CAF50; }\n" +
                "        .message { font-size: 14px; color: #666; line-height: 1.6; margin: 20px 0; }\n" +
                "        .footer { padding: 20px; text-align: center; color: #888; font-size: 12px; border-top: 1px solid #eee; }\n" +
                "        .footer-text { margin: 5px 0; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>✅ Payment Verified!</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p class=\"greeting\">Hello <strong>" + participantName + "</strong>,</p>\n" +
                "            <p class=\"message\">Great news! Your contribution has been successfully verified by the fundraiser creator.</p>\n" +
                "            \n" +
                "            <div class=\"info-box\">\n" +
                "                <div class=\"info-row\"><span class=\"label\">Fundraiser:</span> " + fundraiserTitle + "</div>\n" +
                "                <div class=\"info-row\"><span class=\"label\">Amount:</span> ₹" + amountPaid + "</div>\n" +
                "                <div class=\"info-row\"><span class=\"label\">Verified by:</span> " + verifiedBy + "</div>\n" +
                "            </div>\n" +
                "            \n" +
                "            <p class=\"message\">Thank you for your generous contribution! Your support makes a real difference.</p>\n" +
                "            <p class=\"message\">If you have any questions, feel free to reach out to the fundraiser creator.</p>\n" +
                "            <p class=\"message\">Best regards,<br><strong>NexFund Team</strong></p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p class=\"footer-text\">This is an automated email from NexFund. Please do not reply to this email.</p>\n" +
                "            <p class=\"footer-text\">&copy; 2025 NexFund. All rights reserved.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
