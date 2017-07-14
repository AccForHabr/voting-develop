package com.neoquest.voting.eventbus;

public class GetReportsEvent extends Event {
    public GetReportsEvent(boolean success) {
        super(success);
    }

    public GetReportsEvent(boolean success, String errorMessage) {
        super(success, errorMessage);
    }
}
