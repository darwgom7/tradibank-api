package com.darwgom.tradibankapi.domain.enums;


public enum ReportTypeEnum {
    TRANSACTIONS_CUSTOMER_MONTH("Transactions by Customer for a Specific Month"),
    WITHDRAWALS_OUTSIDE_CITY("Large Withdrawals Outside Origin City"),

    MONTHLY_STATEMENT("Statements by Customer for every Month");

    private final String description;

    ReportTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
