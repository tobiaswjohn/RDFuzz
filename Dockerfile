# Ubuntu 22.04 base image
FROM ubuntu:22.04

# copy needed resources
COPY ./ISLaResources ./RDFuzz/ISLaResources
COPY ./api_tester/src ./RDFuzz/api_tester/src
COPY ./api_tester/pom.xml ./RDFuzz/api_tester/pom.xml
COPY ./api_tester/dependency-reduced-pom.xml ./RDFuzz/api_tester/dependency-reduced-pom.xml

COPY ./owl-el-campaign.sh ./RDFuzz/owl-el-campaign.sh
COPY ./owl-el-generator.sh ./RDFuzz/owl-el-generator.sh
COPY ./rdf-ttl-campaign.sh ./RDFuzz/rdf-ttl-campaign.sh
COPY ./rdf-ttl-generator.sh ./RDFuzz/rdf-ttl-generator.sh


# update system
RUN apt-get -y update
RUN apt-get -y upgrade

# install isla
RUN apt install -y python3.10 python3.10-dev python3.10-venv python3-pip gcc g++ make cmake git clang
WORKDIR "/RDFuzz/ISLaResources"
RUN python3.10 -m venv venv
ENV PATH="/venv/bin:$PATH"
RUN pip install --upgrade pip
RUN pip install isla-solver
WORKDIR "/"

# install java + maven
RUN apt -y install openjdk-17-jre
RUN apt -y install openjdk-17-jdk
RUN apt -y install git maven

# build project
WORKDIR "/RDFuzz/api_tester"
RUN mvn clean package
WORKDIR "/"


# set entry directory
WORKDIR "/RDFuzz"

