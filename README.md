# Kafka Viewer CI
Kafka Viewer CI repository

## Motivation
This tool was developed as simple and user-friendly Kafka topics viewer.
It does support AVRO schemes view and providing.
This tool is the best suitable for manual testing.
The tool not suitable for heavy workloads (like perfomance testing).

## Features
* Multiple Kafka clusters;
* AVRO schemes repository refresh on the fly;
* S3 schemes provider;
* Clusters/topics/partitions introspection;
* Live update.

## Architecture
Simple REST application based on Angular on frontend and Spring Boot on backend side.

## Usage

## Development

This chapter describes IDEA usage only and relies on IDEAM modules system.

1) clone the repo; the basic development branch is `develop`;
2) add `backend` directory as a Maven module;
3) add `frontend` module as Web module; Angular app should be detected automatically;
4) `Dockerfile` under the root folder take responsibility for full artifact build.

Please, read all [Backend README](https://github.com/PertsevRoman/kafka-viewer/blob/develop/backend/README.md)
and [Frontend README](https://github.com/PertsevRoman/kafka-viewer/blob/develop/frontend/README.md)
to family with backend and frontend development flows separately. However, the flows doesn't differs from
standard Spring Boot and Angular development flows. 

## A lot of thanks