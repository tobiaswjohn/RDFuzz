package no.uio.psy.rdfuzz;

import no.uio.psy.rdfuzz.anomalies.Anomaly;
import org.semanticweb.owlapi.model.*;


import java.io.*;
import java.util.*;

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
           /* System.out.println("detailed inspection");
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
            }*/

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

