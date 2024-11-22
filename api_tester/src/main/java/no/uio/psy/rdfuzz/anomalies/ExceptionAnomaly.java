package no.uio.psy.rdfuzz.anomalies;

public class ExceptionAnomaly extends Anomaly {
    private final Exception e;

    public ExceptionAnomaly(Exception e) {
        this.e = e;
    }

    @Override
    public String toString() {
        return "Anomalie with following exception:\n" + e ;
    }
}
