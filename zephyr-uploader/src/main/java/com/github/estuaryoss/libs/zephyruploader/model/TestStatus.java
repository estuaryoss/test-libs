package com.github.estuaryoss.libs.zephyruploader.model;

public enum TestStatus {
    NOT_EXECUTED(-1),
    FAILED(2),
    PASSED(1);

    private int id;

    private TestStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
