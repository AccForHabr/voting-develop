package com.neoquest.voting.model.entity;

public class Response<T> {
    private boolean success = false;
    private String message;
    private String exception;
    private T data = null;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExceptionMessage() {
        return exception;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exception = exceptionMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
