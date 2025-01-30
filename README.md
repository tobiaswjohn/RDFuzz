# Fuzzing of RDF Software

this repository contains the code for generating random OWL ontologies and turtle files using [ISLa](https://github.com/rindPHI/isla)

## Supplementary Material for Paper "Language-Based Testing for Knowledge Graphs"
All files related to our paper can be found on branch _ESWC_

## Structure of Repostitory
 - ISLaResources: contains environment to run ISLa and grammars and scripts to call generate test files
 - api_tester: contains java files that call tools and classify and document anomalies

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
	 
### Run Fuzzing Campaigns
There are two scripts to run testing campaigns. The scripts expect a time limit in minutes.
 - `rdf-ttl-campaign.sh`
	 + script to test RDF-TTL parsers (OWL-API, Apache Jena) 
	 + argument: number of minutes the fuzzing should run (default: 1min)
	 + results will be in folder `found_anomalies/turtle_parsers`
 - `owl-el-campaign.sh`
	 + script to test OWL-EL reasoners (HermiT, Openllet, ELK) 
	 + argument: number of minutes the fuzzing should run (default: 1min)
	 + results will be in folder `found_anomalies/el_reasoners`

