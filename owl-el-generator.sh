#!/bin/bash

# generates an RDF-TTL test case


# arguments: 
# -time OR -count    (selects when to stop; time in seconds or number of generated test cases)
# BOUND              (a number, defining when to stop)

time=false


#### parse arguments
if [$# -ne 2]; then 
    echo "Please provide two arguments."
    exit 1
fi

# parse first argument
if [ $1 == "-time" ]; then
      time=true
    else 
        if [ $1 != "-count" ]; then
            echo "Please provide '-time' or '-count' as the first argument."
            exit 1
        fi
fi

# parse second argument
limit=$2




### set up directory
hostname=$(hostname)    # name of computer that runs benchmark
suts="owl_rdf"
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
        python generator_OWL_EL.py $ontFile >> ../$log
        i=$(( i + 1 ))
    done
else
    for i in $(seq 1 $limit); 
    do
        # generate new ontology files
        ontName="input$i.ttl"
        ontFile="../${directory}/${ontName}" 
        # generate test file
        python generator_OWL_EL.py $ontFile >> ../$log
    done
fi

cd ..

echo "Generated $i test cases."
echo "Generated $i test cases." >> $log

