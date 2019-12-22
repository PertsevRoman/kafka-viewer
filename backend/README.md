## Kafka Viewer backend

[![Build Status](https://travis-ci.com/PertsevRoman/kv-backend.svg?branch=develop)](https://travis-ci.com/PertsevRoman/kv-backend)

### Compile and run

1. Clone the project
2. Deploy Apache Kafka cluster from ``/stack`` folder
    1. Don't forget to disable ``intellibump`` service (if you want) 
3. ``mvn clean install -DskipTests=true``; or you could run app or test from IntelliJ run config
4. target ``intellidump.jar`` file placed in ``/target`` directory

### Command line parameters
#### JVM parameters
``-Dspring.profiles.active=<profile set>`` &mdash; set enabled profiles list

##### Available profiles
* ``spa`` &mdash; enable spa hosting; serve spa app from ``STATIC_LOCATION`` directory;
* ``s3`` &mdash; enable s3 AVRO schemas repository.

#### Environment variables  
* ``STATIC_LOCATION`` &mdash; static files location directory (``spa`` profile only);
* ``KAFKA_HOST`` &mdash; Kafka broker endpoint; you can add multiple clusters with `;` delimiter

Also you have to set AWS environment variables to access AVRO repository:

* ``SERVER_PORT`` &mdash; server port, default is 8083
* ``AWS_ACCESS_KEY`` &mdash; AWS access key
* ``AWS_SECRET_KEY`` &mdash; AWS secret key
* ``AWS_REGION`` &mdash; AWS region
* ``AWS_BUCKET_NAME`` &mdash; AWS bucket name
* ``S3_REPO_CRON`` &mdash; schemes cache refresh cron template; default is `0 * * * * *` (every hour)
* ``POLL_TIMEOUT`` &mdash; basic poll timeout (msec); suitable for consumer.poll() calls, default is 10000 msec
