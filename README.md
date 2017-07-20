# ndex-beacon #

Java Spring Boot knowledge beacon wrapping http://ndexbio.org/.

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

Within the Docker container, the Flask app is set to run at `0.0.0.0:5000`. You can re-map ports when you run a Docker image with the `-p` flag.

```shell
docker run -p 8080:8080 ncats:ndex
```

Now open your browser to `localhost:8080/api` to see the application running.
