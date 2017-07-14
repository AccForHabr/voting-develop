package com.neoquest.voting.eventbus;

public abstract class Event {
    private boolean success;
    private String errorMessage;

    public Event(boolean success) {
        this.success = success;
    }

    public Event(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
