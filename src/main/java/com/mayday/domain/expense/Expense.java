package com.mayday.domain.expense;

import com.mayday.domain.ai.model.EvidenceType;
import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.expense.dto.ExpenseCreateRequest;
import com.mayday.domain.expense.dto.ExpenseUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "expenses")
public class Expense {

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EvidenceType evidenceType;

    @Column(nullable = false)
    private Boolean qualifiedEvidence;

    @Column(length = 500)
    private String reason;

    @Column(length = 500)
    private String remark;

    @Column(nullable = false)
    private boolean deleted;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Expense() {
    }

    public static Expense create(Long userId, ExpenseCreateRequest request) {
        Expense expense = new Expense();
        expense.userId = userId;
        expense.analysisId = request.getAnalysisId();
        expense.date = request.getDate();
        expense.merchantName = request.getMerchantName();
        expense.itemName = request.getItemName();
        expense.amount = request.getAmount();
        expense.category = request.getCategory();
        expense.evidenceType = request.getEvidenceType();
        expense.qualifiedEvidence = request.getQualifiedEvidence();
        expense.reason = request.getReason();
        expense.remark = request.getRemark();
        expense.deleted = false;
        return expense;
    }

    public void update(ExpenseUpdateRequest request) {
        this.date = request.getDate();
        this.merchantName = request.getMerchantName();
        this.itemName = request.getItemName();
        this.amount = request.getAmount();
        this.category = request.getCategory();
        this.evidenceType = request.getEvidenceType();
        this.qualifiedEvidence = request.getQualifiedEvidence();
        this.remark = request.getRemark();
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
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
}
