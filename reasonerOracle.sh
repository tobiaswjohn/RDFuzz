#!/bin/bash

# runs test on provided .owl file
# returns "pass" or "fail", depending on whether the reasoners agree with each other

ontFile=$1


# log file 
log="temp_reasoners.log"
rm -f $log

# use java properties file to prevent unnormal logging
java -Djava.util.logging.config.file=api_tester/src/main/resources/logging.properties -jar api_tester/target/api_tester-1.0-SNAPSHOT.jar $ontFile nothing nothing --test-reasoners --no-export >> $log 2>&1
#java -jar api_tester/target/api_tester-1.0-SNAPSHOT.jar $ontFile nothing nothing --test-reasoners --no-export >> $log 2>&1

if [[ $? == 1 ]] ; then
    # create output, if error is found
    echo "fail"
    count=$((count+1))
else
	echo "pass"
fi
