package com.mayday.domain.ledger.dto;

import java.util.List;

public class LedgerYearsResponse {

    private final List<Integer> years;

    public LedgerYearsResponse(List<Integer> years) {
        this.years = years;
    }

    public List<Integer> getYears() {
        return years;
    }
}
