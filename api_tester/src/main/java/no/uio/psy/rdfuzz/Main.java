package no.uio.psy.rdfuzz;

import no.uio.psy.rdfuzz.anomalies.Anomaly;
import org.semanticweb.owlapi.model.*;


import java.io.*;
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


import static java.lang.System.exit;




public class Main {

    public static void main(String[] args) {
        String fileName=args[0];
        String pathToReportAnomalies = args[1];
        String pathToReportCSV = args[2];

        File ontFile = new File(fileName);
        TestCoordinator testCoordinator = new TestCoordinator();

        // the reasoning tasks that are used
        Set<REASONING_TASKS> reasoningTasks = new HashSet<>();
        reasoningTasks.add(REASONING_TASKS.CONSISTENCY);
        reasoningTasks.add(REASONING_TASKS.INFERRED_AXIOMS);

        // list to save all found anomalies
        List<Anomaly> foundAnomalies = new ArrayList<>();


        // set logging level to warning (important for ELK)
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.WARNING);
        for (Handler h : rootLogger.getHandlers()) {
            h.setLevel(Level.WARNING);
        }


        if (List.of(args).contains("--test-parsers")) {
            foundAnomalies.addAll(testCoordinator.testParsers(ontFile));
        }

        if (List.of(args).contains("--test-reasoners")) {
            System.out.println("test reasoners");
            foundAnomalies.addAll(testCoordinator.testReasoners(ontFile, reasoningTasks));
        }

        if (List.of(args).contains("--minimize-ontology")) {
            System.out.println("minimize test ontology");

            OWLOntology minimalOnt = testCoordinator.minimalWitness(
                    new HashSet<>(foundAnomalies),
                    ontFile,
                    reasoningTasks
            );

            System.out.println("reduced ontology (" + minimalOnt.axioms().count() + " axioms):");
            for (OWLAxiom a : minimalOnt.axioms().toList())
                System.out.println(a);
        }

        if (List.of(args).contains("--no-export")) {
            // print anomalies instead of exporting them
            for (Anomaly a : foundAnomalies)
                System.out.println(a);

            if (foundAnomalies.isEmpty())
                System.out.println("No anomalies detected.");
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

}

