#!/bin/bash

# copies all files specified in "fileorder.txt"

fileList=fileorder.txt

goalDirectory=./ontologies
mkdir $goalDirectory

while IFS="" read p || [ -n "$p" ]
do
  q=$(echo $p | sed -e 's/\r//g')
  cp "../../files/${q}" "$goalDirectory/$q"
  echo "$q"
done <$fileList
