package com.mayday.domain.user;

import jakarta.persistence.*;
import lombok.Getter;

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

    @Enumerated(EnumType.STRING)
    @Column
    private JobCategory jobCategory; // nullable — 온보딩 전엔 없음

    @Column
    private Long initialIncome; // nullable — 온보딩 전엔 없음


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

    public void completeOnboarding(JobCategory jobCategory, Long initialIncome) {
        this.jobCategory = jobCategory;
        this.initialIncome = initialIncome;
        this.onboardingCompleted = true;
    }

}
