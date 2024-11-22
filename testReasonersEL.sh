#!/bin/bash

# generates test cases until error is found

log=temp.log

rm $log

# build 
echo "build source code"
cd api_tester
mvn clean package
cd ..

cd ISLaResources
# source to have access to ISLA
source venv/bin/activate

runs=${1:-10}
count=0

for i in $(seq 1 $runs);
do

    # generate new turtle file

    ontFile="../testOntologies/test$i.owl"

    echo "generate test number $i"
    python generator_OWL_EL.py $ontFile


    # test parsing with OWL API
    
    echo "run test number $i"
    echo "run test number $i" >> $log

    java -jar ../api_tester/target/api_tester-1.0-SNAPSHOT.jar $ontFile --test-reasoners >> $log 2>&1
    
    

    if [[ $? == 1 ]] ; then
        # create output, if error is found
        echo "Error found and safed to $ontFile"
        count=$((count+1))
    fi
done

echo "Found $count bugs in $runs test runs."
