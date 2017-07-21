FROM ubuntu:16.04

RUN apt-get update

RUN apt-get -y install openjdk-8-jdk wget unzip
RUN java -version

ENV PATH $PATH:/home/gradle-3.4.1/bin/

RUN cd home && \
    wget https://services.gradle.org/distributions/gradle-3.4.1-bin.zip && \
    unzip gradle-3.4.1-bin.zip && \
    mkdir ndex && \
    cd ndex

COPY . /home/ndex/

RUN cd home/ndex && \
    gradle clean -x test && \
    gradle build -x test

ENTRYPOINT ["java", "-jar", "home/ndex/server/build/libs/knowledge-beacon-server-1.0.12.jar"]
