package com.example.freight_anomaly_detector.service;

import com.example.freight_anomaly_detector.model.AnomalyResult;
import com.example.freight_anomaly_detector.model.ContextNote;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class AnomalyService {

    /*
     * The sample output strongly indicates a 20% threshold.
     *
     * Example:
     * Delhi-Jaipur:
     * +35.5% history
     * +21.0% peers
     * => flagged
     *
     * Mumbai-Pune:
     * +9.2% history
     * +23.6% peers
     * => flagged
     */
    private static final double ANOMALY_THRESHOLD = 20.0;


    private final ContextService contextService;
    private final LlmService llmService;


    public AnomalyService(
            ContextService contextService,
            LlmService llmService) {

        this.contextService = contextService;
        this.llmService = llmService;
    }


    public List<AnomalyResult> analyze(
            List<AggregationService.WeeklyData> weeklyData,
            List<ContextNote> notes) {

        List<AnomalyResult> results = new ArrayList<>();

        for (AggregationService.WeeklyData current : weeklyData) {

            double currentCost =
                    current.getCostPerTonneKm();


            // -----------------------------------------
            // 1. OWN HISTORY - PREVIOUS 8 WEEKS
            // -----------------------------------------

            List<Double> previousWeeks =
                    weeklyData.stream()

                            .filter(w ->
                                    w.getRoute()
                                            .equals(current.getRoute())
                            )

                            .filter(w ->
                                    w.getWeekOf()
                                            .isBefore(current.getWeekOf())
                            )

                            .sorted(
                                    Comparator.comparing(
                                            AggregationService.WeeklyData
                                                    ::getWeekOf
                                    ).reversed()
                            )

                            .limit(8)

                            .map(
                                    AggregationService.WeeklyData
                                            ::getCostPerTonneKm
                            )

                            .toList();


            double historyAverage =
                    previousWeeks.stream()
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0);


            Double historyPercentage = null;

            if (historyAverage > 0) {

                historyPercentage =
                        ((currentCost - historyAverage)
                                / historyAverage) * 100;
            }


            // -----------------------------------------
            // 2. SIMILAR ROUTES
            // SAME WEEK + SAME ROUTE TYPE
            // EXCLUDE CURRENT ROUTE
            // -----------------------------------------

            List<Double> peerCosts =
                    weeklyData.stream()

                            .filter(w ->
                                    w.getWeekOf()
                                            .equals(current.getWeekOf())
                            )

                            .filter(w ->
                                    w.getRouteType()
                                            .equals(current.getRouteType())
                            )

                            .filter(w ->
                                    !w.getRoute()
                                            .equals(current.getRoute())
                            )

                            .map(
                                    AggregationService.WeeklyData
                                            ::getCostPerTonneKm
                            )

                            .toList();


            double peerAverage =
                    peerCosts.stream()
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0);


            Double peerPercentage = null;

            if (peerAverage > 0) {

                peerPercentage =
                        ((currentCost - peerAverage)
                                / peerAverage) * 100;
            }


            // -----------------------------------------
            // 3. DETERMINE ANOMALY
            // -----------------------------------------

            boolean historyAnomaly =
                    historyPercentage != null
                    &&
                    historyPercentage >= ANOMALY_THRESHOLD;


            boolean peerAnomaly =
                    peerPercentage != null
                    &&
                    peerPercentage >= ANOMALY_THRESHOLD;


            boolean anomaly =
                    historyAnomaly || peerAnomaly;


            // -----------------------------------------
            // 4. CHECK CONTEXT NOTES
            // -----------------------------------------

            ContextNote matchingNote = null;

            if (anomaly) {

                matchingNote =
                        contextService.findMatchingNote(
                                current.getRoute(),
                                current.getWeekOf(),
                                notes
                        );
            }


            // -----------------------------------------
            // 5. BUILD RESULT
            // -----------------------------------------

            AnomalyResult result =
                    new AnomalyResult();

            result.setRoute(current.getRoute());

            result.setWeekOf(current.getWeekOf());

            result.setCostPerTonneKm(
                    currentCost
            );


            result.setVsOwnHistory(
                    formatPercentage(
                            historyPercentage,
                            "this route's past average"
                    )
            );


            result.setVsSimilarRoutes(
                    formatPercentage(
                            peerPercentage,
                            "similar-length routes this week"
                    )
            );


            if (!anomaly) {

                result.setFlagged("No");

                result.setMatchedNoteId("");

                result.setReason(
                        "Cost is within the expected range "
                        + "compared with historical and peer baselines."
                );

            } else if (matchingNote != null) {

                result.setFlagged(
                        "No (justified)"
                );

                result.setMatchedNoteId(
                        matchingNote.getNoteId()
                );

                result.setReason(
                        llmService.generateReason(
                                matchingNote,
                                true
                        )
                );

            } else {

                result.setFlagged("Yes");

                result.setMatchedNoteId("");

                result.setReason(
                        llmService.generateReason(
                                null,
                                false
                        )
                );
            }

            results.add(result);
        }

        return results;
    }


    private String formatPercentage(
            Double percentage,
            String comparison) {

        if (percentage == null) {
            return "N/A";
        }

        return String.format(
                Locale.US,
                "%+.1f%% vs %s",
                percentage,
                comparison
        );
    }
}