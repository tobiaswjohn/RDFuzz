I am using ELK to decide the consistency of ontologies and I discovered an ontology, where the result is not correct. 

- Ontology in functional syntax:
```
Prefix(:=<http://www.example.org/reasonerTester#>)

Ontology (
  Declaration(NamedIndividual(:a))
  Declaration(Class(:B))
  Declaration(DataProperty(:dr))
  
  EquivalentClasses(
      DataHasValue(:dr "s1"@fr) 
      DataHasValue(:dr "s2") 
      :B 
  )
  
  DisjointClasses( 
      DataHasValue(:dr "s2") 
      DataHasValue(:dr "s1"@en) 
  )
  
  ClassAssertion(:B :a)
)
```

- The problem:
The ontology is classified as inconsistent, which I think is not correct. The reason is the following: ELK infers the axiom `EquivalentClasses(:B owl:Nothing)`. Together with the class assertion `B(a)`, this leads to the wrong inference that the ontology is inconsistent. As far as I understand it, the literals `"s1"` and `"s1"@en` should be treated as different literals, i.e. `DataHasValue(:dr "s1"@en)` should describe a different class then `DataHasValue(:dr "s1")`. ELK seems to assume that they are the same. Cross checking the result with HermiT also indicated that the inference of ELK might be incorrect.

- I am aware that the support for literals is only limited but I think users could benefit from extending the existing warnings. The warnings I get contain information that the class inclusions may be incomplete and mention the occurrence of `DataHasValue` as the reason. However, this issue shows a soundness problem. Maybe one can add a warning that if language tags occur, the class inclusions and the consistency assessment might not be sound?

- For reproduction, here is the call from my program using OWL API:
```
OWLOntologyDocumentSource source = new FileDocumentSource(ontFile, new FunctionalSyntaxDocumentFormat());
OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
ont = manager.loadOntologyFromOntologyDocument(source);
ElkReasonerFactory rf = new ElkReasonerFactory();
OWLReasoner elk = rf.createReasoner(ont);

elk.isConsistent()

List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();
gens.add(new InferredEquivalentClassAxiomGenerator());

InferredOntologyGenerator iog = new InferredOntologyGenerator(elk, gens);
OWLOntology infOnt = manager.createOntology();
iog.fillOntology(ont.getOWLOntologyManager().getOWLDataFactory(), infOnt);
```