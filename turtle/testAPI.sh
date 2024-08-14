#!/bin/bash

# generates test cases until error is found

# build 
echo "build source code"
cd api_tester
mvn clean package
cd ..

# source to have access to ISLA
source venv/bin/activate

for i in $(seq 1 10);
do
    # generate new turtle file

    ontFile="tests/test$i.ttl"

    python turtle_generator.py $ontFile


    # test parsing with OWL API

    java -jar api_tester/target/api_tester-1.0-SNAPSHOT.jar $ontFile

    if [[ $? == 1 ]] ; then
        # create output, if error is found
        echo "Error found."
    fi
done
