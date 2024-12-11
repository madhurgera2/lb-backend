package com.application.constants;

public enum RequestStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String status;

    RequestStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public static RequestStatus fromString(String status) {
        for (RequestStatus requestStatus : RequestStatus.values()) {
            if (requestStatus.status.equalsIgnoreCase(status)) {
                return requestStatus;
            }
        }
        throw new IllegalArgumentException("No matching enum constant for " + status);
    }
}
