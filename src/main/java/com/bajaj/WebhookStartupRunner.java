package com.bajaj;


import com.bajaj.dto.FinalQueryRequest;
import com.bajaj.dto.WebhookRequest;
import com.bajaj.dto.WebhookResponse;
import com.bajaj.service.SQLSolutionService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component

public class WebhookStartupRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(WebhookStartupRunner.class);

    private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SQLSolutionService sqlSolutionService;

    @Override
    public void run(String... args) {
        try {
            log.info("Starting webhook generation process...");

            // 1. Generate a webhook
            WebhookRequest webhookRequest = new WebhookRequest(
                    "Prem kushwah",
                    "REG12347",
                    "premkushwah220706@acropolis.in"
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<WebhookRequest> request = new HttpEntity<>(webhookRequest, headers);

            ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                    GENERATE_WEBHOOK_URL,
                    request,
                    WebhookResponse.class
            );

            WebhookResponse webhookResponse = response.getBody();

            if (webhookResponse == null) {
                log.error("Failed to get webhook response");
                return;
            }

            log.info("Webhook generated successfully: {}", webhookResponse);

            // 2. Get registration number to determine the question
            String regNo = webhookRequest.getRegNo();
            int lastTwoDigits = Integer.parseInt(regNo.substring(regNo.length() - 2));
            boolean isOdd = lastTwoDigits % 2 != 0;

            // 3. Solve SQL problem based on registration number
            String finalSqlQuery = sqlSolutionService.solveSQL(isOdd);

            // 4. Submit the solution to the webhook URL
            submitSolution(webhookResponse.getWebhook(), webhookResponse.getAccessToken(), finalSqlQuery);

        } catch (Exception e) {
            log.error("Error during startup process", e);
        }
    }

    private void submitSolution(String webhookUrl, String accessToken, String finalSqlQuery) {
        try {
            log.info("Submitting solution to webhook URL: {}", webhookUrl);

            FinalQueryRequest finalQueryRequest = new FinalQueryRequest(finalSqlQuery);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);

            HttpEntity<FinalQueryRequest> request = new HttpEntity<>(finalQueryRequest, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    webhookUrl,
                    request,
                    String.class
            );

            log.info("Solution submitted successfully, response: {}", response.getBody());

        } catch (Exception e) {
            log.error("Error submitting solution", e);
        }
    }
}