package no.uio.psy.rdfuzz;

import no.uio.psy.rdfuzz.anomalies.Anomaly;
import no.uio.psy.rdfuzz.anomalies.ExceptionAnomaly;
import no.uio.psy.rdfuzz.anomalies.NotElAnomaly;
import openllet.owlapi.OpenlletReasonerFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.util.*;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.*;

import static java.lang.System.exit;

public class Main {
    static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    static OWLAxiom inconsistent = manager.getOWLDataFactory().getOWLSubClassOfAxiom(
            manager.getOWLDataFactory().getOWLThing(),
            manager.getOWLDataFactory().getOWLNothing()
    );


    public static void main(String[] args) throws OWLOntologyCreationException {
        String fileName=args[0];
        File ontFile = new File(fileName);
        OntologyLoader ontL = new OntologyLoader(manager);

        // todo: get this from args
        String pathToReportAnomalies = "/home/tobias/Documents/programming/ontologies/RDFuzz/temp";

        if (List.of(args).contains("--test-reasoners")) {
            System.out.println("test reasoners");
            List<Anomaly> foundAnomalies = testReasoners(ontFile);

            AnomaliePrinter anomPrint = new AnomaliePrinter();
            anomPrint.logAnomalies(foundAnomalies,
                    ontFile,
                    pathToReportAnomalies);
        }

        if (List.of(args).contains("--test-parsers")) {
            System.out.println("test OWL API");
            runOwlApi(ontFile);
            System.out.println("test Jena API");
            runJenaApi(ontFile);
        }

        if (List.of(args).contains("--no-export")) {
            // run to examine bugs in detail

            List<Anomaly> foundAnomalies = testReasoners(ontFile);

            AnomaliePrinter anomPrint = new AnomaliePrinter();
            anomPrint.logAnomalies(foundAnomalies,
                    ontFile,
                    pathToReportAnomalies);

            for (Anomaly a : foundAnomalies)
                System.out.println(a);

        }

    }

    public static List<Anomaly> testReasoners(File ontFile) {
        // load ontology
        OntologyLoader ontL = new OntologyLoader(manager);
        OWLOntology ont;
        try {
            ont = ontL.loadOntologyFile(ontFile);
        }
        catch (OWLOntologyCreationException e) {
            return List.of(new ExceptionAnomaly(e));
        }

        // check if ontology is in EL --> only use for those for testing
        if (!isEL(ont))
            return List.of(new NotElAnomaly());
        else {
            ElReasonerTester tester = new ElReasonerTester(ont);

            tester.runTests();
            return tester.getFoundAnomalies().stream().sorted().toList();
        }

    }

    // tries to load ontology using owl api
    public static void runOwlApi(File ontFile) {        
        try {
            OntologyLoader ontL = new OntologyLoader(manager);
            ontL.loadOntologyFile(ontFile);
        } catch (OWLOntologyCreationException e) {
            // if error occurred: save log file
            //logException(e, ontFile, "Owl API");
            //TODO: log exception
            exit(1);
        }
    }

    private static void runJenaApi(File ontFile) {
        try {
            Model jenaModel = RDFDataMgr.loadDataset(ontFile.getAbsolutePath()).getDefaultModel();
        } catch  (Exception e) {
            //throw  e;
            //System.out.println(e);
            // if error occurred: save log file
            //logException(e, ontFile, "Jena API");
            // TODO: log exception
            exit(1);
        }
    }

    // checks if ontology is in profile
    public static boolean isEL(OWLOntology ont) {
        OWLProfileReport profileReport = new OWL2ELProfile().checkOntology(ont);
        return profileReport.isInProfile();
    }
}

