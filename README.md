# ラズパイと学習リモコンを使ったIoTリモコンアプリ

## はじめに

このプログラムを動かすにはMQTTブローカーと、部屋に設置するための装置が必要です。

- [MQTTブローカー MosquittoをUbuntu 18.04にインストール](https://qiita.com/Yosh0124/items/b94f114afe3d39fb285b)
- [ラズパイと学習リモコンを使ったIoTリモコン](https://github.com/Yosh0124/home-remocon-raspberrypi)

## インストール方法

まずはMQTTブローカーへ接続するための情報を設定します。
`app/sample.keystore.properties`のファイル名を`keystore.properties`に修正して下さい。
※保存するディレクトリは変更しないで下さい。

そして、ご自身で立てたMQTTブローカーの設定に合わせて修正してください。

基本的にはこれだけの設定で動くはずです。