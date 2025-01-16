package no.uio.psy.rdfuzz.reasoners;

import no.uio.psy.rdfuzz.SUT;
import no.uio.psy.rdfuzz.anomalies.ResultWithAnomaly;
import no.uio.psy.rdfuzz.anomalies.ResultWithAnomalyFactory;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.*;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

// class to call the reasoners
public class ReasonerCaller implements ReasonerInteractor {

    private final OWLReasoner reasoner;
    private final OWLOntologyManager manager;
    private final SUT sut;

    private final OWLAxiom inconsistent;

    // generators used for inferred axioms
    private final List<InferredAxiomGenerator<? extends OWLAxiom>> gens;

    // factory for results with anomaly
    private final ResultWithAnomalyFactory resultWithAnomalyFactory = new ResultWithAnomalyFactory();

    ReasonerCaller(OWLReasoner reasoner, SUT sut)  {
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

    public ResultWithAnomaly<Boolean> isConsistent() {
        try {
            boolean consistent = reasoner.isConsistent();
            return new ResultWithAnomaly<>(consistent, sut);
        } catch (Exception e) {
            return resultWithAnomalyFactory.getFalseWithException(sut, e);
        }
    }

    // computes all subclass axioms
    // method is inspired by implementation in https://github.com/ykazakov/ore-2015-competition-framework/tree/master
    // from Class "OREv2ReasonerWrapper"
    public ResultWithAnomaly<Set<OWLAxiom>> classHierarchy() {
        InferredSubClassAxiomGenerator subClassGenerator = new InferredSubClassAxiomGenerator();
        InferredEquivalentClassAxiomGenerator equivClassGenerator = new InferredEquivalentClassAxiomGenerator();
        Set<OWLAxiom> resultAxioms = new HashSet<OWLAxiom>();

        try {
            if (reasoner.isConsistent()) {
                reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
                Set<OWLSubClassOfAxiom> subClassAxioms = subClassGenerator.createAxioms(manager.getOWLDataFactory(), reasoner);
                Set<OWLEquivalentClassesAxiom> equivClassAxioms = equivClassGenerator.createAxioms(manager.getOWLDataFactory(), reasoner);
                resultAxioms.addAll(subClassAxioms);
                resultAxioms.addAll(equivClassAxioms);
                return new ResultWithAnomaly<>(resultAxioms, sut);
            } else
                return new ResultWithAnomaly<>(Set.of(inconsistent), sut);
        } catch (Exception e) {
            return resultWithAnomalyFactory.getEmptyAxiomSetWithException(sut, e);
        }
    }


    public ResultWithAnomaly<Set<OWLAxiom>> inferredAxioms() {
        try {
            if (reasoner.isConsistent()) {
                InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
                OWLOntology infOnt = manager.createOntology();
                iog.fillOntology(manager.getOWLDataFactory(), infOnt);
                return new ResultWithAnomaly<>(infOnt.getAxioms(), sut);
            }
            else
                return new ResultWithAnomaly<>(Set.of(inconsistent), sut);
        } catch (Exception e) {
            return resultWithAnomalyFactory.getEmptyAxiomSetWithException(sut, e);
        }
    }

    // returns instances provided by a class expression
    public ResultWithAnomaly<Set<OWLNamedIndividual>> getInstances(OWLClassExpression classExpression) {
        try {
            NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(classExpression);
            return new ResultWithAnomaly<>(
                    individuals.getFlattened(),
                    sut
            );
        } catch (Exception e) {
            return resultWithAnomalyFactory.getEmptyIndividualSetWithException(sut, e);
        }
    }
}
