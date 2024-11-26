#!/bin/bash

# usage: ./testReasonersELBackground.sh TIME-LIMIT 
# tests tools in background for TIME-LIMIT minutes


nohup ./testParsers.sh $1 >temp.log 2>temp.log </dev/null &
