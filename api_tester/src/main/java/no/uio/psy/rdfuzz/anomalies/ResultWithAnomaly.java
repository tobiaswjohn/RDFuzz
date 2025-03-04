package no.uio.psy.rdfuzz.anomalies;

import no.uio.psy.rdfuzz.SUT;

import java.util.Set;

// represents a result T, together with a report about anomalies
public class ResultWithAnomaly<R> {
    public final R result;   // resutl
    public final Set<Anomaly> foundAnomalies;  // the found anomalies
    public final boolean anyAnomaly;  // indicates if any anomalie was found
    public final SUT sut;   // the sut from which the result was obtained

    public ResultWithAnomaly(R result, Set<Anomaly> foundAnomalies, SUT sut) {
        this.result = result;
        this.foundAnomalies = foundAnomalies;
        this.anyAnomaly = !foundAnomalies.isEmpty();
        this.sut = sut;
    }

    // no anomaly found
    public ResultWithAnomaly(R result, SUT sut) {
        this.result = result;
        this.foundAnomalies = Set.of();
        this.anyAnomaly = false;
        this.sut = sut;
    }
}

