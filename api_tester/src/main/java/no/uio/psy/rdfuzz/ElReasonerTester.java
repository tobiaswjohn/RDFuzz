package no.uio.psy.rdfuzz;

import no.uio.psy.rdfuzz.anomalies.*;
import no.uio.psy.rdfuzz.reasoners.EmptyReasonerCaller;
import no.uio.psy.rdfuzz.reasoners.ReasonerCallerFactory;
import no.uio.psy.rdfuzz.reasoners.ReasonerInteractor;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.HashSet;
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
    public void runAllTests() {
        testConsistency();
        testInferredAxioms();
    }

    // runs the tests specified by the parameter
    public void runTests(Set<REASONING_TASKS> reasoningTasks) {
        if (reasoningTasks.contains(REASONING_TASKS.CONSISTENCY))
            testConsistency();

        if (reasoningTasks.contains(REASONING_TASKS.CLASS_HIERARCHY))
            testClassHierarchy();

        if (reasoningTasks.contains(REASONING_TASKS.INFERRED_AXIOMS))
            testInferredAxioms();
    }

    public void testConsistency() {
        System.out.println("run consistency tests");

        ResultWithAnomaly<Boolean> hermitConsistent = hermit.isConsistent();
        ResultWithAnomaly<Boolean> openlletConsistent = openllet.isConsistent();
        ResultWithAnomaly<Boolean> elkConsistent = elk.isConsistent();

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

    public void testClassHierarchy() {
        System.out.println("compute class hierarchies");

        ResultWithAnomaly<Set<OWLAxiom>> hermitHierarchy = hermit.classHierarchy();
        ResultWithAnomaly<Set<OWLAxiom>> openlletHierarchy = openllet.classHierarchy();
        ResultWithAnomaly<Set<OWLAxiom>> elkHierarchy = elk.classHierarchy();

        // add any found anomalies
        foundAnomalies.addAll(hermitHierarchy.foundAnomalies);
        foundAnomalies.addAll(openlletHierarchy.foundAnomalies);
        foundAnomalies.addAll(elkHierarchy.foundAnomalies);

        boolean isClassHierarchy = true;
        // check for conflicting results and add anomalies if necessary
        foundAnomalies.addAll(compareInferredAxioms(hermitHierarchy, openlletHierarchy, isClassHierarchy));
        foundAnomalies.addAll(compareInferredAxioms(hermitHierarchy, elkHierarchy, isClassHierarchy));
        foundAnomalies.addAll(compareInferredAxioms(elkHierarchy, openlletHierarchy, isClassHierarchy));
    }

    public void testInferredAxioms() {
        ResultWithAnomaly<Set<OWLAxiom>> hermitInfers = hermit.inferredAxioms();
        ResultWithAnomaly<Set<OWLAxiom>> openlletInfers = openllet.inferredAxioms();
        ResultWithAnomaly<Set<OWLAxiom>> elkInfers = elk.inferredAxioms();

        // add any found anomalies
        foundAnomalies.addAll(hermitInfers.foundAnomalies);
        foundAnomalies.addAll(openlletInfers.foundAnomalies);
        foundAnomalies.addAll(elkInfers.foundAnomalies);

        boolean isClassHierarchy = false;
        // check for conflicting results and add anomalies if necessary
        foundAnomalies.addAll(compareInferredAxioms(hermitInfers, openlletInfers, isClassHierarchy));
        foundAnomalies.addAll(compareInferredAxioms(hermitInfers, elkInfers, isClassHierarchy));
        foundAnomalies.addAll(compareInferredAxioms(elkInfers, openlletInfers, isClassHierarchy));

    }

    // checks, if the two systems found a result (i.e. no anomaly in computation) and if the results are the same
    private Set<Anomaly> compareConsistencyChecks(ResultWithAnomaly<Boolean> consistency1,
                                                  ResultWithAnomaly<Boolean> consistency2) {
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

    // compares the sets of axioms and adds an anomaly if they differ
    // third argument indicates, whether the axioms form a class hierarchy --> different (more specific) type of anomaly
    private Set<Anomaly> compareInferredAxioms(ResultWithAnomaly<Set<OWLAxiom>> infers1,
                                               ResultWithAnomaly<Set<OWLAxiom>> infers2,
                                               Boolean isClassHierarchy) {

        // check, if both systems found a result (i.e. no anomaly in computation)
        if (!infers1.anyAnomaly && !infers2.anyAnomaly) {
            boolean isAnomaly = false; // tracks, if this comparison is an anomaly

            // compute the differences between the two results
            Set<OWLAxiom> additional1 = new HashSet<>(infers1.result);
            additional1.removeAll(infers2.result);

            Set<OWLAxiom> additional2 = new HashSet<>(infers2.result);
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
                if (isClassHierarchy)
                    return Set.of(new ClassHierarchyAnomaly(
                            infers1.sut,
                            infers2.sut,
                            additional1,
                            additional2
                    ));
                else return Set.of(new InferenceAnomaly(
                            infers1.sut,
                            infers2.sut,
                            additional1,
                            additional2
                    ));
        }

        return Set.of();
    }



}
