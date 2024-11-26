package no.uio.psy.rdfuzz;

import no.uio.psy.rdfuzz.anomalies.Anomaly;
import no.uio.psy.rdfuzz.anomalies.ExceptionAnomaly;
import no.uio.psy.rdfuzz.anomalies.NotElAnomaly;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;


import java.io.*;
import java.util.*;

import static java.lang.System.exit;

public class Main {
    static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    static OWLAxiom inconsistent = manager.getOWLDataFactory().getOWLSubClassOfAxiom(
            manager.getOWLDataFactory().getOWLThing(),
            manager.getOWLDataFactory().getOWLNothing()
    );


    public static void main(String[] args) throws OWLOntologyCreationException {
        String fileName=args[0];
        String pathToReportAnomalies = args[1];
        String pathToReportCSV = args[2];

        File ontFile = new File(fileName);

        // list to save all found anomalies
        List<Anomaly> foundAnomalies = new ArrayList<>();

        if (List.of(args).contains("--test-parsers")) {
            foundAnomalies.addAll(testParsers(ontFile));
        }

        if (List.of(args).contains("--test-reasoners")) {
            System.out.println("test reasoners");
             foundAnomalies.addAll(testReasoners(ontFile));
        }

        if (List.of(args).contains("--no-export")) {
            // run to examine bugs in detail
            System.out.println("detailed inspection");


            List<Anomaly> anomalies = testReasoners(ontFile);

            for (Anomaly a : anomalies)
                System.out.println(a);

        }
        else {
            // export found anomalies
            AnomalyLogger anomalyLogger = new AnomalyLogger();
            anomalyLogger.logAnomalies(
                    foundAnomalies,
                    ontFile,
                    pathToReportAnomalies);

            anomalyLogger.logSummary(foundAnomalies, pathToReportCSV, ",");
        }

        // indicate, that some anomaly was found
        if (!foundAnomalies.isEmpty())
            exit(1);
    }

    public static List<Anomaly> testReasoners(File ontFile) {
        // load ontology
        OntologyLoader ontL = new OntologyLoader(manager);
        OWLOntology ont;
        try {
            ont = ontL.loadOntologyFile(ontFile);
        }
        catch (OWLOntologyCreationException e) {
            return List.of(new ExceptionAnomaly(e, SUT.OWLAPI));
        }

        // check if ontology is in EL --> only use for those for testing
        if (!isEL(ont)) {
            List<OWLProfileViolation> violations = getElViolations(ont);
            return List.of(new NotElAnomaly(violations));
        }
        else {
            ElReasonerTester tester = new ElReasonerTester(ont);

            tester.runTests();
            return tester.getFoundAnomalies().stream().sorted().toList();
        }
    }

    public static List<Anomaly> testParsers(File ontFile) {
        ParserTester parserTester = new ParserTester();
        parserTester.testParsers(ontFile);
        return parserTester.getFoundAnomalies().stream().sorted().toList();
    }


    // checks if ontology is in profile
    public static boolean isEL(OWLOntology ont) {
        OWLProfileReport profileReport = new OWL2ELProfile().checkOntology(ont);
        return profileReport.isInProfile();
    }

    // get EL violations
    public static List<OWLProfileViolation> getElViolations(OWLOntology ont) {
        OWLProfileReport profileReport = new OWL2ELProfile().checkOntology(ont);
        return profileReport.getViolations();
    }
}

