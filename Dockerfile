FROM ubuntu:latest
MAINTAINER Richard Bruskiewich <richard@starinformatics.com>
USER root
RUN apt-get -y update
RUN apt-get -y install default-jre
ADD server/build/libs/ndex-beacon*.jar ./ndex-beacon.jar
CMD ["java","-jar","ndex-beacon.jar"]

