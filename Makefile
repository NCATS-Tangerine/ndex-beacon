docker-build:
	gradle build
	docker build -t ncats:ndex .

docker-run:
	docker run --rm --name ndex -p 8076:8080 ncats:ndex

docker-stop:
	docker stop ndex

docker-logs:
	docker logs ndex
