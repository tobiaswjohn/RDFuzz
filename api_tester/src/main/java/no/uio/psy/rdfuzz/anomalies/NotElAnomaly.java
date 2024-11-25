package no.uio.psy.rdfuzz.anomalies;

public class NotElAnomaly extends Anomaly {
    @Override
    public String toString() {
        return "Anomaly: not in EL";
    }

    @Override
    public AnomalySummary getSummary() {
        AnomalySummary as = new AnomalySummary();
        as.inEL = false;
        return as;
    }
}
