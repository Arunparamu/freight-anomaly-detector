package com.example.freight_anomaly_detector.service;



import com.example.freight_anomaly_detector.model.ContextNote;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ContextService {

    public ContextNote findMatchingNote(
            String route,
            LocalDate weekOf,
            List<ContextNote> notes) {

        LocalDate weekEnd = weekOf.plusDays(6);

        for (ContextNote note : notes) {

            boolean routeMatches =
                    note.getAppliesTo().equalsIgnoreCase(route)
                    ||
                    note.getAppliesTo().equalsIgnoreCase("All Routes");

            if (!routeMatches) {
                continue;
            }

            boolean dateMatches =
                    !note.getDate().isBefore(weekOf)
                    &&
                    !note.getDate().isAfter(weekEnd);

            if (!dateMatches) {
                continue;
            }

    
            String text =
                    note.getNote().toLowerCase();

            if (text.contains("not significantly affected")
                    || text.contains("no rate change")
                    || text.contains("no major disruptions")
                    || text.contains("remained normal")
                    || text.contains("costs were not")) {

                continue;
            }

            return note;
        }

        return null;
    }
}