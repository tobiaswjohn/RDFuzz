#!/bin/bash

# generates test cases until time limit is reached

#runs=${1:-10}
timeLimit=${1:-1}   # timeLimit in minutes
timeLimit=$(( timeLimit * 60 ))

endTime=$(date -d "$(date) + ${timeLimit} seconds")



# check

hostname=$(hostname)    # name of computer that runs benchmark
suts="turtle_parsers"
# directory, where found anomalies are stored
directory="found_anomalies/${suts}/${hostname}/test_run_$(date +'%Y_%m_%d_%H_%M')"
directoryOntologies="${directory}/test_ontologies"
directoryAnomalies="${directory}/anomalies"

# create directories if they does not exist yet
mkdir -p "$directory"
mkdir -p "$directoryOntologies"
mkdir -p "$directoryAnomalies"

# log file 
log="${directory}/fuzzing.log"

echo "time limit: ${timeLimit} seconds" >> $log
echo "expected end time: ${endTime}" >> $log


# .csv file to summarize testing results
summaryFile="${directory}/summary.csv"
touch $summaryFile
echo "id,folder,ontology,inEL,exceptions,consistencyAnomalies,inferenceAnomalies,anomalyReport" > $summaryFile

#rm $log

# build 
#cd api_tester
#echo "build java sources"
#echo "build java sources" >> ../$log

#mvn clean package >> ../$log

#cd ..

cd ISLaResources
# source to have access to ISLA
source venv/bin/activate

count=0

i=1

#for i in $(seq 1 $runs);
while [ $SECONDS -lt $timeLimit ]; 
do

    # generate new ontology files
    ontName="test$i.ttl"

    ontFile="../${directoryOntologies}/${ontName}" 

    # documentation

    echo -n "${i},${directory},${ontName}," >> ../$summaryFile

    echo "generate test number $i"
    echo "generate test number $i" >> ../$log

    python turtle_generator.py $ontFile >> ../$log


    # test parsing with OWL API
    
    echo "run test number $i"
    echo "run test number $i" >> ../$log

    java -jar ../api_tester/target/api_tester-1.0-SNAPSHOT.jar $ontFile ../$directoryAnomalies ../$summaryFile --test-parsers >> ../$log 2>&1

    if [[ $? == 1 ]] ; then
        # create output, if error is found
        echo "Error found and safed to $ontFile"
        count=$((count+1))
    fi

    # finish documentation

    echo "" >> ../$summaryFile

    i=$(( i + 1 ))


done

cd ..

echo "Found anomalies in $count cases out of $i test runs within $timeLimit seconds."
echo "Found anomalies in $count cases out of $i test runs within $timeLimit seconds." >> $log

