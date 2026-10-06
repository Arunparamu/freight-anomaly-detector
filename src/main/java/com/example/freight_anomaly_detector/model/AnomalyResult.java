package com.example.freight_anomaly_detector.model;

import java.time.LocalDate;

public class AnomalyResult {

    private String route;
    private LocalDate weekOf;

    private double costPerTonneKm;

    private String vsOwnHistory;
    private String vsSimilarRoutes;

    private String flagged;

    private String matchedNoteId;
    private String reason;

    public AnomalyResult() {
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public LocalDate getWeekOf() {
        return weekOf;
    }

    public void setWeekOf(LocalDate weekOf) {
        this.weekOf = weekOf;
    }

    public double getCostPerTonneKm() {
        return costPerTonneKm;
    }

    public void setCostPerTonneKm(double costPerTonneKm) {
        this.costPerTonneKm = costPerTonneKm;
    }

    public String getVsOwnHistory() {
        return vsOwnHistory;
    }

    public void setVsOwnHistory(String vsOwnHistory) {
        this.vsOwnHistory = vsOwnHistory;
    }

    public String getVsSimilarRoutes() {
        return vsSimilarRoutes;
    }

    public void setVsSimilarRoutes(String vsSimilarRoutes) {
        this.vsSimilarRoutes = vsSimilarRoutes;
    }

    public String getFlagged() {
        return flagged;
    }

    public void setFlagged(String flagged) {
        this.flagged = flagged;
    }

    public String getMatchedNoteId() {
        return matchedNoteId;
    }

    public void setMatchedNoteId(String matchedNoteId) {
        this.matchedNoteId = matchedNoteId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}