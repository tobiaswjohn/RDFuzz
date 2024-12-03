package no.uio.psy.rdfuzz;

import no.uio.psy.rdfuzz.anomalies.Anomaly;
import no.uio.psy.rdfuzz.anomalies.ExceptionAnomaly;
import no.uio.psy.rdfuzz.anomalies.NotElAnomaly;
import openllet.owlapi.OpenlletReasonerFactory;
import org.apache.jena.base.Sys;
import org.apache.jena.ontology.impl.OWLDLProfile;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.*;


import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

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
            // print anomalies instead of exporting them
            System.out.println("detailed inspection");
            OntologyLoader ontL = new OntologyLoader(manager);
            OWLOntology ont = null;
            try {
                ont = ontL.loadOntologyFile(ontFile);
            }
            catch (Exception e) {
                System.out.println("could not create ontology");
            }
            System.out.println("run temp test");
            temp(ont);
            System.out.println("end temp test");
            if (!isEL(ont)) {
                System.out.println("not in EL!");
                if (!isDL(ont))
                    System.out.println("not in DL!");
                else
                    System.out.println("in DL!");
                foundAnomalies.add(new NotElAnomaly(getDlViolations(ont)));
            }

            for (Anomaly a : foundAnomalies)
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
            System.out.println("profiler indicates not EL. Check for known bugs");
            List<OWLProfileViolation> violations = getElViolations(ont);
            if (!allAreBugs(violations))
                return List.of(new NotElAnomaly(violations));
        }

        System.out.println("is in EL");
        ElReasonerTester tester = new ElReasonerTester(ont);

        tester.runTests();
        return tester.getFoundAnomalies().stream().sorted().toList();

    }

    public static List<Anomaly> testParsers(File ontFile) {
        ParserTester parserTester = new ParserTester();
        parserTester.testParsers(ontFile);
        return parserTester.getFoundAnomalies().stream().sorted().toList();
    }

    // checks, if the violations result from bugs
    // CAVE: assumes that ontologies are created using the EL profile grammar!
    public static boolean allAreBugs(List<OWLProfileViolation> violations) {
        for (OWLProfileViolation v : violations) {
            if (!isProfilerBug(v))
                return false;
        }
        return true;
    }

    public static boolean isProfilerBug(OWLProfileViolation violation) {
        String violationString = violation.toString();
        System.out.println("violation: " + violationString);
        List<String> knownProfilerBugPatterns = List.of(
                "Use of data range not in profile: http://www.w3.org/1999/02/22-rdf-syntax-ns#langString .*",
                "Use of data range not in profile: <.*> \\[DatatypeDefinition.*",
                "Not enough .*; at least two needed.*"
        );

        // check, if any of the known bugs matches
        for (String bugPattern : knownProfilerBugPatterns ) {
            if (violationString.matches(bugPattern))
                return true;
        }
        return false;
    }

    public static void temp(OWLOntology ont) throws OWLOntologyCreationException {
        OWLReasoner openllet = OpenlletReasonerFactory.getInstance().createReasoner(ont);

        openllet.isConsistent();

    }

    // checks if ontology is in profile
    public static boolean isEL(OWLOntology ont) {
        OWLProfileReport profileReport = new OWL2ELProfile().checkOntology(ont);
        return profileReport.isInProfile();
    }

    // checks if ontology is in profile
    public static boolean isDL(OWLOntology ont) {
        OWLProfileReport profileReport = new OWL2DLProfile().checkOntology(ont);
        return profileReport.isInProfile();
    }

    // get EL violations
    public static List<OWLProfileViolation> getElViolations(OWLOntology ont) {
        OWLProfileReport profileReport = new OWL2ELProfile().checkOntology(ont);
        return profileReport.getViolations();
    }

    // get DL violations
    public static List<OWLProfileViolation> getDlViolations(OWLOntology ont) {
        OWLProfileReport profileReport = new OWL2DLProfile().checkOntology(ont);
        return profileReport.getViolations();
    }
}

