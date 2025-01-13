package no.uio.psy.rdfuzz;

import no.uio.psy.rdfuzz.anomalies.*;
import no.uio.psy.rdfuzz.reasoners.EmptyReasonerCaller;
import no.uio.psy.rdfuzz.reasoners.ReasonerCaller;
import no.uio.psy.rdfuzz.reasoners.ReasonerCallerFactory;
import no.uio.psy.rdfuzz.reasoners.ReasonerInteractor;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// tests the EL reasoners
// creates a list of anomalies found for the provided ontology
public class ElReasonerTester {
    private final ReasonerInteractor hermit;
    private final ReasonerInteractor openllet;
    private final ReasonerInteractor elk;

    private final OWLOntology ont;

    private final Set<Anomaly> foundAnomalies = new HashSet<>();

    ElReasonerTester(OWLOntology ont) {
        this.ont = ont;

        ReasonerCallerFactory callerFactory = new ReasonerCallerFactory();

        ReasonerInteractor hermit1;
        try {
            hermit1 = callerFactory.getCaller(ont, SUT.HERMIT);
        }
        catch (Exception e) {
            hermit1 = new EmptyReasonerCaller(SUT.HERMIT);
            foundAnomalies.add(new ExceptionAnomaly(e, SUT.HERMIT));
        }
        hermit = hermit1;

        ReasonerInteractor openllet1;
        try {
            openllet1 = callerFactory.getCaller(ont, SUT.OPENLLET);
        }
        catch (Exception e) {
            openllet1 = new EmptyReasonerCaller(SUT.OPENLLET);
            foundAnomalies.add(new ExceptionAnomaly(e, SUT.OPENLLET));
        }
        openllet = openllet1;

        ReasonerInteractor elk1;
        try {
            elk1 = callerFactory.getCaller(ont, SUT.ELK);
        } catch (Exception e) {
            elk1 = new EmptyReasonerCaller(SUT.ELK);
            foundAnomalies.add(new ExceptionAnomaly(e, SUT.ELK));
        }
        elk = elk1;
    }

    public Set<Anomaly> getFoundAnomalies() {
        return foundAnomalies;
    }

    // runs all kinds of test with the ontology
    public void runTests() {
        testConsistency();
        //testInferredAxioms();
    }

    // reudces the ontology while keeping all anomalies
    public void minimalWitness() {
        OWLOntology reduced = minimalOnt();
        System.out.println("reduced ontology (" + reduced.axioms().count() + " axioms):");
        for (OWLAxiom a : reduced.axioms().toList())
            System.out.println(a);
    }

    private OWLOntology minimalOnt() {
        TestCaseReducer ontReducer = new TestCaseReducer();
        return ontReducer.reduceOnt(ont, foundAnomalies);
    }


    private void testConsistency() {
            System.out.println("run consistency tests");

            ResultWithAnomalie<Boolean> hermitConsistent = hermit.isConsistent();
            ResultWithAnomalie<Boolean> openlletConsistent = openllet.isConsistent();
            ResultWithAnomalie<Boolean> elkConsistent = elk.isConsistent();

            //System.out.println("ELK consistent: " + elk.isConsistent().result);

            // add any found anomalies
            foundAnomalies.addAll(hermitConsistent.foundAnomalies);
            foundAnomalies.addAll(openlletConsistent.foundAnomalies);
            foundAnomalies.addAll(elkConsistent.foundAnomalies);

            // check for deviating results and add anomalies if necessary
            foundAnomalies.addAll(compareConsistencyChecks(hermitConsistent, openlletConsistent));
            foundAnomalies.addAll(compareConsistencyChecks(hermitConsistent, elkConsistent));
            foundAnomalies.addAll(compareConsistencyChecks(openlletConsistent, elkConsistent));

    }

    private void testInferredAxioms() {
            ResultWithAnomalie<Set<OWLAxiom>> hermitInfers = hermit.inferredAxioms();
            ResultWithAnomalie<Set<OWLAxiom>> openlletInfers = openllet.inferredAxioms();
            ResultWithAnomalie<Set<OWLAxiom>> elkInfers = elk.inferredAxioms();

            // add any found anomalies
            foundAnomalies.addAll(hermitInfers.foundAnomalies);
            foundAnomalies.addAll(openlletInfers.foundAnomalies);
            foundAnomalies.addAll(elkInfers.foundAnomalies);

            // check for conflicting results and add anomalies if necessary
            foundAnomalies.addAll(compareInferredAxioms(hermitInfers, openlletInfers));
            foundAnomalies.addAll(compareInferredAxioms(hermitInfers, elkInfers));
            foundAnomalies.addAll(compareInferredAxioms(elkInfers, openlletInfers));

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

    private Set<Anomaly> compareInferredAxioms(ResultWithAnomalie<Set<OWLAxiom>> infers1,
                                               ResultWithAnomalie<Set<OWLAxiom>> infers2) {

        // check, if both systems found a result (i.e. no anomaly in computation)
        if (!infers1.anyAnomaly && !infers2.anyAnomaly) {
            boolean isAnomaly = false; // tracks, if this comparison is an anomaly

            // compute the differences between the two results
            Set<OWLAxiom> additional1 = new HashSet<OWLAxiom>(infers1.result);
            additional1.removeAll(infers2.result);

            Set<OWLAxiom> additional2 = new HashSet<OWLAxiom>(infers2.result);
            additional2.removeAll(infers1.result);

            // if one of the reasoners is ELK, only check for subset relation of result
            if (infers1.sut != SUT.ELK && infers2.sut != SUT.ELK) {
                if (!additional2.isEmpty() || !additional1.isEmpty())
                    isAnomaly = true;
            }

            else if (infers1.sut == SUT.ELK && !additional1.isEmpty() ||
                    infers2.sut == SUT.ELK && !additional2.isEmpty())
                isAnomaly = true;

            if (isAnomaly)
                return Set.of(new InferenceAnomaly(
                        infers1.sut,
                        infers2.sut,
                        additional1,
                        additional2
                ));
        }

        return Set.of();
    }



}
