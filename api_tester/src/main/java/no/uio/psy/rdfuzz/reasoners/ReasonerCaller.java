package no.uio.psy.rdfuzz.reasoners;

import no.uio.psy.rdfuzz.SUT;
import no.uio.psy.rdfuzz.anomalies.Anomaly;
import no.uio.psy.rdfuzz.anomalies.ExceptionAnomaly;
import no.uio.psy.rdfuzz.anomalies.ResultWithAnomalie;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
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

    private final OWLAxiom inconsistent;

    // generators used for inferred axioms
    private final List<InferredAxiomGenerator<? extends OWLAxiom>> gens;

    ReasonerCaller(OWLReasoner reasoner, SUT sut) {
        this.reasoner = reasoner;
        this.manager = reasoner.getRootOntology().getOWLOntologyManager();
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
                new InferredSubObjectPropertyAxiomGenerator()
        );
        this.inconsistent = manager.getOWLDataFactory().getOWLSubClassOfAxiom(
                manager.getOWLDataFactory().getOWLThing(),
                manager.getOWLDataFactory().getOWLNothing()
        );
    }

    ReasonerCaller(OWLReasoner reasoner,
                   SUT sut,
                   List<InferredAxiomGenerator<? extends OWLAxiom>> gens) {
        this.reasoner = reasoner;
        this.manager = reasoner.getRootOntology().getOWLOntologyManager();
        this.sut = sut;
        this.gens = gens;
        this.inconsistent = manager.getOWLDataFactory().getOWLSubClassOfAxiom(
                manager.getOWLDataFactory().getOWLThing(),
                manager.getOWLDataFactory().getOWLNothing()
        );
    }

    public ResultWithAnomalie<Boolean> isConsistent() {
        try {
            boolean consistent = reasoner.isConsistent();
            return new ResultWithAnomalie<>(consistent, sut);
        } catch (Exception e) {
            return  new ResultWithAnomalie<>(
                    false,
                    Set.of(new ExceptionAnomaly(e, sut)),
                    sut
            );
        }
    }

    public ResultWithAnomalie<Set<OWLAxiom>> inferredAxioms() {
        try {
            if (reasoner.isConsistent()) {
                InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
                OWLOntology infOnt = manager.createOntology();
                iog.fillOntology(manager.getOWLDataFactory(), infOnt);
                return new ResultWithAnomalie<>(infOnt.getAxioms(), sut);
            }
            else
                return new ResultWithAnomalie<>(Set.of(inconsistent), sut);
        } catch (Exception e) {
            return  new ResultWithAnomalie<>(
                    Set.of(),
                    Set.of(new ExceptionAnomaly(e, sut)),
                    sut
            );
        }
    }

    // returns instances provided by a class expression
    public ResultWithAnomalie<Set<OWLNamedIndividual>> getInstances(OWLClassExpression classExpression) {
        try {
            NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(classExpression);
            return new ResultWithAnomalie<>(
                    individuals.getFlattened(),
                    sut
            );
        } catch (Exception e) {
            return new ResultWithAnomalie<>(
                    Set.of(),
                    Set.of(new ExceptionAnomaly(e, sut)),
                    sut
            );
        }
    }
}
