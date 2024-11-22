package no.uio.psy.rdfuzz.reasoners;

import no.uio.psy.rdfuzz.SUT;
import no.uio.psy.rdfuzz.anomalies.Anomaly;
import no.uio.psy.rdfuzz.anomalies.ExceptionAnomaly;
import no.uio.psy.rdfuzz.anomalies.ResultWithAnomalie;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.*;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

// class to call the reasoners
public class ReasonerCaller {

    private final OWLReasoner reasoner;
    private final OWLOntologyManager manager;
    private final SUT sut;

    // generators used for inferred axioms
    private final List<InferredAxiomGenerator<? extends OWLAxiom>> gens;

    ReasonerCaller(OWLReasoner _reasoner, SUT sut) {
        reasoner = _reasoner;
        manager = _reasoner.getRootOntology().getOWLOntologyManager();
        this.sut = sut;
        gens = List.of(
                new InferredPropertyAssertionGenerator(),
                new InferredClassAssertionAxiomGenerator(),
                new InferredSubClassAxiomGenerator(),
                new InferredDisjointClassesAxiomGenerator(),
                new InferredEquivalentClassAxiomGenerator(),
                new InferredEquivalentDataPropertiesAxiomGenerator(),
                new InferredEquivalentObjectPropertyAxiomGenerator(),
                new InferredInverseObjectPropertiesAxiomGenerator(),
                new InferredObjectPropertyCharacteristicAxiomGenerator(),
                new InferredSubDataPropertyAxiomGenerator(),
                new InferredDataPropertyCharacteristicAxiomGenerator(),
                new InferredObjectPropertyCharacteristicAxiomGenerator(),
                new InferredSubObjectPropertyAxiomGenerator()
        );
    }

    ReasonerCaller(OWLReasoner _reasoner,
                   SUT sut,
                   List<InferredAxiomGenerator<? extends OWLAxiom>> _gens) {
        reasoner = _reasoner;
        manager = _reasoner.getRootOntology().getOWLOntologyManager();
        this.sut = sut;
        gens = _gens;
    }

    public ResultWithAnomalie<Boolean> isConsistent() {
        try {
            boolean consistent = reasoner.isConsistent();
            return new ResultWithAnomalie<>(consistent, Set.of(), sut);
        } catch (Exception e) {
            return  new ResultWithAnomalie<>(
                    false,
                    Set.of(new ExceptionAnomaly(e)),
                    sut
            );
        }
    }

    public Set<OWLAxiom> inferredAxioms() throws OWLOntologyCreationException {
        InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
        OWLOntology infOnt = manager.createOntology();
        iog.fillOntology(manager.getOWLDataFactory(), infOnt);
        return infOnt.getAxioms();
    }
}
