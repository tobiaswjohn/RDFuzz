I am using Openllet to decide the consistency of an ontology and found an example where the decision is not correct. Here is a minimal example. 

- Ontology in functional syntax:
```
Prefix(:=<http://www.example.org/reasonerTester#>)
Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)

Ontology (
	Declaration(NamedIndividual(:a))
	Declaration(NamedIndividual(:c))
	Declaration(ObjectProperty(:rsim))
	Declaration(ObjectProperty(:snonsim))
	Declaration(DataProperty(:dp))
	Declaration(DataProperty(:dr))
	
	EquivalentClasses(
	    ObjectHasSelf(:rsim) 
	    ObjectOneOf(:c)		# renaming :c to :a makes a difference
	    ObjectSomeValuesFrom(:snonsim DataSomeValuesFrom(:dp rdfs:Literal)) 
	    DataHasValue(:dr "s") 
	)
	
	DataPropertyDomain( 
	    :dp 
	    DataHasValue(:dr "s")
	)
)


```

- The problem:
Openllet infers that the ontology is inconsistent. I cross-checked the consistency with HermiT, which classified the ontology as consistent. 
- The behavior of Openllet is a bit puzzling as the result of the consistency check seems to depend on the name of the individual in the `EquivalentClass` axiom. The ontology is correctly classified as consistent, if one uses the individual `:a` instead of `:c`. As far as I can see, there should be no difference in the behavior of the reasoner.

- For reproduction, here is the call from my program using OWL API:
```
OWLOntologyDocumentSource source = new FileDocumentSource(ontFile, new FunctionalSyntaxDocumentFormat());
OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
ont = manager.loadOntologyFromOntologyDocument(source);
OWLReasoner openllet = OpenlletReasonerFactory.getInstance().createReasoner(ont);

openllet.isConsistent()
```

