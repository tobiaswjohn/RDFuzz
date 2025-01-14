package no.uio.psy.rdfuzz;

import no.uio.psy.rdfuzz.anomalies.Anomaly;
import org.apache.jena.base.Sys;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestCoordinatorTest {
    TestCoordinator testCoordinator = new TestCoordinator();


    @Test
    void testReasoners() {
        File ontFile1 = new File("src/test/resources/test5.owl");
        List<Anomaly> anomalies1 = testCoordinator.testReasoners(ontFile1);
        assertEquals(4, anomalies1.size());

        File ontFile2 = new File("src/test/resources/inferenceAnomaly_test1456.owl");
        List<Anomaly> anomalies2 = testCoordinator.testReasoners(ontFile2);
        assertEquals(3, anomalies2.size());
    }

    @Test
    void testParsers() {
        File ontFile1 = new File("src/test/resources/parserTests/owl_api.ttl");
        List<Anomaly> anomalies1 = testCoordinator.testParsers(ontFile1);
        assertEquals(1, anomalies1.size());

        File ontFile2 = new File("src/test/resources/parserTests/jena_api.ttl");
        List<Anomaly> anomalies2 = testCoordinator.testParsers(ontFile2);
        assertEquals(1, anomalies2.size());
    }

    @Test
    void isEL() {
        File ontFile = new File("src/test/resources/notEL_test272.owl");
        assertFalse(testCoordinator.isEL(loadOnt(ontFile)));
    }


    OWLOntology loadOnt(File ontFile) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OntologyLoader ontL = new OntologyLoader(manager);
        OWLOntology ont;
        try {
            ont = ontL.loadOntologyFile(ontFile);
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
        return  ont;
    }
}