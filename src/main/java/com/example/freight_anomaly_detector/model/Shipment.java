package com.example.freight_anomaly_detector.model;

import java.time.LocalDate;

public class Shipment {

    private String shipmentId;
    private String origin;
    private String destination;
    private String routeType;
    private String material;

    private double quantityTonnes;
    private double distanceKm;
    private double freightCostInr;

    private LocalDate shipmentDate;
    private String transporter;

    public Shipment() {
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public double getQuantityTonnes() {
        return quantityTonnes;
    }

    public void setQuantityTonnes(double quantityTonnes) {
        this.quantityTonnes = quantityTonnes;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public double getFreightCostInr() {
        return freightCostInr;
    }

    public void setFreightCostInr(double freightCostInr) {
        this.freightCostInr = freightCostInr;
    }

    public LocalDate getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(LocalDate shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public String getTransporter() {
        return transporter;
    }

    public void setTransporter(String transporter) {
        this.transporter = transporter;
    }

    public String getRoute() {
        return origin + "-" + destination;
    }
}