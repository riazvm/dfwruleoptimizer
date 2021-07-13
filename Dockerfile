FROM openjdk:16-jdk-alpine

# Base image for your docker container

COPY ./target/dfwruleoptimizer-0.0.1-SNAPSHOT.jar /usr/app/

# Docker to copy files from the local file-system to a specific folder inside the build image.
# Here, we copy our .jar file to the build image (Linux image) inside /usr/app.

WORKDIR /usr/app
# WORKDIR instruction sets the working directory for any RUN, CMD, ENTRYPOINT, COPY and ADD
# instructions that follow in the Dockerfile. Here we switched the workdir to /usr/app so as
# we don't have to write the long path again and again.

RUN sh -c 'touch dfwruleoptimizer-0.0.1-SNAPSHOT.jar'
# RUN - This tells Docker to execute a shell command-line within the target system.
# Here we practically just "touch" our file so that it has its modification time updated
# (Docker creates all container files in an "unmodified" state by default).

ENTRYPOINT ["java" , "-jar" , "dfwruleoptimizer-0.0.1-SNAPSHOT.jar"]

#ENTRYPOINT - This allows you to configure a container that will run as an executable.
# It's where you tell Docker how to run your application.
# We know we run our spring-boot app as java -jar <app-name>.jar, so we put it in an array.