package com.mayday.domain.ai.dto;

import com.mayday.domain.ai.model.EvidenceType;
import com.mayday.domain.ai.model.ExpenseCategory;

import java.time.LocalDate;

public class ExpenseAnalysisResponse {

    private final String analysisId;
    private final String type;
    private final LocalDate date;
    private final String merchantName;
    private final String itemName;
    private final Long amount;
    private final ExpenseCategory category;
    private final EvidenceType evidenceType;
    private final Boolean qualifiedEvidence;
    private final String reason;
    private final int confidenceScore;

    public ExpenseAnalysisResponse(
            String analysisId,
            String type,
            LocalDate date,
            String merchantName,
            String itemName,
            Long amount,
            ExpenseCategory category,
            EvidenceType evidenceType,
            Boolean qualifiedEvidence,
            String reason,
            int confidenceScore
    ){
        this.analysisId = analysisId;
        this.type = type;
        this.date = date;
        this.merchantName = merchantName;
        this.itemName = itemName;
        this.amount = amount;
        this.category = category;
        this.evidenceType = evidenceType;
        this.qualifiedEvidence = qualifiedEvidence;
        this.reason = reason;
        this.confidenceScore = confidenceScore;
    }

    public String getAnalysisId(){ return analysisId;}

    public String getType() {return type;}

    public LocalDate getDate(){ return date;}

    public String getMerchantName(){return merchantName;}

    public String getItemName(){return itemName;}

    public Long getAmount(){return amount;}

    public ExpenseCategory getCategory(){return category;}

    public EvidenceType getEvidenceType(){return evidenceType;}

    public Boolean getQualifiedEvidence(){return qualifiedEvidence;}

    public String getReason(){return reason;}

    public int getConfidenceScore(){return confidenceScore;}

}
