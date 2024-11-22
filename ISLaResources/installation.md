# Installation (Requirements)  

 - Python 3
	 + `sudo apt -y upgrade`
 - Isla
	 + see [github](https://github.com/rindPHI/isla#build-run-install)
	 + `sudo apt install python3.10 python3.10-dev python3.10-venv gcc g++ make cmake git clang`
	 + `cd RDFuzz/ISLaResources`
	 
 ```
	 python3.10 -m venv venv
	source venv/bin/activate
	pip install --upgrade pip
	pip install isla-solver
 ```
 - java + maven
	 + `sudo apt install openjdk-17-jre`
	 + `sudo apt install openjdk-17-jdk`
	 + `sudo apt -y install git maven`