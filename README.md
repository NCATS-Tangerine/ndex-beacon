# ndex-beacon #

Knowledge Beacon wrapper for the NDex Bio graph repository

## Quickstart

Clone the project:

```shell
git clone --recursive https://github.com/NCATS-Tangerine/ndex-beacon.git
```

The use of the `--recursive` flag reflects the use of an embedded *ontology* submodule. If you get ontology errors at 
some future time, you may need to use the `git submodule update` operation to update this module.

You can run the project directly on your machine or from within a Docker container.

### Running Directly on Machine

Copy over the server/src/main/resources/{application.properties => application.properties-template file into an 
application.properties file in the same directory. Configure as needed.

*[Optional]* For example, reset the port you want to run the beacon on. By default, the port is set to 
**8076** (localhost:8076). To reset, open `server/src/main/resources/application.properties`, and change `server.port`.

Build the project with Gradle (we currently specific a wrapper using Gradle 5.2). From the root folder of the project:

```shell
gradle build
```

If the build is failing make sure you have pulled all git submodules, you may have missed the `--recursive` flag when 
cloning. If it's still failing check that you're using Gradle version 5.2 or higher, as previous versions may fail. 

Once the JAR file has been built you may execute it:

```
java -jar server/build/libs/ndex-beacon-#.#.#.jar
```

where *#.#.#* is the release number of the application (e.g. 1.1.2). (You may also be able to use an astrix as a 
wild card, and use the command `java -jar server/build/libs/ndex-beacon-*.jar`).

You may then view the Swagger UI at http://localhost:8076/beacon/ndex/swagger-ui.html

# Running with Docker

To run Docker, you'll obviously need to [install Docker first](https://docs.docker.com/engine/installation/) in your target Linux operating environment (bare metal server or virtual machine running Linux).

For our installations, we typically use Ubuntu Linux, for which there is an [Ubuntu-specific docker installation using the repository](https://docs.docker.com/engine/installation/linux/docker-ce/ubuntu/#install-using-the-repository).
Note that you should have 'curl' installed first before installing Docker:

```
sudo apt install curl
```

For other installations, please find instructions specific to your choice of Linux variant, on the Docker site.

## Testing Docker

In order to ensure that Docker is working correctly, run the following command:

```
sudo docker run hello-world
```

This should result in something akin to the following output:

```
Unable to find image 'hello-world:latest' locally
latest: Pulling from library/hello-world
ca4f61b1923c: Pull complete
Digest: sha256:be0cd392e45be79ffeffa6b05338b98ebb16c87b255f48e297ec7f98e123905c
Status: Downloaded newer image for hello-world:latest

Hello from Docker!
This message shows that your installation appears to be working correctly.

To generate this message, Docker took the following steps:
 1. The Docker client contacted the Docker daemon.
 2. The Docker daemon pulled the "hello-world" image from the Docker Hub.
    (amd64)
 3. The Docker daemon created a new container from that image which runs the
    executable that produces the output you are currently reading.
 4. The Docker daemon streamed that output to the Docker client, which sent it
    to your terminal.

To try something more ambitious, you can run an Ubuntu container with:
 docker run -it ubuntu bash

Share images, automate workflows, and more with a free Docker ID:
 https://cloud.docker.com/

For more examples and ideas, visit:
 https://docs.docker.com/engine/userguide/
```

## Running the Docker Container

Then with Docker installed, you can build an image from the `Dockerfile` provided in the main directory of this project.

```shell
docker build -t ncats:ndex .
```

Within the Docker container, the Springboot Server is set to run at `localhost:8076`. You can expose and re-map the 
ports when you run a Docker image with the `-p` flag.

```shell
docker run -p 8076:8076 ncats:ndex
```

Now open your browser to http://localhost:8076/beacon/ndex/swagger-ui.html to see the application running.

## Overview (developer's comments)

This beacon queries for NDEx networks by our search parameters, and then queries those resulting networks again for 
the desired data. All data that the beacon discovers is cached, and used for the metadata endpoints

The `bio.knowledge.server.json` package contains the Java implementations of NDEx models. Documentation for the NDEx 
data model is here: www.home.ndexbio.org/data-model/

The swagger documentation is here: http://openapi.ndexbio.org/#/

NDEx uses lucene search: https://lucene.apache.org/core/

In NdexClient.java we use a number of endpoints to query nDex:

The **BASE URL** is http://www.ndexbio.org/v2

**NETWORK SEARCH** /search/network?start={start}&size={size}

This queries for all networks with the given search term (which is provided in the body of the post request). 
Among other things, it returns the network ID's for matching networks.

**QUERY FOR NODE MATCH** /search/network/{networkId}/interconnectquery

We feed a network ID, and it returns all the data (nodes, edges, node attributes, edge attributes, citations, etc.) 
that match those search terms. Again the search term is given in the body of the post request. This only returns data 
(nodes, edges, etc.) matches.

**QUERY FOR NODE AND EDGES** /search/network/{networkId}/query

This functions similarly as interconnectquery, but returns all the data that interconnectquery would plus all nodes 
and edges and so on that those given nodes are connected to.

**NETWORK SUMMARY** /network/{networkId}/summary

This endpoint provides a summary of information about the network. We use it to get citation data for the entire network.
