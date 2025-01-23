# Ubuntu 22.04 base image
FROM ubuntu:22.04

# update system
RUN apt-get -y update
RUN apt-get -y upgrade

# copy needed resources
#COPY ./ISLaResources ./RDFuzz/ISLaResources
COPY ./api_tester/src ./RDFuzz/api_tester/src
COPY ./api_tester/pom.xml ./RDFuzz/api_tester/pom.xml
COPY ./api_tester/dependency-reduced-pom.xml ./RDFuzz/api_tester/dependency-reduced-pom.xml


# install java + maven
RUN apt -y install openjdk-17-jre
RUN apt -y install openjdk-17-jdk
RUN apt -y install git maven

# build project
WORKDIR "/RDFuzz/api_tester"
RUN mvn clean package
WORKDIR "/"

COPY ./reasonerOracle.sh ./RDFuzz/reasonerOracle.sh

# two test ontlogies: first: pass; second: fail
COPY ./test_cases/ore_2015/consistency/temp_anomaly_test5.owl ./RDFuzz/test_cases/ore_2015/consistency/temp_anomaly_test5.owl
COPY ./test_cases/ore_2015/consistency/ontologies/ore_ont_264.owl ./RDFuzz/test_cases/ore_2015/consistency/ontologies/ore_ont_264.owl

# set entry directory
WORKDIR "/RDFuzz"
