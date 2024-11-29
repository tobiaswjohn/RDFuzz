package no.uio.psy.rdfuzz.anomalies;

import no.uio.psy.rdfuzz.SUT;

// not a "real" anomaly
// indicates, that the result could not be computed, because reasoner for sut could not be created in the first place
public class NotCreatedAnomaly extends  Anomaly{
    private  final SUT sut;

    public NotCreatedAnomaly(SUT sut) {
        this.sut = sut;
    }

    @Override
    public String toString() {
        return "helper anomaly; reasoner for " + sut + " could not be created";
    }
}
