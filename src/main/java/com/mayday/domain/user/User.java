package com.mayday.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import com.mayday.domain.income.model.IndustryCategory;

@Getter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean agreedToTerms;

    @Column(nullable = false)
    private boolean agreedToPrivacy;

    @Column(nullable = false)
    private boolean agreedToReceiptAnalysis;

    @Column(nullable = false)
    private boolean onboardingCompleted;

    protected User(){}

    public User(String email, String password,
                boolean agreedToTerms, boolean agreedToPrivacy,
                boolean agreedToReceiptAnalysis){
        this.email = email;
        this.password = password;
        this.agreedToTerms = agreedToTerms;
        this.agreedToPrivacy = agreedToPrivacy;
        this.agreedToReceiptAnalysis = agreedToReceiptAnalysis;
        this.onboardingCompleted = false;
    }

    @Enumerated(EnumType.STRING)
    private IndustryCategory industryCategory;

    public void updateIndustryCategory(IndustryCategory industryCategory) {
        this.industryCategory = industryCategory;
    }
}
