package no.uio.psy.rdfuzz.anomalies;

import no.uio.psy.rdfuzz.SUT;

public class ExceptionAnomaly extends Anomaly {
    private final Exception e;
    protected final SUT sut;

    public ExceptionAnomaly(Exception e, SUT sut) {
        this.e = e;
        this.sut = sut;
    }

    @Override
    public AnomalySummary getSummary() {
        AnomalySummary as = new AnomalySummary();
        as.exceptions = 1;
        return as;
    }

    @Override
    public String toString() {
        return "Anomaly with following exception thrown by " + sut + ":\n" + e ;
    }
}
