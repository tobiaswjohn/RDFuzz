package no.uio.psy.rdfuzz;

import no.uio.psy.rdfuzz.anomalies.Anomaly;
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


        if (args.length == 2 && Objects.equals(args[1], "--no-export")) {
            // run to examine bugs in detail

            //runJenaApi(ontFile);

            OWLOntology ont = ontL.loadOntologyFile(ontFile);

            List<Anomaly> foundAnomalies = testReasoners(ont);

            for (Anomaly a : foundAnomalies)
                System.out.println(a);


            /*
            System.out.println("test Openllet reasoner");
            Set<OWLAxiom> inferredOpenllet = runOpenlletReasoner(ont, ontFile);

            System.out.println("test Hermit reasoner");
            Set<OWLAxiom> inferredHermit = runHermitReasoner(ont, ontFile);

            System.out.println("test ELK reasoner");
            Set<OWLAxiom> inferredElk = runElkReasoner(ont, ontFile);

            System.out.println("Openllet="+ inferredOpenllet + "\nHermit=" + inferredHermit + "\nElk=" + inferredElk);
            assert inferredOpenllet != null;
            System.out.println("openllet =?= HermiT : " + equivalent(inferredOpenllet, inferredHermit));
            System.out.println("onenllet >?= Elk : " + subset(inferredElk,inferredOpenllet));


            Set<OWLAxiom> additionalHermit = new HashSet<>(inferredHermit);
            Set<OWLAxiom> additionalOpenllet = new HashSet<>(inferredOpenllet);
            additionalHermit.removeAll(inferredOpenllet);
            additionalOpenllet.removeAll(inferredHermit);

            Set<OWLAxiom> additionalElk = new HashSet<>(inferredElk);
            additionalElk.removeAll(inferredHermit);

            System.out.println(additionalHermit);
            System.out.println(additionalOpenllet);
            System.out.println(additionalElk);

            ReasonerFactory rf = new ReasonerFactory();
            System.out.println(getInstances(
                    rf.createReasoner(ont),
                    manager.getOWLDataFactory().getOWLClass("http://www.w3.org/2002/07/owl#Thing"))
            );

             */

        }
        else {
            // regular test run

            System.out.println("test OWL API");
            runOwlApi(ontFile);

            // load ontology to reasoner
            OWLOntology ont = ontL.loadOntologyFile(ontFile);


            /*
            System.out.println("test Hermit reasoner");
            Set<OWLAxiom> axiomsHermit = runHermitReasoner(ont, ontFile);
            System.out.println("test Openllet reasoner");
            Set<OWLAxiom> axiomsOpenllet = runOpenlletReasoner(ont, ontFile);
            System.out.println("test Elk reasoner");
            Set<OWLAxiom> axiomsElk = runElkReasoner(ont, ontFile);

            System.out.println("axiomsOpenllet="+ axiomsOpenllet + " axiomsHermit=" + axiomsHermit);

            assert axiomsOpenllet != null;
            assert axiomsHermit != null;
            assert axiomsElk != null;

            boolean HvsO = equivalent(axiomsHermit, axiomsOpenllet);

            // we can only check for subset realtionship for Elk as Elk does not support all types of reasoning tasks,
            // i.e. can only infer fewer axioms
            boolean HvsE = subset(axiomsElk, axiomsHermit);
            boolean OvsE = subset(axiomsElk, axiomsOpenllet);

            // trigger exception, if one of the reasoners pairwise disagree
            if (!HvsE || !HvsO || !OvsE) {
                StringBuilder message = new StringBuilder();

                if (!HvsO) {
                    Set<OWLAxiom> additionalHermit = new HashSet<>(axiomsHermit);
                    Set<OWLAxiom> additionalOpenllet = new HashSet<>(axiomsOpenllet);
                    additionalHermit.removeAll(axiomsOpenllet);
                    additionalOpenllet.removeAll(axiomsHermit);

                    message.append("Hermit and Openllet infer different number of axioms.");
                    if (!additionalHermit.isEmpty()) {
                        message.append("\nadditional Axioms from Hermit:\n");
                        for (OWLAxiom a : additionalHermit)
                            message.append(a.toString()).append("\n");
                    }
                    if (!additionalOpenllet.isEmpty()) {
                        message.append("\nadditional Axioms from Openllet:\n");
                        for (OWLAxiom a : additionalOpenllet)
                            message.append(a.toString()).append("\n");
                    }

                    message.append("\n––––––––––––––––––––––––––––––––––––––––––\n");
                }

                if (!HvsE) {
                    Set<OWLAxiom> additionalElk = new HashSet<>(axiomsElk);
                    additionalElk.removeAll(axiomsHermit);

                    message.append("Elk infers axioms that are not inferred by Hermit.");

                    if (!additionalElk.isEmpty()) {
                        message.append("\nadditional Axioms from Elk:\n");
                        for (OWLAxiom a : additionalElk)
                            message.append(a.toString()).append("\n");
                    }

                    message.append("\n––––––––––––––––––––––––––––––––––––––––––\n");
                }

                if (!OvsE) {
                    Set<OWLAxiom> additionalElk = new HashSet<>(axiomsElk);
                    additionalElk.removeAll(axiomsOpenllet);

                    message.append("Elk infers axioms that are not inferred by Openllet.");

                    if (!additionalElk.isEmpty()) {
                        message.append("\nadditional Axioms from Elk:\n");
                        for (OWLAxiom a : additionalElk)
                            message.append(a.toString()).append("\n");
                    }

                    message.append("\n––––––––––––––––––––––––––––––––––––––––––\n");
                }

                Exception e = new Exception(message.toString());
                logException(e, ontFile, "Reasoner comparison");
                exit(1);
            }
            */


            // disable check of jena API if desired
            if (!Arrays.stream(args).toList().contains("--nojena")) {
                System.out.println("test Jena API");
                runJenaApi(ontFile);
            }


        }
    }

    public static List<Anomaly> testReasoners(OWLOntology ont) {
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
            logException(e, ontFile, "Owl API");
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
            logException(e, ontFile, "Jena API");
            exit(1);
        }
    }



    private static PrintWriter getPrintWriter() {
        int i = 0;
        String folder = "/home/tobias/Documents/programming/ontologies/RDFuzz/turtle";
        String name = folder + "/" + "found_bugs/error" + i +".txt";
        while (new File(name).exists()) {
            i++;
            name = folder + "/" + "found_bugs/error" + i + ".txt";
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

    // returns the number of inferred axioms
    private static Set<OWLAxiom> inferAxioms(OWLReasoner reasoner, OWLOntology ont) throws OWLOntologyCreationException {
        // infer properties and class assertions
        List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();
        /*gens.add( new InferredPropertyAssertionGenerator());
        gens.add(new InferredClassAssertionAxiomGenerator());
        gens.add(new InferredSubClassAxiomGenerator());
        gens.add( new InferredDisjointClassesAxiomGenerator());
        gens.add( new InferredEquivalentClassAxiomGenerator());

         */
        gens.add( new InferredEquivalentDataPropertiesAxiomGenerator());
        /*gens.add( new InferredEquivalentObjectPropertyAxiomGenerator());
        gens.add( new InferredInverseObjectPropertiesAxiomGenerator());
        gens.add( new InferredObjectPropertyCharacteristicAxiomGenerator());
        gens.add( new InferredSubDataPropertyAxiomGenerator());
        gens.add(new InferredDataPropertyCharacteristicAxiomGenerator());
        gens.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
        gens.add( new InferredSubObjectPropertyAxiomGenerator());

         */




        InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
        OWLOntology infOnt = manager.createOntology();
        iog.fillOntology(ont.getOWLOntologyManager().getOWLDataFactory(), infOnt);

        for (OWLAxiom a : infOnt.getAxioms())
            System.out.println(a);

        return infOnt.getAxioms();
    }

    private static void logException(Exception e, File ontFile, String type) {
        PrintWriter writer = getPrintWriter();

        writer.println("Found error while parsing file " + ontFile);
        writer.println("Error occured while testing " + type);
        writer.println();

        writer.print(e);
        writer.println("\n––––––––––––––––––––––––––––––––––––––––––");
        writer.println("–––––––––––    Ontology File    ––––––––––––");
        writer.println("––––––––––––––––––––––––––––––––––––––––––\n");

        // add content of turtle file
        try (BufferedReader br = new BufferedReader(new FileReader(ontFile))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                writer.println(i + "\t" + line);
                ++i;
                // process the line.
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        writer.close();
    }

    // checks if ontology is in profile
    public static boolean isEL(OWLOntology ont) {
        OWLProfileReport profileReport = new OWL2ELProfile().checkOntology(ont);
        return profileReport.isInProfile();
    }
}

