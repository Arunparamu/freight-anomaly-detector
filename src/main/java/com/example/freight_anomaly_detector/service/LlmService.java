package com.example.freight_anomaly_detector.service;



import com.example.freight_anomaly_detector.model.ContextNote;

import org.springframework.stereotype.Service;

@Service
public class LlmService {

    public String generateReason(
            ContextNote note,
            boolean justified) {

        if (justified && note != null) {

            return "Matches note "
                    + note.getNoteId()
                    + " dated "
                    + note.getDate()
                    + ": "
                    + note.getNote();
        }

        return "No matching note found for this route or date range. "
                + "Cost rise looks unexplained and is worth a human review.";
    }
}
