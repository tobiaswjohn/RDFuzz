package no.uio.psy.rdfuzz.anomalies;

import no.uio.psy.rdfuzz.SUT;

// an anomalie that results from different SUTs having different results
public class DifferentialAnomaly extends Anomaly {
    protected final SUT sut1;
    protected final SUT sut2;

    DifferentialAnomaly(SUT _sut1, SUT _sut2) {
        sut1 = _sut1;
        sut2 = _sut2;
    }
    @Override
    public String toString() {
        return "Differential anomaly between " + sut1 + " and " + sut2;
    }
}
