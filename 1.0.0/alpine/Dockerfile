FROM openjdk:15-jdk-alpine

ENV LANG C.UTF-8
ENV GRASSLANG_VERSION w.W.v
ENV GRASSLANG_PATH /usr/bin/grass
ENV GRASSLANG_DIR /usr/local/grass

WORKDIR ${GRASSLANG_DIR}

ADD grass ${GRASSLANG_PATH}
ADD *.java ${GRASSLANG_DIR}

RUN apk update --no-cache && \
    chmod 755 ${GRASSLANG_PATH}

ENV PATH ${PATH}:${GRASSLANG_PATH}
