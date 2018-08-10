# ndex-beacon #

Knowledge Beacon wrapper for the NDex Bio graph repository

## Quickstart

Clone the project:

```shell
git clone https://github.com/NCATS-Tangerine/ndex-beacon.git
```

You can run the project directly on your machine or from within a Docker container.

### Running Directly on Machine

*[Optional]* Set the port you want to run the beacon on. By default the port is set to **8080** (localhost:8080). Open `server/src/main/resources/application.properties`, and change `server.port`.

Build the project with Gradle (we are currently using Gradle 4.7). From the root folder of the project:

```shell
gradle build
```

Execute the JAR file:

```shell
java -jar build/libs/ndex-beacon-#.#.#.jar
```

where *#.#.#* is the release number of the application (e.g. 1.0.18)

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
