FROM bellsoft/liberica-openjdk-debian:25

WORKDIR /srv
ENV LANG=ja_JP.UTF-8

RUN apk add --no-cache bash curl git

CMD ["java", "-jar", "/srv/app.jar"]