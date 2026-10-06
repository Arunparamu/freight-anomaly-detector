package com.example.freight_anomaly_detector.model;

import java.time.LocalDate;

public class ContextNote {

    private String noteId;
    private LocalDate date;
    private String appliesTo;
    private String note;

    public ContextNote() {
    }

    public ContextNote(
            String noteId,
            LocalDate date,
            String appliesTo,
            String note) {

        this.noteId = noteId;
        this.date = date;
        this.appliesTo = appliesTo;
        this.note = note;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(String appliesTo) {
        this.appliesTo = appliesTo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}