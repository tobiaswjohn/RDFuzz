#!/bin/bash

# minimizes a test ontology; i.e. removes axioms while still keeping same set of anomalies
# first argument: ontology to minimize
# optinal argument: timeout (in seconds)
# secon optional argument: max heap size in GB


ontFile=$1

maxTime=${2:-86400} # no timeout: 24h
heapLimit=${3:-8} # no limit: 8GB


limit=-Xmx${heapLimit}G 

# log file 
#log="temp_reasoners.log"
#rm -f $log

# heap size: 16GB
# use java properties file to prevent unnormal logging
#timeout $maxTime java -Djava.util.logging.config.file=api_tester/src/main/resources/logging.properties -jar api_tester/target/api_tester-1.0-SNAPSHOT.jar $ontFile nothing nothing --test-reasoners --no-export >> $log 2>&1
timeout $maxTime java $limit -Djava.util.logging.config.file=api_tester/src/main/resources/logging.properties -jar api_tester/target/api_tester-1.0-SNAPSHOT.jar $ontFile ./ nothing --no-export --print-anomalies --test-reasoners --minimize-ontology 

