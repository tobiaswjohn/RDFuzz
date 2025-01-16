package no.uio.psy.rdfuzz.anomalies;

import no.uio.psy.rdfuzz.SUT;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.Set;

public class ClassHierarchyAnomaly extends InferenceAnomaly {

    @Override
    protected String getName() {
        return "Class hierarchy differentiation";
    }


    public ClassHierarchyAnomaly(SUT sut1, SUT sut2, Set<OWLAxiom> additional1, Set<OWLAxiom> additional2) {
        super(sut1, sut2, additional1, additional2);
    }

    @Override
    public AnomalySummary getSummary() {
        AnomalySummary as = new AnomalySummary();
        as.hierarchyAnomalies = 1;
        return as;
    }


}
