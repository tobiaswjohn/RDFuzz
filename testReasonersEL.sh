#!/bin/bash

# generates test cases until specified number is reached

runs=${1:-10}

hostname=$(hostname)    # name of computer that runs benchmark
suts="el_reasoners"
# directory, where found anomalies are stored
directory="found_anomalies/${suts}/${hostname}/test_run_$(date +'%Y_%m_%d_%H_%M')"
directoryOntologies="${directory}/test_ontologies"
directoryAnomalies="${directory}/anomalies"

# create directories if they does not exist yet
mkdir -p "$directory"
mkdir -p "$directoryOntologies"
mkdir -p "$directoryAnomalies"




log="${directory}/temp.log"

rm $log

# build 
cd api_tester
echo "build java sources"
echo "build java sources" >> ../$log

mvn clean package >> ../$log

cd ..

cd ISLaResources
# source to have access to ISLA
source venv/bin/activate

count=0

for i in $(seq 1 $runs);
do

    # generate new ontology files

    ontFile="../${directoryOntologies}/test$i.owl"

    echo "generate test number $i"
    echo "generate test number $i" >> ../$log

    python generator_OWL_EL.py $ontFile


    # test parsing with OWL API
    
    echo "run test number $i"
    echo "run test number $i" >> ../$log

    java -jar ../api_tester/target/api_tester-1.0-SNAPSHOT.jar $ontFile ../$directoryAnomalies --test-reasoners >> ../$log 2>&1
    
    

    if [[ $? == 1 ]] ; then
        # create output, if error is found
        echo "Error found and safed to $ontFile"
        count=$((count+1))
    fi
done

echo "Found $count bugs in $runs test runs."
