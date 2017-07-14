package com.neoquest.voting.model.entity;

public class Report {
    private int    id;
    private String name;
    private String author;
    private String description;
    private String hashValue;
    private int    votesCount = 0;

    public Report() {

    }

    public Report(int id, String author, String name, String description, Integer votesCount) {
        this.id = id;
        this.author = author;
        this.name = name;
        this.description = description;
        this.votesCount = votesCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVotesCount() {
        return votesCount;
    }

    public void setVotesCount(Integer votesCount) {
        this.votesCount = votesCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

    public void setVotesCount(int votesCount) {
        this.votesCount = votesCount;
    }
}
