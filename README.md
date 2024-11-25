# Fuzzing of RDF Software

this repository contains the code for generating random OWL ontologies and turtle files using [ISLa](https://github.com/rindPHI/isla)

## Run Fuzzing
 - `testReasonersEL.sh`
	 + script to test EL reasoners (HermiT, Openllet, ELK) using fuzzing

## Structure of Repostitory
 - ISLaResources: contains environment to run ISLa and grammars and scripts to call generate test files
 - api_tester: contains java files that call tools and classify and document anomalies
 - found_anomalies: folder that saves test runs and found anomalies (in sub-folder) 
 - reduced_bugs: inspected anomalies
 - old_files: files used for early testing
