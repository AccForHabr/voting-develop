package com.neoquest.voting.model.entity;

import java.util.ArrayList;
import java.util.List;

public class ReportsResponse extends Response<ReportsResponse.ReportsEntity> {

    public static final class ReportsEntity {
        private List<Report> reports = new ArrayList<>();
        private String k;
        private String publicKey;

        public List<Report> getReports() {
            return reports;
        }

        public void setReports(List<Report> reports) {
            this.reports = reports;
        }

        public String getK() {
            return k;
        }

        public void setK(String k) {
            this.k = k;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }
    }
}
