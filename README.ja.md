# grasslang

[Click here for English version.](https://github.com/Q-Brains/grasslang/blob/master/README.md)

## 概要

かの有名なプログラミング言語である grasslang を dockerコンテナ として提供します。

## タグ

- [`1.0-slim`, `1-slim`, `slim`, `latest`](https://github.com/Q-Brains/grasslang/blob/master/1.0.0/slim/Dockerfile)
- [`1.0-alpine`, `1-alpine`, `alpine`](https://github.com/Q-Brains/grasslang/blob/master/1.0.0/alpine/Dockerfile)

## 使い方

### 起動

```command-line
docker run -it qbrains/grasslang:latest bash
```

### `grass` コマンド

`grass` コマンドは Grassファイル を実行するためのコマンドです。  
Grassファイル は拡張子が `.grass` または `.www` でなければいけません。  
以下はコンテナ内で利用できる `grass` コマンドの使用例です。  

```command-line
grass index.grass
```

```command-line
grass hoge.www
```

## grasslangとは

[ちょっと草植えときますね型言語 Grass](http://www.blue.sky.or.jp/grass/doc_ja.html) を参照。  
