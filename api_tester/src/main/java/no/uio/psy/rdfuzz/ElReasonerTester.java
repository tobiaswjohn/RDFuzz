package no.uio.psy.rdfuzz;

import no.uio.psy.rdfuzz.anomalies.Anomaly;
import no.uio.psy.rdfuzz.anomalies.ConsistencyAnomaly;
import no.uio.psy.rdfuzz.anomalies.ResultWithAnomalie;
import no.uio.psy.rdfuzz.reasoners.ReasonerCaller;
import no.uio.psy.rdfuzz.reasoners.ReasonerCallerFactory;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.HashSet;
import java.util.Set;

// tests the EL reasoners
// creates a list of anomalies found for the provided ontology
public class ElReasonerTester {
    private final ReasonerCaller hermit;
    private final ReasonerCaller openllet;
    private final ReasonerCaller elk;


    private Set<Anomaly> foundAnomalies = new HashSet<>();

    ElReasonerTester(OWLOntology ont) {
        ReasonerCallerFactory callerFactory = new ReasonerCallerFactory();
        hermit = callerFactory.getCaller(ont, SUT.HERMIT);
        openllet = callerFactory.getCaller(ont, SUT.OPENLLET);
        elk = callerFactory.getCaller(ont, SUT.ELK);

    }

    public Set<Anomaly> getFoundAnomalies() {
        return foundAnomalies;
    }

    // runs all kinds of test with the ontology
    public void runTests() {
        testConsistency();

    }

    private void testConsistency() {
        ResultWithAnomalie<Boolean> hermitConsistent = hermit.isConsistent();
        ResultWithAnomalie<Boolean> openlletConsistent = openllet.isConsistent();
        ResultWithAnomalie<Boolean> elkConsistent = elk.isConsistent();

        // add any found anomalies
        foundAnomalies.addAll(hermitConsistent.foundAnomalies);
        foundAnomalies.addAll(openlletConsistent.foundAnomalies);
        foundAnomalies.addAll(elkConsistent.foundAnomalies);

        // check for deviating results and add anomalies if necessary
        foundAnomalies.addAll(compareConsistencyChecks(hermitConsistent, openlletConsistent));
        foundAnomalies.addAll(compareConsistencyChecks(hermitConsistent, elkConsistent));
        foundAnomalies.addAll(compareConsistencyChecks(openlletConsistent, elkConsistent));

        System.out.println("run consistency tests");
    }

    private void testInferredAxioms() {

    }


    // checks, if the two systems found a result (i.e. no anomaly in computation) and if the results are the same
    private Set<Anomaly> compareConsistencyChecks(ResultWithAnomalie<Boolean> consistency1,
                                                  ResultWithAnomalie<Boolean> consistency2) {
        if (!consistency1.anyAnomaly && !consistency2.anyAnomaly)
            if (consistency1.result != consistency2.result)
                return Set.of(new ConsistencyAnomaly(
                        consistency1.sut,
                        consistency2.sut,
                        consistency1.result,
                        consistency2.result
                ));

        return Set.of();
    }
}
