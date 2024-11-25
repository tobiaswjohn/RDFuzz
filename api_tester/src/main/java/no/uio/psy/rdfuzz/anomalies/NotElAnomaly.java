package no.uio.psy.rdfuzz.anomalies;

import org.apache.jena.iri.Violation;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;

import java.util.List;

public class NotElAnomaly extends Anomaly {

    private final List<OWLProfileViolation> violations;

    public NotElAnomaly(List<OWLProfileViolation> violations) {
        this.violations = violations;
    }

    @Override
    public String toString() {
        StringBuilder message = new StringBuilder("Anomaly: not in EL\n");
        for (OWLProfileViolation v : violations)
            message.append(" ").append(v.toString()).append("\n");

        return message.toString();
    }

    @Override
    public AnomalySummary getSummary() {
        AnomalySummary as = new AnomalySummary();
        as.inEL = false;
        return as;
    }
}
