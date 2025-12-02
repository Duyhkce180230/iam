package com.example.identity.enums;

public enum Permissions {
    READ_ONLY("Read-only"),
    CREATE_TEST_ORDER("Create test order"),
    MODIFY_TEST_ORDER("Modify test order"),
    DELETE_TEST_ORDER("Delete test order"),
    REVIEW_TEST_ORDER("Review test order"),
    ADD_COMMENT("Add comment"),
    MODIFY_COMMENT("Modify comment"),
    DELETE_COMMENT("Delete comment"),
    VIEW_USER("View user"),
    CREATE_USER("Create user"),
    MODIFY_USER("Modify user"),
    DELETE_USER("Delete user"),
    LOCK_UNLOCK_USER("Lock and unlock user"),
    VIEW_ROLE("View role"),
    CREATE_ROLE("Create role"),
    UPDATE_ROLE("Update role"),
    DELETE_ROLE("Delete role"),
    ;

    private final String displayName;

    Permissions(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
