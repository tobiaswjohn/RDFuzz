I am using Openllet to compute inferred axioms and I discovered an incorrect inference. Here is a minimal example. 

- Ontology in functional syntax:
```
Prefix(:=<http://www.example.org/reasonerTester#>)

Ontology (
	Declaration(Class(:C))
	Declaration(NamedIndividual(:a))
	Declaration(NamedIndividual(:d))
	Declaration(ObjectProperty(:qsim))
	
	EquivalentClasses(  
	    ObjectHasSelf(:qsim) 
	    ObjectOneOf(:d) 
	    :C 
    )
)
```

- The problem:
Openllet infers the axiom `ClassAssertion(:C :a)`, which is not entailed by the ontology.
- The behavior of Openllet is a bit puzzling as the result of the inference seems to depend on the name of the individual, class and relation used in the `EquivalentClass` axiom. E.g., the ontology incorrect inference disappears if one uses the individual `:a` instead of `:d` or the relation `:r` instead of `:qsim`. As far as I can see, there should be no difference in the behavior of the reasoner.

- For reproduction, here is the call from my program using OWL API:
```
OWLOntologyDocumentSource source = new FileDocumentSource(ontFile, new FunctionalSyntaxDocumentFormat());
OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
ont = manager.loadOntologyFromOntologyDocument(source);
OWLReasoner openllet = OpenlletReasonerFactory.getInstance().createReasoner(ont);

List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();
gens.add( new InferredClassAssertionAxiomGenerator());

InferredOntologyGenerator iog = new InferredOntologyGenerator(openllet, gens);
OWLOntology infOnt = manager.createOntology();
iog.fillOntology(ont.getOWLOntologyManager().getOWLDataFactory(), infOnt);


```

