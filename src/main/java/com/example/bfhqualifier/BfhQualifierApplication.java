package com.example.bfhqualifier;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BfhQualifierApplication implements CommandLineRunner {

    private final RestTemplate restTemplate;

    public BfhQualifierApplication(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(BfhQualifierApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // Step 1: Generate webhook
        String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("name", "John Doe");
        userDetails.put("regNo", "REG12347");
        userDetails.put("email", "john@example.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(userDetails, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(generateWebhookUrl, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String webhookUrl = (String) response.getBody().get("webhook");
                String accessToken = (String) response.getBody().get("accessToken");

                // Step 2: Determine which SQL problem to solve
                String regNo = userDetails.get("regNo"); // "REG12347"
                int lastTwoDigits = Integer.parseInt(regNo.substring(regNo.length() - 2));
                String sqlQuery;
                if (lastTwoDigits % 2 == 1) {
                    // Odd → Question 1
                    // Replace below with your final answer after checking the question!
                    sqlQuery = "-- TODO: Write SQL for Question 1\nSELECT * FROM table1;";
                } else {
                    // Even → Question 2
                    // Replace below with your final answer after checking the question!
                    sqlQuery = "-- TODO: Write SQL for Question 2\nSELECT * FROM table2;";
                }

                // Step 3: Submit solution to webhook with JWT Authorization
                Map<String, String> submitBody = new HashMap<>();
                submitBody.put("finalQuery", sqlQuery);

                HttpHeaders submitHeaders = new HttpHeaders();
                submitHeaders.setContentType(MediaType.APPLICATION_JSON);
                submitHeaders.setBearerAuth(accessToken);

                HttpEntity<Map<String, String>> submitRequest = new HttpEntity<>(submitBody, submitHeaders);

                ResponseEntity<String> submitResponse =
                        restTemplate.exchange(webhookUrl, HttpMethod.POST, submitRequest, String.class);

                System.out.println("Webhook submission status: " + submitResponse.getStatusCode());
                System.out.println("Webhook response: " + submitResponse.getBody());
            } else {
                System.err.println("Failed to generate webhook: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error in workflow: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}