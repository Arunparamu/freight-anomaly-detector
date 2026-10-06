package com.example.freight_anomaly_detector.controller;


import com.example.freight_anomaly_detector.model.AnomalyResult;
import com.example.freight_anomaly_detector.model.ContextNote;
import com.example.freight_anomaly_detector.model.Shipment;

import com.example.freight_anomaly_detector.service.AggregationService;
import com.example.freight_anomaly_detector.service.AnomalyService;
import com.example.freight_anomaly_detector.service.CsvService;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
public class AnalysisController {

    private final CsvService csvService;
    private final AggregationService aggregationService;
    private final AnomalyService anomalyService;


    public AnalysisController(CsvService csvService,AggregationService aggregationService,AnomalyService anomalyService) {
        this.csvService = csvService;
        this.aggregationService = aggregationService;
        this.anomalyService = anomalyService;
    }


    @GetMapping("/api/analyze")
    public String analyze() {

        String shipmentFile ="data/shipment_records.csv";

        String contextFile ="data/context_notes.csv";

        String outputFile ="output/output.csv";

        List<Shipment> shipments =csvService.readShipments(
                        shipmentFile
                );

        List<ContextNote> notes =csvService.readContextNotes(
                        contextFile
                );
        List<AggregationService.WeeklyData> weeklyData =aggregationService.aggregate(
                        shipments
                );

        List<AnomalyResult> results = anomalyService.analyze(
                        weeklyData,
                        notes
                );

        writeOutput(
                outputFile,
                results
        );


        return "Analysis completed successfully. "
                + "Generated "
                + results.size()
                + " weekly route records at "
                + outputFile;
    }


    private void writeOutput(
            String filePath,
            List<AnomalyResult> results) {

        try {

            Path path =
                    Path.of(filePath);

            Files.createDirectories(
                    path.getParent()
            );


            try (
                    BufferedWriter writer =
                            Files.newBufferedWriter(path);

                    CSVPrinter printer =
                            new CSVPrinter(
                                    writer,
                                    CSVFormat.DEFAULT
                                            .builder()
                                            .setHeader(
                                                    "route",
                                                    "week_of",
                                                    "cost_per_tonne_km",
                                                    "vs_own_history",
                                                    "vs_similar_routes",
                                                    "flagged",
                                                    "matched_note_id",
                                                    "reason"
                                            )
                                            .build()
                            )
            ) {

                for (AnomalyResult result : results) {

                    printer.printRecord(
                            result.getRoute(),

                            result.getWeekOf(),

                            String.format(
                                    "%.2f",
                                    result.getCostPerTonneKm()
                            ),

                            result.getVsOwnHistory(),

                            result.getVsSimilarRoutes(),

                            result.getFlagged(),

                            result.getMatchedNoteId(),

                            result.getReason()
                    );
                }
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to create output CSV",
                    e
            );
        }
    }
}
