package com.neoquest.voting.eventbus;

public class ErrorEvent {
    private String error;

    public ErrorEvent(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
