package com.mayday.domain.income;

import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.income.dto.IncomeCreateRequest;
import com.mayday.domain.income.dto.IncomeUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String analysisId;

    @Column(nullable = false)
    private LocalDate date;

    private String merchantName;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Long receivedAmount;

    @Column(nullable = false)
    private boolean withholdingTaxApplied;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory category;

    @Column(length = 500)
    private String remark;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    protected Income() {
    }

    public static Income create(Long userId, IncomeCreateRequest request) {
        Income income = new Income();
        income.userId = userId;
        income.analysisId = request.getAnalysisId();
        income.date = request.getDate();
        income.merchantName = request.getMerchantName();
        income.itemName = request.getItemName();
        income.amount = request.getAmount();
        income.receivedAmount = request.resolveReceivedAmount();
        income.withholdingTaxApplied = request.isWithholdingTaxApplied();
        income.category = request.getCategory();
        income.remark = request.getRemark();
        income.deleted = false;
        return income;
    }

    public void update(IncomeUpdateRequest request) {
        this.date = request.getDate();
        this.merchantName = request.getMerchantName();
        this.itemName = request.getItemName() == null || request.getItemName().isBlank()
                ? this.itemName
                : request.getItemName();
        this.amount = request.getAmount();
        this.receivedAmount = request.resolveReceivedAmount();
        this.withholdingTaxApplied = request.isWithholdingTaxApplied();
        this.category = request.getCategory();
        this.remark = request.getRemark();
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public long getWithholdingTax() {
        return withholdingTaxApplied ? Math.max(amount - receivedAmount, 0L) : 0L;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getAnalysisId() { return analysisId; }
    public LocalDate getDate() { return date; }
    public String getMerchantName() { return merchantName; }
    public String getItemName() { return itemName; }
    public Long getAmount() { return amount; }
    public Long getReceivedAmount() { return receivedAmount; }
    public boolean isWithholdingTaxApplied() { return withholdingTaxApplied; }
    public ExpenseCategory getCategory() { return category; }
    public String getRemark() { return remark; }
    public boolean isDeleted() { return deleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
