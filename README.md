# ndex-beacon #

Knowledge Beacon wrapper for the NDex Bio graph repository

## Quickstart

Clone the project:

```shell
git clone --recursive https://github.com/NCATS-Tangerine/ndex-beacon.git
```

You can run the project directly on your machine or from within a Docker container.

### Running Directly on Machine

*[Optional]* Set the port you want to run the beacon on. By default the port is set to **8080** (localhost:8080). Open `server/src/main/resources/application.properties`, and change `server.port`.

Build the project with Gradle (we are currently using Gradle 4.7). From the root folder of the project:

```shell
gradle build
```
If the build is failing make sure you have pulled all git submodules, you may have missed the `--recursive` flag when cloning. If it's still failing check that you're using Gradle version 4.5 or higher, as previous versions will likely fail.

Once the JAR file has been built you may execute it:

```
java -jar server/build/libs/ndex-beacon-#.#.#.jar
```

where *#.#.#* is the release number of the application (e.g. 1.0.18). (You may also be able to use an astrix as a wild card, and use the command `java -jar server/build/libs/ndex-beacon-*.jar`).

You may then view the Swagger UI at http://localhost:8080/beacon/ndex/swagger-ui.html

### Running Using Docker (Linux) ##

Docker can be installed through a shell script available online.

```shell
wget https://get.docker.com -O install.sh
sh install.sh
```
Then with Docker installed, you can build an image from the `Dockerfile` provided in the main directory of this project.

```shell
cd ndex
docker build -t ncats:ndex .
```

Within the Docker container, the Springboot Server is set to run at `localhost:8080`. You can expose and re-map the ports when you run a Docker image with the `-p` flag.

```shell
docker run -p 8080:8080 ncats:ndex
```

Now open your browser to `localhost:8080/api` to see the application running.

## Overview (developer's comments)

This beacon queries for NDEx networks by our search parameters, and then queries those resulting networks again for the desired data. All data that the beacon discovers is cached, and used for the metadata endpoints

The `bio.knowledge.server.json` package contains the Java implementations of NDEx models. Documentation for the NDEx data model is here: www.home.ndexbio.org/data-model/

The swagger documentation is here: http://openapi.ndexbio.org/#/

NDEx uses lucene search: https://lucene.apache.org/core/

In NdexClient.java we use a number of endpoints to query nDex:

The **BASE URL** is http://www.ndexbio.org/v2

**NETWORK SEARCH** /search/network?start={start}&size={size}

This queries for all networks with the given search term (which is provided in the body of the post request). Among other things, it returns the network ID's for matching networks.

**QUERY FOR NODE MATCH** /search/network/{networkId}/interconnectquery

We feed a network ID, and it returns all the data (nodes, edges, node attributes, edge attributes, citations, etc.) that match those search terms. Again the search term is given in the body of the post request. This only returns data (nodes, edges, etc.) matches.

**QUERY FOR NODE AND EDGES** /search/network/{networkId}/query

This functions similarly as interconnectquery, but returns all the data that interconnectquery would plus all nodes and edges and so on that those given nodes are connected to.

**NETWORK SUMMARY** /network/{networkId}/summary

This endpoint provides a summary of information about the network. We use it to get citation data for the entire network.
