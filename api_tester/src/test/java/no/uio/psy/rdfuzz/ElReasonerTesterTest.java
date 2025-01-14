package no.uio.psy.rdfuzz;

import no.uio.psy.rdfuzz.anomalies.ExceptionAnomaly;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ElReasonerTesterTest {

    @Test
    void initializeReasoners() {
        File ontFile = new File("src/test/resources/test5.owl");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OntologyLoader ontL = new OntologyLoader(manager);
        OWLOntology ont;
        try {
            ont = ontL.loadOntologyFile(ontFile);
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }

        ElReasonerTester tester = new ElReasonerTester(ont);

        // no anomalies where found during loading of the ontology
        assertTrue(tester.getFoundAnomalies().isEmpty());
    }
}