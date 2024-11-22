package no.uio.psy.rdfuzz.anomalies;

import no.uio.psy.rdfuzz.SUT;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.Set;

public class InferenceAnomaly extends DifferentialAnomaly{
    Set<OWLAxiom> additional1;
    Set<OWLAxiom> additional2;

    public InferenceAnomaly(SUT sut1,
                            SUT sut2,
                            Set<OWLAxiom> additional1,
                            Set<OWLAxiom> additional2) {
        super(sut1, sut2);
        this.additional1 = additional1;
        this.additional2 = additional2;
    }

    @Override
    public String toString() {
        return "Inference differentiation between "+ sut1 + " and "+ sut2 + ":\n\t" +
                "additional axioms " + sut1 + ": " + additional1 + "\n\t" +
                "additional axioms " + sut2 + ": " + additional2;
    }
}
