I am using Openllet to compute inferred axioms and I discovered an incorrect inference. Here is a minimal example. 

- Ontology in functional syntax:
```
Prefix(:=<http://www.example.org/reasonerTester#>)

Ontology (
	Declaration(Class(:A))
	Declaration(ObjectProperty(:p))
	Declaration(ObjectProperty(:q))
	Declaration(ObjectProperty(:r))
	
	SubClassOf(  
	    ObjectHasSelf(:q) 
	    ObjectSomeValuesFrom(:r :A)
	)
	
	SubObjectPropertyOf(  
	    :p 
	    :q
	)
)
```

- The problem:
Openllet infers the axiom `IrreflexiveObjectProperty(:p)`, which is not entailed by the ontology. I also cross checked using HermiT, which does not infer this axiom.

- For reproduction, here is the call from my program using OWL API:
```
OWLOntologyDocumentSource source = new FileDocumentSource(ontFile, new FunctionalSyntaxDocumentFormat());
OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
ont = manager.loadOntologyFromOntologyDocument(source);
OWLReasoner openllet = OpenlletReasonerFactory.getInstance().createReasoner(ont);

List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();
gens.add( new InferredObjectPropertyCharacteristicAxiomGenerator());

InferredOntologyGenerator iog = new InferredOntologyGenerator(openllet, gens);
OWLOntology infOnt = manager.createOntology();
iog.fillOntology(ont.getOWLOntologyManager().getOWLDataFactory(), infOnt);

```

