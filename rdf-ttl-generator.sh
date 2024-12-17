#!/bin/bash

# generates an RDF-TTL test case


# arguments: 
# -pure OR -tailored  (selects grammar)
# -time OR -count    (selects when to stop; time in seconds or number of generated test cases)
# BOUND                 (a number, defining when to stop)

pure=false
time=false


#### parse arguments

# parse first argument
if [ $1 == "-pure" ]; then
      pure=true
    else 
        if [ $1 != "-tailored" ]; then
            echo "Please provide '-pure' or '-tailored' as the second argument."
            exit 1
        fi
fi

# parse second argument
if [ $2 == "-time" ]; then
      time=true
    else 
        if [ $2 != "-count" ]; then
            echo "Please provide '-time' or '-count' as the second argument."
            exit 1
        fi
fi

# parse third argument
limit=$3




### set up directory
hostname=$(hostname)    # name of computer that runs benchmark
suts="rdf_ttl"
# directory, where found anomalies are stored
directory="test_cases/${suts}/${hostname}/generated_$(date +'%Y_%m_%d_%H_%M')"


# create directories if they does not exist yet
mkdir -p "$directory"

# log file 
log="${directory}/generator.log"



cd ISLaResources
# source to have access to ISLA
source venv/bin/activate

count=0
i=1

if $time ; then
    while [ $SECONDS -lt $limit ]; 
    do
        # generate new ontology files
        ontName="input$i.ttl"
        ontFile="../${directory}/${ontName}" 
        # generate test file
        if $pure ; then
            python turtle_generator_initial.py $ontFile >> ../$log
        else
            python turtle_generator.py $ontFile >> ../$log
        fi
        i=$(( i + 1 ))
    done
else
    for i in $(seq 1 $limit); 
    do
        # generate new ontology files
        ontName="input$i.ttl"
        ontFile="../${directory}/${ontName}" 
        # generate test file
        if $pure ; then
            echo "generate pure"
            python turtle_generator_initial.py $ontFile >> ../$log
        else
            echo "generate tailored"
            python turtle_generator.py $ontFile >> ../$log
        fi
    done
fi

cd ..

echo "Generated $i test cases."
echo "Generated $i test cases." >> $log

