# grasslang

[日本語版はこちら](https://github.com/Q-Brains/grasslang/blob/master/README.ja.md)

Unnatural English writing? Because it is written by Japanese who is not good at English.

## Overview

Dockerize that very famous programming language, grasslang.

## Simple Tags

- [`1.0-slim`, `1-slim`, `slim`, `latest`](https://github.com/Q-Brains/grasslang/blob/master/1.0.0/slim/Dockerfile)
- [`1.0-alpine`, `1-alpine`, `alpine`](https://github.com/Q-Brains/grasslang/blob/master/1.0.0/alpine/Dockerfile)

## Usage

### Run

```command-line
docker run -it qbrains/gphotos-auth:latest bash
```

### `grass` command

The `grass` command is for execute Grass file.  
Grass files must have the extension `.grass` or `.www`.  
Here is an example of using the `grass` command available in the container.

```command-line
grass index.grass
```

```command-line
grass hoge.www
```

## What is grasslang?

See [here](http://www.blue.sky.or.jp/grass/).
