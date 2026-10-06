package com.example.freight_anomaly_detector.service;


import com.example.freight_anomaly_detector.model.ContextNote;
import com.example.freight_anomaly_detector.model.Shipment;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import org.springframework.stereotype.Service;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvService {

    public List<Shipment> readShipments(String filePath) {

        List<Shipment> shipments = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(Path.of(filePath))) {

            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            Iterable<CSVRecord> records = format.parse(reader);

            for (CSVRecord record : records) {

                Shipment shipment = new Shipment();

                shipment.setShipmentId(record.get("shipment_id"));
                shipment.setOrigin(record.get("origin"));
                shipment.setDestination(record.get("destination"));
                shipment.setRouteType(record.get("route_type"));
                shipment.setMaterial(record.get("material"));

                shipment.setQuantityTonnes(
                        Double.parseDouble(record.get("quantity_tonnes"))
                );

                shipment.setDistanceKm(
                        Double.parseDouble(record.get("distance_km"))
                );

                shipment.setFreightCostInr(
                        Double.parseDouble(record.get("freight_cost_inr"))
                );

                shipment.setShipmentDate(
                        LocalDate.parse(record.get("shipment_date"))
                );

                shipment.setTransporter(record.get("transporter"));

                shipments.add(shipment);
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to read shipment CSV", e
            );
        }

        return shipments;
    }


    public List<ContextNote> readContextNotes(String filePath) {

        List<ContextNote> notes = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(Path.of(filePath))) {

            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            Iterable<CSVRecord> records = format.parse(reader);

            for (CSVRecord record : records) {

                ContextNote note = new ContextNote();

                note.setNoteId(record.get("note_id"));

                note.setDate(
                        LocalDate.parse(record.get("date"))
                );

                note.setAppliesTo(
                        record.get("applies_to")
                );

                note.setNote(
                        record.get("note")
                );

                notes.add(note);
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to read context notes CSV", e
            );
        }

        return notes;
    }
}