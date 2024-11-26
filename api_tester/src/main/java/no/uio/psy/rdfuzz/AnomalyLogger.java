package no.uio.psy.rdfuzz;

import no.uio.psy.rdfuzz.anomalies.Anomaly;
import no.uio.psy.rdfuzz.anomalies.AnomalySummary;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;


public class AnomalyLogger {

    String reportFile = "-";

    // log summarized anomaly information about the test case
    public void logSummary(List<Anomaly> foundAnomalies,
                           String pathToReportFile,
                           String delimiter) {

        AnomalySummary as = new AnomalySummary();

        // collect summaries from all anomalies
        for (Anomaly a : foundAnomalies) {
            as.add(a.getSummary());
        }

        String line = as.toCSVString(delimiter) + delimiter + reportFile;

        // append line to file
        try {
            Files.write(Paths.get(pathToReportFile), line.getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        //System.out.println(line);
    }

    public void logAnomalies(List<Anomaly> foundAnomalies,
                             File ontFile,
                             String folderToPrint) {

        // if no anomalies found --> do not do logging
        if (foundAnomalies.isEmpty())
            return ;


        PrintWriter writer = getPrintWriter(folderToPrint);


        writer.println("Found anomalies while testing file " + ontFile);

        int count = 1;
        for (Anomaly a : foundAnomalies) {
            writer.println();
            writer.print("Anomaly " + count + ": ");
            writer.println(a);
            count++;
        }

        writer.println();
        writer.println("\n––––––––––––––––––––––––––––––––––––––––––––");
        writer.println("–––––––––––    Ontology File    ––––––––––––");
        writer.println("––––––––––––––––––––––––––––––––––––––––––––\n");

        // add content of ontology file
        try (BufferedReader br = new BufferedReader(new FileReader(ontFile))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                //writer.println(i + "\t" + line);
                writer.println(line);
                ++i;
                // process the line.
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        writer.close();
    }

    // print writer for results
    private PrintWriter getPrintWriter(String folderToPrint) {
        int i = 0;
        String name = folderToPrint + "/" + reportName(i);
        while (new File(name).exists()) {
            i++;
            name = folderToPrint + "/" + reportName(i);
        }

        System.out.println("Write bug report to file " + name);

        reportFile = reportName(i);

        // write error message and .ttl file to file
        PrintWriter writer;
        try {
            writer = new PrintWriter(name, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return writer;
    }

    private String reportName(Integer i){
        return "anomaly" + i + ".txt";
    }

}
