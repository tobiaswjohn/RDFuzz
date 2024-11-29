package no.uio.psy.rdfuzz.reasoners;

import no.uio.psy.rdfuzz.SUT;
import no.uio.psy.rdfuzz.anomalies.NotCreatedAnomaly;
import no.uio.psy.rdfuzz.anomalies.ResultWithAnomalie;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.Set;

// a reasoner caller for an empty reasoner, i.e. one that could not be created
public class EmptyReasonerCaller implements  ReasonerInteractor{

    private final SUT sut;

    public EmptyReasonerCaller(SUT sut) {
        this.sut = sut;
    }
    @Override
    public ResultWithAnomalie<Boolean> isConsistent() {
        return new ResultWithAnomalie<Boolean>(
                false,
                Set.of(new NotCreatedAnomaly(sut)),
                sut
            );
    }

    @Override
    public ResultWithAnomalie<Set<OWLAxiom>> inferredAxioms() {
        return new ResultWithAnomalie<>(
                Set.of(),
                Set.of(new NotCreatedAnomaly(sut)),
                sut
        );
    }
}
