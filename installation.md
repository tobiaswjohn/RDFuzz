# Installation 
 - download our source files from Zenodo or obtaine them from [github](https://github.com/tobiaswjohn/RDFuzz)
 - the following commands are expected to be exevuted in the folder containing our source files


# Install ISLa (incl. Requirements)
 - Python 3
	 + `sudo apt -y upgrade`
 - Isla
	 + see [github](https://github.com/rindPHI/isla#build-run-install)
	 + `sudo apt install python3.10 python3.10-dev python3.10-venv gcc g++ make cmake git clang`
	 
 - create python environment and install ISLa (and where our tool finds it later)
 	 + `cd ISLaResources`
	 + `python3.10 -m venv venv`
	 + `source venv/bin/activate`
	 + `pip install --upgrade pip`
	 + `pip install isla-solver`
	 + `cd ../`


# Install Requirements to run our Tool
 - Java 
	 + `sudo apt install openjdk-17-jre`
	 + `sudo apt install openjdk-17-jdk`
 - Maven
	 + `sudo apt install git maven`

# Build our Tool
 - `cd api_tester`
 - `mvn clean package`
 - `cd ../`