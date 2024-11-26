package no.uio.psy.rdfuzz;

import no.uio.psy.rdfuzz.anomalies.Anomaly;
import no.uio.psy.rdfuzz.anomalies.ExceptionAnomaly;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParserTester {

    private final Set<Anomaly> foundAnomalies = new HashSet<>();

    public Set<Anomaly> getFoundAnomalies() {
        return foundAnomalies;
    }

    public void testParsers(File ontFile) {
        System.out.println("test OWL API");
        foundAnomalies.addAll(runOwlApi(ontFile));
        System.out.println("test Jena API");
        foundAnomalies.addAll(runJenaApi(ontFile));
    }

    // tries to load ontology using owl api
    private Set<Anomaly> runOwlApi(File ontFile) {
        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OntologyLoader ontL = new OntologyLoader(manager);
            ontL.loadOntologyFile(ontFile);
        } catch (OWLOntologyCreationException e) {

            return Set.of(new ExceptionAnomaly(e, SUT.OWLAPI));
        }
        return Set.of();
    }



    private Set<Anomaly> runJenaApi(File ontFile) {
        try {
            Model jenaModel = RDFDataMgr.loadDataset(ontFile.getAbsolutePath()).getDefaultModel();
        } catch  (Exception e) {
            return Set.of(new ExceptionAnomaly(e, SUT.JENAAPI));
        }
        return Set.of();
    }
}
