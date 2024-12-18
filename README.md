# image-converter-job

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Project status](https://img.shields.io/badge/Project%20status-Maintenance-orange.svg)](https://img.shields.io/badge/Project%20status-Maintenance-orange.svg)

## Project status

I use this project to learn new technologies related to spring batch

1) Read file from "to process" folder
2) Put files on db
3) Move files to "processing" folder
4) Send msg to converter image service
5) record the result
6) move file to "finalized"
7) update status


Ribbon and Hystrix


# About

A project that converts image text into simple text using diverse technologies.  
I used [tesseract](https://github.com/tesseract-ocr/tesseract) for it and exposes it as a web service using spring boot, jakarta microprofile, quarkus, etc.

# Model
It's very simple application, just a controller and a service:
![Model](https://github.com/fernando-romulo-silva/image-converter/blob/master/doc/class-diagram.png)

# Technologies

- Git
- Java
- Maven
- Spring Boot
- Spring Security
- Spring MVC

# Modules

## image-converter-springboot

The idea here is to use spring boot with the smallest docker container using spring boot technologies, like layer and modularization.

### How to Execute

requirements: 
 - Java 16
 - Maven 3
 
$ docker build -f src/docker/Dockerfile -t image-service-converter-image .

```bash
# clone it
git clone https://github.com/fernando-romulo-silva/image-converter

# for spring boot
cd image-converter\image-converter-springboot

# execute
mvn spring-boot:run

or

docker image build -f src/main/docker/Dockerfile -t image-converter-service-iso .

docker run -p 5000:8080 -d --name mage-converter-service-1 image-converter-service-iso


```
