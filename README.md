# Supplementary Material for Paper "Language-Based Testing for Knowledge Graphs"

This repository contains the code for generating random RDF-TTL files and OWL-EL ontologies using [ISLa](https://github.com/rindPHI/isla), a test oracle common for RDF-TTL parsers and OWL-EL reasoners and the test cases and found anomalies documented in the paper.

## Structure of Repostitory
 - ISLaResources: contains environment to run ISLa and grammars and scripts to call generate test files
 - api_tester: contains java files that call tools and classify and document anomalies
 - found_anomalies: folder that saves test runs and contains found anomalies (in sub-folders) 
 - found_bugs: the indentified bugs that are mentioned in the paper

## Results Discussed in Paper
- the test cases and found anomalies for the RDF-TTL campaign are in folder [found_anomalies/turtle_parsers/rdfuzz/test_run_2024_11_30_10_14](found_anomalies/turtle_parsers/rdfuzz/test_run_2024_11_30_10_14)
- the test cases and found anomalies for the OWL-EL campaign, as well as the anomalies classified as discussed in the paper are in folder [found_anomalies/el_reasoners/rdfuzz/test_run_2024_11_29_15_17](found_anomalies/el_reasoners/rdfuzz/test_run_2024_11_29_15_17)


## Usage
### Usage using Docker (Recommended)
- build the docker image using the provided docker file
```
docker build -t rdfuzz .
```
- create a new container, where you can run the scripts in
```
docker run -it rdfuzz
```

### Usage without Docker / Manual Installation
If you do not want to use docker, you can find the instructions how to install  ISLa, our tool and all necessary requirements in [installation.md](installation.md).


### Generate Test Inputs
There are two scripts to generate random files for testing. Different options for the used grammar and the limit when to stop the generation of test cases can be selected.
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
	 
### Reproduce Fuzzing Campaigns
There are two scripts to reproduce the testing campaigns from our paper. The scripts expect a time limit in minutes.
 - `rdf-ttl-campaign.sh`
	 + script to test RDF-TTL parsers (OWL-API, Apache Jena) 
	 + argument: number of minutes the fuzzing should run (default: 1min)
 - `owl-el-campaign.sh`
	 + script to test OWL-EL reasoners (HermiT, Openllet, ELK) 
	 + argument: number of minutes the fuzzing should run (default: 1min)
	 + results will be in folder `found_anomalies/el_reasoners`

To run exactly the campaigns documented in the paper, one can call the scripts in the following way (note, that these calls need 58 hours of total run time):
```
./rdf-ttl-campaign.sh 1440
./owl-el-campaign.sh 600
```

