# Ideas for Fuzzing RDF 

## Questions about standard
- [W3 standard](https://www.w3.org/TR/turtle/#grammar-production-base) for turtle
1. how does one parse "true" or "false" as literals?
   - could be a string, if parsed as RDFLiteral
   - could be a bool, if parsed as BooleanLiteral
2. definition of parsing of PNAM_NS if parsed as prefix: what happens if the key is already in the map?
3. Can there be more than one definition of a "base" IRI?

## Practical Stuff
- constraint about prefixes seems to be the only restriction (apart from questions above)
- → maybe solve this by adding necessary declarations after generating file → easier than finding solution that satisfies contraint
- generating complex / long files seems to be difficult
    - either: already enough for testing
    - or: concatenate several shorter grammars