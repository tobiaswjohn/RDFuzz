package no.uio.psy.rdfuzz.anomalies;

public class ExceptionAnomaly extends Anomaly {
    private final Exception e;

    public ExceptionAnomaly(Exception e) {
        this.e = e;
    }

    @Override
    public AnomalySummary getSummary() {
        AnomalySummary as = new AnomalySummary();
        as.exceptions = 1;
        return as;
    }

    @Override
    public String toString() {
        return "Anomaly with following exception:\n" + e ;
    }
}
