package no.uio.psy.rdfuzz;

import no.uio.psy.rdfuzz.anomalies.Anomaly;
import no.uio.psy.rdfuzz.anomalies.ExceptionAnomaly;
import no.uio.psy.rdfuzz.anomalies.NotElAnomaly;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;


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

        // list to save all found anomalies
        List<Anomaly> foundAnomalies = new ArrayList<>();

        if (List.of(args).contains("--test-parsers")) {
            foundAnomalies.addAll(testCoordinator.testParsers(ontFile));
        }

        if (List.of(args).contains("--test-reasoners")) {
            System.out.println("test reasoners");
            foundAnomalies.addAll(testCoordinator.testReasoners(ontFile));
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

