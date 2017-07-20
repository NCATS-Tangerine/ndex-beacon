# ndex-beacon #

Knowledge Beacon wrapper for the NDex graph repository

## Quickstart Using Docker ##

At the moment Docker is only available to Linux users. Docker can be installed through a shell script available online.

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
