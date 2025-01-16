package no.uio.psy.rdfuzz.anomalies;

import no.uio.psy.rdfuzz.SUT;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.util.Set;

public class ResultWithAnomalyFactory {

    public ResultWithAnomaly<Set<OWLAxiom>> getEmptyAxiomSetWithException(SUT sut, Exception e) {
        return  new ResultWithAnomaly<>(
                Set.of(),
                Set.of(new ExceptionAnomaly(e, sut)),
                sut
        );
    }

    public ResultWithAnomaly<Set<OWLNamedIndividual>> getEmptyIndividualSetWithException(SUT sut, Exception e) {
        return  new ResultWithAnomaly<>(
                Set.of(),
                Set.of(new ExceptionAnomaly(e, sut)),
                sut
        );
    }

    public ResultWithAnomaly<Boolean> getFalseWithException(SUT sut, Exception e) {
        return  new ResultWithAnomaly<>(
                false,
                Set.of(new ExceptionAnomaly(e, sut)),
                sut
        );
    }

}
