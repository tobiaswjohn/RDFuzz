package no.uio.psy.rdfuzz.reasoners;

import no.uio.psy.rdfuzz.SUT;
import openllet.owlapi.OpenlletReasonerFactory;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.*;

import java.util.List;


// factory class to produce callers for different reasoners
public class ReasonerCallerFactory {

    private ReasonerCaller getHermiTCaller(OWLOntology ont) {
        ReasonerFactory rf = new ReasonerFactory();
        return new ReasonerCaller(rf.createReasoner(ont), SUT.HERMIT);
    }

    private ReasonerCaller getOpenlletCaller(OWLOntology ont) {
        OpenlletReasonerFactory rf = OpenlletReasonerFactory.getInstance();
        return new ReasonerCaller(rf.createReasoner(ont), SUT.OPENLLET);
    }

    private ReasonerCaller getElkCaller(OWLOntology ont) {
        ElkReasonerFactory rf = new ElkReasonerFactory();
        // disable the kinds of inference that ELK can not fully handle
        List<InferredAxiomGenerator<? extends OWLAxiom>> gens = List.of(
                //new InferredPropertyAssertionGenerator(),
                new InferredClassAssertionAxiomGenerator(),
                new InferredSubClassAxiomGenerator(),
                //new InferredDisjointClassesAxiomGenerator(),
                new InferredEquivalentClassAxiomGenerator(),
                //new InferredEquivalentDataPropertiesAxiomGenerator(),
                new InferredEquivalentObjectPropertyAxiomGenerator(),
                //new InferredInverseObjectPropertiesAxiomGenerator(),
                new InferredObjectPropertyCharacteristicAxiomGenerator(),
                //new InferredSubDataPropertyAxiomGenerator(),
                //new InferredDataPropertyCharacteristicAxiomGenerator(),
                new InferredObjectPropertyCharacteristicAxiomGenerator(),
                new InferredSubObjectPropertyAxiomGenerator()
        );
        return new ReasonerCaller(rf.createReasoner(ont), SUT.ELK, gens);
    }

    // get caller according to SUT one wants to test
    public ReasonerCaller getCaller(OWLOntology ont, SUT reasoner) {
        return switch (reasoner) {
            case HERMIT -> getHermiTCaller(ont);
            case OPENLLET -> getOpenlletCaller(ont);
            case ELK -> getElkCaller(ont);
        };
    }

}
