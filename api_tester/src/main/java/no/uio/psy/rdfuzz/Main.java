package no.uio.psy.rdfuzz;

import no.uio.psy.rdfuzz.anomalies.Anomaly;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;


import java.io.*;
import java.util.*;


import static java.lang.System.exit;




public class Main {

    static String ontologyFileName ="";
    static String pathToReportAnomalies = "";
    static String pathToReportCSV = "";

    static boolean testParsers = false;
    static boolean testReasoners = false;
    static boolean misc = false; // to some miscellaneous stuff
    static boolean minimizeOntology = false;
    static boolean noExport = false;
    static boolean printAnomalies = false; // indicates, whether anomalies are printed to terminal

    // the tasks that are used to test reasoners
    static Set<REASONING_TASKS> reasoningTasks = Set.of(
        REASONING_TASKS.CONSISTENCY,
        //REASONING_TASKS.INFERRED_AXIOMS,
        REASONING_TASKS.CLASS_HIERARCHY
    );

    // list to save all found anomalies
    static List<Anomaly> foundAnomalies = new ArrayList<>();

    public static void parseArguments(String[] args) {
        ontologyFileName =args[0];
        pathToReportAnomalies = args[1];
        pathToReportCSV = args[2];

        List<String> listOfArguments = List.of(args);

        if (listOfArguments.contains("--test-parsers"))
            testParsers = true;

        if (listOfArguments.contains("--test-reasoners"))
            testReasoners = true;

        if (listOfArguments.contains("--misc"))
            misc = true;

        if (listOfArguments.contains("--minimize-ontology"))
            minimizeOntology = true;

        if (listOfArguments.contains("--no-export"))
            noExport = true;

        if (listOfArguments.contains("--print-anomalies"))
            printAnomalies = true;
    }

    private static void runTests() {
        File ontFile = new File(ontologyFileName);
        TestCoordinator testCoordinator = new TestCoordinator();

        if (testParsers) {
            foundAnomalies.addAll(testCoordinator.testParsers(ontFile));
        }

        if (testReasoners) {
            System.out.println("test reasoners");
            foundAnomalies.addAll(testCoordinator.testReasoners(ontFile, reasoningTasks));
        }

        if (misc) {
            // do some more stuff --> CAVE: this might part might change a lot!

            // load ontology with Hermit reasoner
            OWLOntologyDocumentSource source = new FileDocumentSource(ontFile, new FunctionalSyntaxDocumentFormat());
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology ont = null;
            try {
                ont = manager.loadOntologyFromOntologyDocument(source);
            } catch (OWLOntologyCreationException e) {
                throw new RuntimeException(e);
            }

            ReasonerFactory rf = new ReasonerFactory();
            OWLReasoner hermit = rf.createReasoner(ont);
        }

        if (minimizeOntology) {
            System.out.println("minimize test ontology");

            OWLOntology minimalOnt = testCoordinator.minimalWitness(
                    new HashSet<>(foundAnomalies),
                    ontFile,
                    reasoningTasks
            );
            System.out.println("reduced ontology (" + minimalOnt.axioms().count() + " axioms):");
            for (OWLAxiom a : minimalOnt.axioms().toList())
                System.out.println(a);
            System.out.println();
        }

        if (printAnomalies) {
            // print anomalies to terminal
            for (Anomaly a : foundAnomalies)
                System.out.println(a);

            if (foundAnomalies.isEmpty())
                System.out.println("No anomalies detected.");
        }

        if (!noExport) {
            // export found anomalies
            AnomalyLogger anomalyLogger = new AnomalyLogger();
            anomalyLogger.logAnomalies(
                    foundAnomalies,
                    ontFile,
                    pathToReportAnomalies);

            anomalyLogger.logSummary(foundAnomalies, pathToReportCSV, ",");
        }
    }

    public static void main(String[] args) {
        parseArguments(args);
        runTests();

        // indicate, that some anomaly was found
        if (!foundAnomalies.isEmpty())
            exit(1);
    }

}

