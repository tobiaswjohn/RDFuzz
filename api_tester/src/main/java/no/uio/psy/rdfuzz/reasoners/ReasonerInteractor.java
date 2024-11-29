package no.uio.psy.rdfuzz.reasoners;

import no.uio.psy.rdfuzz.anomalies.ResultWithAnomalie;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.Set;

// interface to call reasoners
public interface ReasonerInteractor {

    ResultWithAnomalie<Boolean> isConsistent();

    ResultWithAnomalie<Set<OWLAxiom>> inferredAxioms();

}
