FROM bellsoft/liberica-openjdk-debian:25

WORKDIR /srv
ENV LANG=ja_JP.UTF-8

RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates curl git && rm -rf /var/lib/apt/lists/*

COPY build/libs/combinationSpring-0.0.1-SNAPSHOT.jar /srv/app.jar
CMD ["java","-jar","/srv/app.jar"]