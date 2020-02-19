FROM openjdk:15-jdk-alpine

ENV LANG C.UTF-8
ENV GRASSLANG_VERSION w.W.v
ENV WORK_DIR /usr/local/grass

RUN apk update --no-cache

WORKDIR ${WORK_DIR}

ADD . ${WORK_DIR}

ENV PATH /bin/grass:${PATH}
