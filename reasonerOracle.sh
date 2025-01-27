#!/bin/bash

# runs test on provided .owl file
# returns "pass" or "fail", depending on whether the reasoners agree with each other

# optinal argument: timeout (in seconds)


ontFile=$1
maxTime=${2:-86400} # no timeout: 24h


# log file 
log="temp_reasoners.log"
rm -f $log

wasTimeout=0

# use java properties file to prevent unnormal logging
timeout $maxTime java -Djava.util.logging.config.file=api_tester/src/main/resources/logging.properties -jar api_tester/target/api_tester-1.0-SNAPSHOT.jar $ontFile nothing nothing --test-reasoners --no-export >> $log 2>&1
#java -jar api_tester/target/api_tester-1.0-SNAPSHOT.jar $ontFile nothing nothing --test-reasoners --no-export >> $log 2>&1

exitCode=$?

if [[ $exitCode == 124 ]] ; then
    # time limit exceeded
    echo "timeout"
elif [[ $exitCode == 1 ]] ; then
    echo "fail"
else
	echo "pass"
fi

