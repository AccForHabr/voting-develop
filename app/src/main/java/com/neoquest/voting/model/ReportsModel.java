package com.neoquest.voting.model;

import com.neoquest.voting.model.entity.Report;

import java.util.ArrayList;
import java.util.List;

public enum ReportsModel {
    INSTANCE;

    private List<Report> reportsList = new ArrayList<>();
    private Integer votesCommonCount = 0;

    public List<Report> getReportsList() {
        return reportsList;
    }

    public synchronized void setReportsList(List<Report> reportsList) {
        this.reportsList.clear();
        this.reportsList.addAll(reportsList);
        votesCommonCount = 0;
        for (Report report : reportsList) {
            votesCommonCount = votesCommonCount + report.getVotesCount();
        }
    }

    public Integer getVotesCommonCount() {
        return votesCommonCount;
    }
}
