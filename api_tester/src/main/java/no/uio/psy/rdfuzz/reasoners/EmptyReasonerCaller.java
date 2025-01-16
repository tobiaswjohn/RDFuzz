package no.uio.psy.rdfuzz.reasoners;

import no.uio.psy.rdfuzz.SUT;
import no.uio.psy.rdfuzz.anomalies.NotCreatedAnomaly;
import no.uio.psy.rdfuzz.anomalies.ResultWithAnomaly;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.Set;

// a reasoner caller for an empty reasoner, i.e. one that could not be created
public class EmptyReasonerCaller implements  ReasonerInteractor{

    private final SUT sut;

    public EmptyReasonerCaller(SUT sut) {
        this.sut = sut;
    }
    @Override
    public ResultWithAnomaly<Boolean> isConsistent() {
        return new ResultWithAnomaly<>(
                false,
                Set.of(new NotCreatedAnomaly(sut)),
                sut
            );
    }

    @Override
    public ResultWithAnomaly<Set<OWLAxiom>> inferredAxioms() {
        return new ResultWithAnomaly<>(
                Set.of(),
                Set.of(new NotCreatedAnomaly(sut)),
                sut
        );
    }

    @Override
    public ResultWithAnomaly<Set<OWLAxiom>> classHierarchy() {
        return new ResultWithAnomaly<>(
                Set.of(),
                Set.of(new NotCreatedAnomaly(sut)),
                sut
        );
    }
}
