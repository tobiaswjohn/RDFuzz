#!/bin/bash

# creates fresh docker container, as used by OntoMutate

containerName=${1:-reasonerContainer} # no timeout: 24h


# remove old container
docker rm -f $containerName

# build image
docker build --no-cache --pull -f Dockerfile.oracle -t reasoner_tester .

# create new container
docker run -d --name $containerName -it reasoner_tester


