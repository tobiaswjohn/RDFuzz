package no.uio.psy.rdfuzz.anomalies;

import no.uio.psy.rdfuzz.SUT;

// anomaly when one reasoner classifies as consistent and the other not
public class ConsistencyAnomaly extends DifferentialAnomaly {
    boolean isCconsistentSut1;
    boolean isConsistentSut2;

    public ConsistencyAnomaly(SUT _sut1,
                              SUT _sut2,
                              boolean _isConsistentSut1,
                              boolean _isConsistentSut2) {
        super(_sut1, _sut2);
        isCconsistentSut1 = _isConsistentSut1;
        isConsistentSut2 = _isConsistentSut2;
    }

    @Override
    public String toString() {
        return "Consistency differentiation:\n\t" + sut1 + ": " + con(isCconsistentSut1) + ", " + sut2 + ": " + con(isConsistentSut2);
    }

    private String con(boolean isConsistent) {
        if (isConsistent)
            return "consistent";
        else
            return "not consistent";
    }

    @Override
    public AnomalySummary getSummary() {
        AnomalySummary as = new AnomalySummary();
        as.consistencyAnomalies = 1;
        return as;
    }
}
