#build frontend
FROM node:10 as front_build

WORKDIR /src

ADD frontend /src/frontend

WORKDIR /src/frontend

RUN npm ci
RUN npm run build:prod

# build backend
FROM openjdk:8 as backend_build

WORKDIR /src

ADD backend /src/backend

WORKDIR /src/backend

RUN ./mvnw clean install -DskipTests=true

# composite to single
FROM openjdk:8

WORKDIR /app

# collect artifacts
COPY --from=front_build /src/frontend/dist/* /app/front
COPY --from=backend_build /src/backend/target/kv-backend.jar /app/backend/kv-backend.jar

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
