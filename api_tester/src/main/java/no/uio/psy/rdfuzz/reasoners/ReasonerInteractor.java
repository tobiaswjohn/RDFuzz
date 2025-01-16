package no.uio.psy.rdfuzz.reasoners;

import no.uio.psy.rdfuzz.anomalies.ResultWithAnomaly;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.Set;

// interface to call reasoners
public interface ReasonerInteractor {

    ResultWithAnomaly<Boolean> isConsistent();

    ResultWithAnomaly<Set<OWLAxiom>> inferredAxioms();

    ResultWithAnomaly<Set<OWLAxiom>> classHierarchy();

}
