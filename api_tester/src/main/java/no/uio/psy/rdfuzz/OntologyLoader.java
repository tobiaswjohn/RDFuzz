package no.uio.psy.rdfuzz;

import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;

// simple class that uses different strategies to load ontology file based on ending
public class OntologyLoader {
    OWLOntologyManager manager;

    public OntologyLoader(OWLOntologyManager manger) {
        this.manager = manger;
    }

    public OWLOntology loadOntologyFile(File ontFile)  throws OWLOntologyCreationException {
        // check, if ontology is in turtle format
        if (ontFile.getName().endsWith(".ttl"))
            return loadTurtleFile(ontFile);
        else {
            return loadFunctionalSyntaxFile(ontFile);
        }
    }

    public OWLOntology loadTurtleFile(File ontFile)  throws OWLOntologyCreationException {
        OWLOntologyDocumentSource source =
                new FileDocumentSource(ontFile, new TurtleDocumentFormat());

        return manager.loadOntologyFromOntologyDocument(source);
    }

    public OWLOntology loadFunctionalSyntaxFile(File ontFile)  throws OWLOntologyCreationException {
        OWLOntologyDocumentSource source =
                new FileDocumentSource(ontFile, new FunctionalSyntaxDocumentFormat());

        return manager.loadOntologyFromOntologyDocument(source);
    }

}
