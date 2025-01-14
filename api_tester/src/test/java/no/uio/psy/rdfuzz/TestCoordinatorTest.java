package no.uio.psy.rdfuzz;

import no.uio.psy.rdfuzz.anomalies.Anomaly;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TestCoordinatorTest {
    TestCoordinator testCoordinator = new TestCoordinator();
    Set<REASONING_TASKS> allReasoningTasks = Set.of(
            REASONING_TASKS.CONSISTENCY,
            REASONING_TASKS.INFERRED_AXIOMS
            );


    @Test
    void testReasoners() {

        File ontFile1 = new File("src/test/resources/test5.owl");
        List<Anomaly> anomalies1 = testCoordinator.testReasoners(ontFile1, allReasoningTasks);
        assertEquals(4, anomalies1.size());

        File ontFile2 = new File("src/test/resources/inferenceAnomaly_test1456.owl");
        List<Anomaly> anomalies2 = testCoordinator.testReasoners(ontFile2, allReasoningTasks);
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

    @Test
    void minimalWitness() {
        Set<REASONING_TASKS> consistencyAndInferred = Set.of(
                REASONING_TASKS.CONSISTENCY,
                REASONING_TASKS.INFERRED_AXIOMS
        );
        File ontFile = new File("src/test/resources/test5.owl");
        List<Anomaly> anomalies1 = testCoordinator.testReasoners(ontFile, consistencyAndInferred);
        OWLOntology minimalOnt1 = testCoordinator.minimalWitness(
                new HashSet<>(anomalies1),
                ontFile,
                allReasoningTasks);

        // actual size of minimal ontology can vary as there are multiple solutions with different sizes
        assertTrue(minimalOnt1.axioms().count() < 15);
        assertTrue(minimalOnt1.axioms().count() > 10);


        // minimal, but only w.r.t. consistency
        Set<REASONING_TASKS> consistency = Set.of(
                REASONING_TASKS.CONSISTENCY
        );
        List<Anomaly> anomalies2 = testCoordinator.testReasoners(ontFile, consistency);
        OWLOntology minimalOnt2 = testCoordinator.minimalWitness(
                new HashSet<>(anomalies2),
                ontFile,
                consistency);

        assertEquals(1, minimalOnt2.axioms().count());
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