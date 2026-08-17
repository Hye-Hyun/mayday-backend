package com.mayday.domain.income;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    protected Income() {
    }

    private Income(Long userId, Long amount, LocalDate date) {
        this.userId = userId;
        this.amount = amount;
        this.date = date;
        this.createdAt = LocalDateTime.now();
    }

    public static Income create(Long userId, Long amount, LocalDate date) {
        return new Income(userId, amount, date);
    }

    public void update(Long amount, LocalDate date) {
        this.amount = amount;
        this.date = date;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public boolean isDeleted() { return deleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}