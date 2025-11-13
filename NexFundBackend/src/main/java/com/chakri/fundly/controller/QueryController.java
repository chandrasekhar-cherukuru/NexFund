package com.chakri.fundly.controller;

import com.chakri.fundly.model.Query;
import com.chakri.fundly.service.QueryEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class QueryController {

    @Autowired
    private QueryEmailService queryEmailService;

    @PostMapping("/query")
    public ResponseEntity<String> submitQuery(@RequestBody Query query) {
        try {
            queryEmailService.sendQueryEmail(query);
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send email");
        }
    }
}
