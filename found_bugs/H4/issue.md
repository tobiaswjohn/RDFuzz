I am using HermiT to compute inferred axioms and I discovered an incorrect inference. Here is a minimal example. 

- Ontology in functional syntax:
```
Prefix(:=<http://www.example.org/reasonerTester#>)

Ontology (
	Declaration(ObjectProperty(:p))
	Declaration(ObjectProperty(:q))
	Declaration(DataProperty(:dp))
	Declaration(DataProperty(:dq))

	ObjectPropertyDomain( 
	    :q
	    ObjectHasSelf(:p)
	)
	
	SubClassOf(  
	    DataHasValue(:dq "s") 
	    DataHasValue(:dp "s1")
	) 
)
```

- The problem:
HermiT infers the axiom `SubObjectPropertyOf(:q, :p)`, which can not be inferred from the ontology. Interestingly, the `SubClassOf` axiom seems to be unrelated from this inference, but the wrong inference disappears if one deletes the `SubClassOf` axiom.

- For reproduction, here is the call from my program using OWL API:
```
OWLOntologyDocumentSource source = new FileDocumentSource(ontFile, new FunctionalSyntaxDocumentFormat());
OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
ont = manager.loadOntologyFromOntologyDocument(source);
ReasonerFactory rf = new ReasonerFactory();
OWLReasoner hermit = rf.createReasoner(ont);

List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();
gens.add( new InferredSubObjectPropertyAxiomGenerator());

InferredOntologyGenerator iog = new InferredOntologyGenerator(hermit, gens);
OWLOntology infOnt = manager.createOntology();
iog.fillOntology(ont.getOWLOntologyManager().getOWLDataFactory(), infOnt);

```

