# Freight Anomaly Detector

An AI-assisted freight cost anomaly detection system built using **Java 17 and Spring Boot**.

The system analyzes shipment records on a weekly basis, calculates normalized freight costs, compares them against historical and peer-route baselines, and identifies unusual cost increases. Operational context notes are then used to determine whether an anomaly is justified or requires human review.

---

##  Problem Statement

Freight costs can vary because of:

- Fuel price changes
- Seasonal demand
- Festivals and holidays
- Weather conditions
- Toll changes
- Route disruptions
- Temporary operational surcharges

The goal of this project is to automatically identify **unusual freight cost increases** and determine whether there is a valid operational explanation.

The system produces a structured CSV report containing:

- Route
- Week
- Cost per tonne-km
- Historical comparison
- Peer-route comparison
- Anomaly status
- Matching context note
- Explanation

---



##  Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Core application |
| Spring Boot | Application framework |
| Apache Commons CSV | CSV parsing and generation |
| Maven | Dependency management |
| Java Streams | Data processing |
| Java Collections | Aggregation and comparison |
| LocalDate | Date and weekly calculations |

---
# Example Workflow

```text
2,940 shipment records
          ↓
Weekly aggregation
          ↓
728 route-week records
          ↓
Calculate cost/tonne-km
          ↓
Compare with previous 8 weeks
          ↓
Compare with similar routes
          ↓
Detect anomalies
          ↓
Check operational context
          ↓
Generate final CSV
```

##  Project Structure

```text
freight-anomaly-detector/
│
├── data/
│   ├── shipment_records.csv
│   └── context_notes.csv
│
├── output/
│   └── output.csv
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── example/
│                   └── freight/
│                       ├── FreightApplication.java
│                       │
│                       ├── model/
│                       │   ├── Shipment.java
│                       │   ├── ContextNote.java
│                       │   └── AnomalyResult.java
│                       │
│                       ├── service/
│                       │   ├── CsvService.java
│                       │   ├── AggregationService.java
│                       │   ├── AnomalyService.java
│                       │   ├── ContextService.java
│                       │   └── LlmService.java
│                       │
│                       └── controller/
│                           └── AnalysisController.java
│
├── pom.xml
└── README.md
```

---

#  Data Processing

## 1. Weekly Aggregation

Shipment records are grouped using:

```text
route + route_type + week
```

The week starts on **Monday**.

For example:

```text
2025-01-06
2025-01-07
2025-01-09
```

belong to:

```text
2025-01-06
```

---

## 2. Cost per Tonne-km

The normalized freight cost is calculated as:

```text
Cost per tonne-km =
Total Freight Cost
------------------------------
Total Quantity × Distance
```

In Java:

```java
double costPerTonneKm =
        totalFreightCost / totalQuantityDistance;
```

This normalization allows different shipment sizes and distances to be compared fairly.

---

#  Anomaly Detection

The system uses two independent baselines.

## 1. Own Route History

The current week's cost is compared against the **previous 8 weeks** of the same route.

```text
Historical Average =
Average of previous 8 weekly route costs
```

Percentage difference:

```text
(Current Cost - Historical Average)
------------------------------------ × 100
Historical Average
```

---

## 2. Similar Routes

The current route is compared with other routes having the same:

```text
week
+
route_type
```

The current route itself is excluded from the peer calculation.

Percentage difference:

```text
(Current Cost - Peer Average)
----------------------------- × 100
Peer Average
```

---

##  Anomaly Threshold

A route is considered suspicious when its cost increase crosses the configured anomaly threshold.

The current implementation uses:

```text
20%
```

as the threshold.

The threshold can easily be changed in:

```text
AnomalyService.java
```

```java
private static final double ANOMALY_THRESHOLD = 20.0;
```

---

#  Context Validation

After detecting an anomaly, the system searches `context_notes.csv`.

A context note is considered relevant when:

1. The note applies to the route or all routes.
2. The note date falls within the affected week.
3. The note indicates an actual operational reason for a cost change.

Notes that explicitly indicate no significant cost impact are ignored.

This prevents the system from incorrectly using unrelated notes as explanations.

---


# Output

The application generates:

```text
output/output.csv
```

with the following columns:

```text
route
week_of
cost_per_tonne_km
vs_own_history
vs_similar_routes
flagged
matched_note_id
reason
```

Example:

```text
route: Ahmedabad-Mumbai
week_of: 2025-01-20
cost_per_tonne_km: 3.29
vs_own_history: +29.5%
vs_similar_routes: +22.5%
flagged: No (justified)
matched_note_id: N002
```

---

#  How to Run

## Prerequisites

Install:

- Java 17+
- Maven 3.8+
- Git

Verify:

```bash
java -version
```

and:

```bash
mvn -version
```

---

## Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/freight-anomaly-detector.git
```

Navigate into the project:

```bash
cd freight-anomaly-detector
```

---

## Run Application

```bash
mvn spring-boot:run
```

The application starts on:

```text
http://localhost:8080
```

---

## Run Analysis

Open:

```text
http://localhost:8080/api/analyze
```

The application will process the shipment data and generate:

```text
output/output.csv
```

---

#  Current Result

The current implementation successfully processes the provided shipment dataset and generates:

```text
728 weekly route records
```

The generated file is:

```text
output/output.csv
```

---






