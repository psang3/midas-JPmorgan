package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue()
    private long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserRecord recipient;

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private float incentive;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    protected TransactionRecord() {
    }

    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount, float incentive) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.incentive = incentive;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public UserRecord getSender() {
        return sender;
    }

    public UserRecord getRecipient() {
        return recipient;
    }

    public float getAmount() {
        return amount;
    }

    public float getIncentive() {
        return incentive;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("TransactionRecord[id=%d, sender='%s', recipient='%s', amount='%f', incentive='%f', timestamp='%s']", 
            id, sender.getName(), recipient.getName(), amount, incentive, timestamp);
    }
} 