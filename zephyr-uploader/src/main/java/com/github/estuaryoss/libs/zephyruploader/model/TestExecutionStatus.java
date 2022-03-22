package com.github.estuaryoss.libs.zephyruploader.model;

public enum TestExecutionStatus {
    SUCCESS("SUCCESS"),
    FAILURE("FAILURE"),
    SKIPPED("SKIPPED");

    private String status;

    private TestExecutionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
