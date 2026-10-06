package com.example.freight_anomaly_detector.service;


import com.example.freight_anomaly_detector.model.Shipment;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AggregationService {

    public static class WeeklyData {

        private String route;
        private String routeType;
        private LocalDate weekOf;

        private double totalFreightCost;
        private double totalQuantityDistance;

        public WeeklyData(
                String route,
                String routeType,
                LocalDate weekOf) {

            this.route = route;
            this.routeType = routeType;
            this.weekOf = weekOf;
        }

        public void addShipment(Shipment shipment) {

            totalFreightCost += shipment.getFreightCostInr();

            totalQuantityDistance +=
                    shipment.getQuantityTonnes()
                            * shipment.getDistanceKm();
        }

        public double getCostPerTonneKm() {

            if (totalQuantityDistance == 0) {
                return 0;
            }

            return totalFreightCost / totalQuantityDistance;
        }

        public String getRoute() {
            return route;
        }

        public String getRouteType() {
            return routeType;
        }

        public LocalDate getWeekOf() {
            return weekOf;
        }
    }


    public List<WeeklyData> aggregate(
            List<Shipment> shipments) {

        Map<String, WeeklyData> map = new HashMap<>();

        for (Shipment shipment : shipments) {

            LocalDate weekOf = shipment.getShipmentDate()
                    .with(
                            TemporalAdjusters.previousOrSame(
                                    DayOfWeek.MONDAY
                            )
                    );

            String route = shipment.getRoute();

            String key =
                    route + "|" +
                    shipment.getRouteType() + "|" +
                    weekOf;

            WeeklyData data = map.computeIfAbsent(
                    key,
                    k -> new WeeklyData(
                            route,
                            shipment.getRouteType(),
                            weekOf
                    )
            );

            data.addShipment(shipment);
        }

        return map.values()
                .stream()
                .sorted(
                        Comparator
                                .comparing(WeeklyData::getRoute)
                                .thenComparing(WeeklyData::getWeekOf)
                )
                .collect(Collectors.toList());
    }
}