#build frontend
FROM node:10 as front_build

WORKDIR /src

ADD kv-frontend /src/kv-frontend

WORKDIR /src/kv-frontend

RUN npm i
RUN npm run build:prod

# build backend
FROM openjdk:8 as backend_build

WORKDIR /src

ADD kv-backend /src/kv-backend

WORKDIR /src/kv-backend

RUN ./mvnw clean install -DskipTests=true

# composite to single
FROM openjdk:8

WORKDIR /app

# collect artifacts
COPY --from=front_build /src/kv-frontend/dist/* /app/front
COPY --from=backend_build /src/kv-backend/target/kv-backend.jar /app/backend/kv-backend.jar

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
