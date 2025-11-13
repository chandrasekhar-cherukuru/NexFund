package com.chakri.fundly.service;

import com.chakri.fundly.model.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class QueryEmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendQueryEmail(Query query) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("nexfund.app@gmail.com");
        message.setSubject(query.getSubject() != null && !query.getSubject().isEmpty()
                ? query.getSubject()
                : "New Query from NexFund");

        message.setText(
                "Name: " + query.getName() + "\n" +
                        "Email: " + query.getEmail() + "\n" +
                        "Phone: " + (query.getPhone() != null ? query.getPhone() : "") + "\n" +
                        "Message: " + query.getMessage()
        );

        mailSender.send(message);
    }
}
