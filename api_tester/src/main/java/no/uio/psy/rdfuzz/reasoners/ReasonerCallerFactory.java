package no.uio.psy.rdfuzz.reasoners;

import no.uio.psy.rdfuzz.SUT;
import openllet.owlapi.OpenlletReasonerFactory;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.model.OWLOntology;



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
        return new ReasonerCaller(rf.createReasoner(ont), SUT.ELK);
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
