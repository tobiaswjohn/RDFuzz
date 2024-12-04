I am using HermiT to compute inferred axioms and I discovered a missing inference. Here is a minimal example. 

- Ontology in functional syntax:
```
Prefix(:=<http://www.example.org/reasonerTester#>)

Ontology (
	Declaration(DataProperty(:dp))
	
    DataPropertyRange(:dp DataOneOf("s") )
)
```

- The problem:
HermiT does not infer the axiom `FuctionalDataProp(:dp)`. I cross checked the reasoning with Openllet, which correctly infers the axiom.

- For reproduction, here is the call from my program using OWL API:
```
OWLOntologyDocumentSource source = new FileDocumentSource(ontFile, new FunctionalSyntaxDocumentFormat());
OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
ont = manager.loadOntologyFromOntologyDocument(source);
ReasonerFactory rf = new ReasonerFactory();
OWLReasoner hermit = rf.createReasoner(ont);

List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();
gens.add( new InferredDataPropertyCharacteristicAxiomGenerator());

InferredOntologyGenerator iog = new InferredOntologyGenerator(hermit, gens);
OWLOntology infOnt = manager.createOntology();
iog.fillOntology(ont.getOWLOntologyManager().getOWLDataFactory(), infOnt);

```

