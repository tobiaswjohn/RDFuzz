I am using HermiT to compute inferred axioms and I discovered a missing inference. Here is a minimal example. 

- Ontology in functional syntax:
```
Prefix(:=<http://www.example.org/reasonerTester#>)
Ontology (
	Declaration(NamedIndividual(:a))
	Declaration(DataProperty(:dp))
	
	EquivalentClasses(  
	    ObjectOneOf(:a) 
	    DataHasValue(:dp "data")  
    )
)
```

- The problem:
HermiT does not infer the assertion `DataPropertyAssertion(:dp :a "data")` although it is entailed by the ontology. I also cross checked the result with Openllet, which correctly inferred this assertion.

- For reproduction, here is the call from my program using OWL API:
```
OWLOntologyDocumentSource source = new FileDocumentSource(ontFile, new FunctionalSyntaxDocumentFormat());
OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
ont = manager.loadOntologyFromOntologyDocument(source);
ReasonerFactory rf = new ReasonerFactory();
OWLReasoner hermit = rf.createReasoner(ont);

List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();
gens.add( new InferredPropertyAssertionGenerator());

InferredOntologyGenerator iog = new InferredOntologyGenerator(hermit, gens);
OWLOntology infOnt = manager.createOntology();
iog.fillOntology(ont.getOWLOntologyManager().getOWLDataFactory(), infOnt);

```

