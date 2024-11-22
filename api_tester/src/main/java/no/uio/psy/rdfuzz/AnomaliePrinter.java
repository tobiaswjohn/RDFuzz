package no.uio.psy.rdfuzz;

import no.uio.psy.rdfuzz.anomalies.Anomaly;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;


public class AnomaliePrinter {
    // print writer for results
    private PrintWriter getPrintWriter(String folderToPrint) {
        int i = 0;
        String name = folderToPrint + "/anomaly" + i +".txt";
        while (new File(name).exists()) {
            i++;
            name = folderToPrint + "/anomaly" + i + ".txt";
        }

        System.out.println("Write bug report to file " + name);
        // write error message and .ttl file to file
        PrintWriter writer;
        try {
            writer = new PrintWriter(name, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return writer;
    }


    public void logAnomalies(List<Anomaly> foundAnomalies,
                             File ontFile,
                             String folderToPrint) {
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
}
