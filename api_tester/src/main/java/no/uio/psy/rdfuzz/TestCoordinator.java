package no.uio.psy.rdfuzz;

import no.uio.psy.rdfuzz.anomalies.Anomaly;
import no.uio.psy.rdfuzz.anomalies.ExceptionAnomaly;
import no.uio.psy.rdfuzz.anomalies.NotElAnomaly;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;

import java.io.File;
import java.util.List;

// runs the different tests, e.g. tests of parsers, reasoners
public class TestCoordinator {
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    OWLAxiom inconsistent = manager.getOWLDataFactory().getOWLSubClassOfAxiom(
            manager.getOWLDataFactory().getOWLThing(),
            manager.getOWLDataFactory().getOWLNothing()
    );

    public List<Anomaly> testReasoners(File ontFile) {
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
        //if (!tester.getFoundAnomalies().isEmpty()) {
        //   tester.minimalWitness();
        //}
        return tester.getFoundAnomalies().stream().sorted().toList();

    }



    public List<Anomaly> testParsers(File ontFile) {
        ParserTester parserTester = new ParserTester();
        parserTester.testParsers(ontFile);
        return parserTester.getFoundAnomalies().stream().sorted().toList();
    }

    // checks, if the violations result from bugs
    // CAVE: assumes that ontologies are created using the EL profile grammar!
    public boolean allAreBugs(List<OWLProfileViolation> violations) {
        for (OWLProfileViolation v : violations) {
            if (!isProfilerBug(v))
                return false;
        }
        return true;
    }

    public boolean isProfilerBug(OWLProfileViolation violation) {
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


    // checks if ontology is in profile
    public boolean isEL(OWLOntology ont) {
        OWLProfileReport profileReport = new OWL2ELProfile().checkOntology(ont);
        return profileReport.isInProfile();
    }

    // checks if ontology is in profile
    public boolean isDL(OWLOntology ont) {
        OWLProfileReport profileReport = new OWL2DLProfile().checkOntology(ont);
        return profileReport.isInProfile();
    }

    // get EL violations
    public List<OWLProfileViolation> getElViolations(OWLOntology ont) {
        OWLProfileReport profileReport = new OWL2ELProfile().checkOntology(ont);
        return profileReport.getViolations();
    }

    // get DL violations
    public List<OWLProfileViolation> getDlViolations(OWLOntology ont) {
        OWLProfileReport profileReport = new OWL2DLProfile().checkOntology(ont);
        return profileReport.getViolations();
    }
}
