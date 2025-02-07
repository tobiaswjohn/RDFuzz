package no.uio.psy.rdfuzz;


import no.uio.psy.rdfuzz.anomalies.Anomaly;
import org.apache.jena.ontology.Ontology;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// removes from an ontology all axioms that are not necessary while keeping the same anomalies
public class TestCaseReducer {
    private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

    private Set<REASONING_TASKS> reasoningTasks = new HashSet<>();

    public OWLOntology reduceOnt(OWLOntology ont,
                                 Set<Anomaly> anomalies,
                                 Set<REASONING_TASKS> reasoningTasks) {

        this.reasoningTasks = reasoningTasks;
        // compute justification, i.e. minimal set of axioms
        try {

            Set<OWLAxiom> axioms = singleJustification(
                    new HashSet<>(),
                    ont.axioms().collect(Collectors.toSet()),
                    anomalies
            );


           // System.out.println("check result: " + allAnomaliesEntailed(manager.createOntology(axioms), anomalies));

            // create ontology from the minimal set of axioms
            return manager.createOntology(axioms.stream());

        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
    }

    // algorithm inspired by Horridge, Parsia, Sattler: "Explaining Inconsistencies in OWL Ontologies", 2009
    private Set<OWLAxiom> singleJustification(Set<OWLAxiom> axiomsFixed,
                                           Set<OWLAxiom> axioms,
                                           Set<Anomaly> anomalies) throws OWLOntologyCreationException {
        if (axioms.size() == 1)
            return axioms;

        Tuple<Set<OWLAxiom>, Set<OWLAxiom>> split = split(axioms);

        // compute combined sets by adding fixed part to both
        Set<OWLAxiom> xPlusFixed = new HashSet<>(split.x);
        xPlusFixed.addAll(axiomsFixed);
        OWLOntology ontologyX = manager.createOntology(xPlusFixed);
        if (allAnomaliesEntailed(ontologyX, anomalies))
            return singleJustification(axiomsFixed, split.x, anomalies);

        Set<OWLAxiom> yPlusFixed = new HashSet<>(split.y);
        yPlusFixed.addAll(axiomsFixed);
        OWLOntology ontologyY = manager.createOntology(yPlusFixed);
        if(allAnomaliesEntailed(ontologyY, anomalies))
            return singleJustification(axiomsFixed, split.y, anomalies);

        // we need a combination of axioms from both sets
        Set<OWLAxiom> minimalX = singleJustification(yPlusFixed, split.x, anomalies);
        Set<OWLAxiom> minimalXPlusFixed = new HashSet<>(minimalX);
        minimalXPlusFixed.addAll(axiomsFixed);
        Set<OWLAxiom> minimalY = singleJustification(minimalXPlusFixed, split.y, anomalies);
        // combine both sets
        minimalX.addAll(minimalY);

        return minimalX;
    }

    // splits set of axioms into two equally sized sets
    public Tuple<Set<OWLAxiom>, Set<OWLAxiom>> split(Set<OWLAxiom> axioms) {
        Set<OWLAxiom> first = new HashSet<>();
        Set<OWLAxiom> second = new HashSet<>();

        boolean intoFirst = true;
        for (OWLAxiom a : axioms) {
            if (intoFirst)
                first.add(a);
            else
                second.add(a);
            intoFirst = !intoFirst; // next axiom is added to other set
        }
        return new Tuple<>(first, second);
    }

    private boolean allAnomaliesEntailed(OWLOntology ont, Set<Anomaly> anomalies) {
        // see, if any anomaly is not contained (both directions

        // limit number of calls of "entailedAnomalies" as this is very expensive!
        Set<Anomaly> entailed = entailedAnomalies((ont));
        return anomalies.containsAll(entailed) && entailed.containsAll(anomalies);

    }
    // anomalies entailed by an ontology
    private Set<Anomaly> entailedAnomalies(OWLOntology ont) {
        ElReasonerTester tester = new ElReasonerTester(ont);

        // run the tests, specified by the configuration
        tester.runTests(reasoningTasks);
        return tester.getFoundAnomalies();
    }
}
