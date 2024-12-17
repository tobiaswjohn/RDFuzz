# Supplementary Material for Paper "Language-Based Testing for Knowledge Graphs"

this repository contains the code for generating random OWL ontologies and turtle files using [ISLa](https://github.com/rindPHI/isla) and the test cases and found anomalies documented in the paper.

## Structure of Repostitory
 - ISLaResources: contains environment to run ISLa and grammars and scripts to call generate test files
 - api_tester: contains java files that call tools and classify and document anomalies
 - found_anomalies: folder that saves test runs and contains found anomalies (in sub-folders) 
 - found_bugs: the indentified bugs that are mentioned in the paper

 
## Installation
- comments to set up docker 

## Results In Paper
- the test cases and found anomalies for the OWL-EL campaign, as well as the anomalies sorted as described in the paper are in folder [found_anomalies/el_reasoners/rdfuzz/test_run_2024_11_29_15_17](found_anomalies/el_reasoners/rdfuzz/test_run_2024_11_29_15_17)
- the test cases found anomalies for the RDF-TTL campaign, as well as the anomalies sorted as described in the paper are in folder [found_anomalies/turtle_parsers/rdfuzz/test_run_2024_11_30_10_14](found_anomalies/turtle_parsers/rdfuzz/test_run_2024_11_30_10_14)

## Generate Test Inputs
 - `rdf-ttl-generator.sh`
	 + script to generate RDF-TTL files
	 + argument 1 (type of grammar): 
		 * `-pure`: generates RDF-TTL files
		 * `-tailored`: generates RDF-TTL files that do not contain known bugs (also used in testing campaign)
	+ argument 2 (type of limit):
		* `-time`: the script stops when time limit (in seconds) is reached
		* `-count`: the script stops when specified number of inputs was created
	+ argument 3: a number, indicating the limit specified as the second argument
 - `owl-el-generator.sh`
	 + script to generate OWL-EL ontologies
	 + argument 1 (type of limit): 
		* `-time`: the script stops when time limit (in seconds) is reached
		* `-count`: the script stops when specified number of inputs was created
	+ argument 2: a number, indicating the limit specified as the first argument
- an example call, to generate OWL-EL ontologies for one minute would look like this:
```
./owl-el-generator.sh -time 60
```
	 
## Reproduce Fuzzing Campaigns
 - `owl-el-campaign.sh`
	 + script to test OWL-EL reasoners (HermiT, Openllet, ELK) 
	 + argument: number of minutes the fuzzing should run (default: 1min)
	 + results will be in folder `found_anomalies/el_reasoners`
 - `rdf-ttl-campaign.sh`
	 + script to test RDF-TTL parsers (OWL-API, Apache Jena) 
	 + argument: number of minutes the fuzzing should run (default: 1min)

