package com.neoquest.voting.eventbus;

public class VoteEvent extends Event {
    public VoteEvent(boolean success) {
        super(success);
    }

    public VoteEvent(boolean success, String errorMessage) {
        super(success, errorMessage);
    }
}
