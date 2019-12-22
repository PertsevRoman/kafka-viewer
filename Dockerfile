FROM openjdk:8

WORKDIR /app

# collect artifacts
COPY frontend/dist/* /app/front
COPY backend/target/kv-backend.jar /app/backend/kv-backend.jar

# verify FE files
WORKDIR /app/front
RUN ls

# verify BE files
WORKDIR /app/backend
RUN ls

WORKDIR /app

# envs
ENV KAFKA_HOST "localhost:9092"
ENV STATIC_LOCATION "/app/front"
ENV S3_REPO_CRON "0 * * * * *"
ENV AWS_ACCESS_KEY ""
ENV AWS_SECRET_KEY ""
ENV AWS_REGION ""
ENV AWS_BUCKET_NAME ""
ENV SPRING_PROFILES_ACTIVE "spa,s3"

# port expose
EXPOSE 8083

CMD [ "java", "-jar", "/app/backend/kv-backend.jar" ]
