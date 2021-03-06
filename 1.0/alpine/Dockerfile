FROM openjdk:15-jdk-alpine

ENV LANG C.UTF-8
ENV GRASSLANG_VERSION 1.0.0
ENV GRASSLANG_PATH /usr/bin/grass
ENV GRASSLANG_DIR /usr/local/grass

WORKDIR ${GRASSLANG_DIR}

RUN apk add --update wget && \
    wget -P ${GRASSLANG_DIR} https://raw.githubusercontent.com/Q-Brains/grasslang/master/1.0/Grass.java && \
    apk del --purge wget && \
    touch ${GRASSLANG_PATH} && \
    { \
    echo '#!/bin/sh'; \
    echo ''; \
    echo 'PROGRAMNAME=$(basename $0)'; \
    echo 'HELP_MSG="You can get help by specifying '"'"'$PROGRAMNAME -h'"'"'"'; \
    echo ''; \
    echo 'usage() {'; \
    echo '  echo "Usage: grass [-opt] file"'; \
    echo '  echo '; \
    echo '  echo "Options:"'; \
    echo '  echo "  -h, --help"'; \
    echo '  echo "  -v, --version"'; \
    echo '  echo '; \
    echo '  exit 1'; \
    echo '}'; \
    echo ''; \
    echo 'run() {'; \
    echo '  javac -encoding UTF-8 "$GRASSLANG_DIR/Grass.java"'; \
    echo '  java -classpath "$GRASSLANG_DIR" Grass $1'; \
    echo '}'; \
    echo ''; \
    echo 'param='; \
    echo ''; \
    echo 'for OPT in "$@"'; \
    echo 'do'; \
    echo '  case "$OPT" in'; \
    echo '    '"'"'-h'"'"'|'"'"'--help'"'"' )'; \
    echo '      usage'; \
    echo '      exit 1'; \
    echo '      ;;'; \
    echo '    '"'"'-v'"'"'|'"'"'--version'"'"' )'; \
    echo '      echo "grass version $GRASSLANG_VERSION"'; \
    echo '      exit 1'; \
    echo '      ;;'; \
    echo '    '"'"'--'"'"'|'"'"'-'"'"' )'; \
    echo '      shift 1'; \
    echo '      usage'; \
    echo '      break'; \
    echo '      ;;'; \
    echo '    -* )'; \
    echo '      echo "$PROGRAMNAME: '"'"'$(echo $1 | sed '"'"'s/^-*//'"'"')'"'"' option does not exist. Please check with '"'"'$PROGRAMNAME -h'"'"'." 1>&2'; \
    echo '      exit 1'; \
    echo '      ;;'; \
    echo '    * )'; \
    echo '      if [ ! -z "$1" ]; then'; \
    echo '        param="$1"'; \
    echo '        shift 1'; \
    echo '      fi'; \
    echo '      ;;'; \
    echo '  esac'; \
    echo 'done'; \
    echo ''; \
    echo 'if [ -z ${param} ]; then'; \
    echo '  echo "There is no file to execute."'; \
    echo '  exit 1'; \
    echo 'fi'; \
    echo ''; \
    echo 'if [ ! ${param##*.} = grass ] && [ ! ${param##*.} = www ]; then'; \
    echo '  echo "Specify a Grass file as the file to be executed."'; \
    echo '  exit 1'; \
    echo 'fi'; \
    echo ''; \
    echo 'run $param'; \
    } \
    > ${GRASSLANG_PATH} && \
    chmod 755 ${GRASSLANG_PATH}

ENV PATH ${PATH}:${GRASSLANG_PATH}
