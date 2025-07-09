package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IncentiveService {
    
    private static final Logger logger = LoggerFactory.getLogger(IncentiveService.class);
    private static final String INCENTIVE_API_URL = "http://localhost:8080/incentive";
    
    private final RestTemplate restTemplate;
    
    public IncentiveService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }
    
    public Incentive getIncentive(Transaction transaction) {
        try {
            logger.info("Calling incentive API for transaction: {}", transaction);
            Incentive incentive = restTemplate.postForObject(INCENTIVE_API_URL, transaction, Incentive.class);
            logger.info("Received incentive: {}", incentive);
            return incentive != null ? incentive : new Incentive(0.0f);
        } catch (Exception e) {
            logger.error("Error calling incentive API: {}", e.getMessage(), e);
            // Return zero incentive if API call fails
            return new Incentive(0.0f);
        }
    }
} 