package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final IncentiveService incentiveService;
    
    public TransactionService(UserRepository userRepository, TransactionRepository transactionRepository, IncentiveService incentiveService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.incentiveService = incentiveService;
    }
    
    @Transactional
    public boolean processTransaction(Transaction transaction) {
        logger.info("Processing transaction: {}", transaction);
        
        // Validate sender exists
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        if (sender == null) {
            logger.warn("Invalid sender ID: {}", transaction.getSenderId());
            return false;
        }
        
        // Validate recipient exists
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        if (recipient == null) {
            logger.warn("Invalid recipient ID: {}", transaction.getRecipientId());
            return false;
        }
        
        // Validate sender has sufficient balance
        if (sender.getBalance() < transaction.getAmount()) {
            logger.warn("Insufficient balance for sender {}: required {}, available {}", 
                sender.getName(), transaction.getAmount(), sender.getBalance());
            return false;
        }
        
        // Process the transaction
        try {
            // Get incentive from API
            Incentive incentive = incentiveService.getIncentive(transaction);
            float incentiveAmount = incentive.getAmount();
            
            // Deduct amount from sender
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            userRepository.save(sender);
            
            // Add amount + incentive to recipient (incentive is not deducted from sender)
            recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);
            userRepository.save(recipient);
            
            // Record the transaction with incentive
            TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
            transactionRepository.save(transactionRecord);
            
            logger.info("Transaction processed successfully: {} -> {}: ${} + ${} incentive", 
                sender.getName(), recipient.getName(), transaction.getAmount(), incentiveAmount);
            return true;
            
        } catch (Exception e) {
            logger.error("Error processing transaction: {}", e.getMessage(), e);
            return false;
        }
    }
} 