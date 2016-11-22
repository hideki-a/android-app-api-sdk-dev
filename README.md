# ApiSdkDev

Movable Type Data API SDK for Androidの研究・開発用のアプリです。

## サーバーの定義

### 開発用

`ApiSdkDev/app/src/main/res/values/server.xml`にサーバーを定義してください。以下サンプルです。

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="development">https://192.168.1.20/mt/mt-data-api.cgi</string>
    <string name="development_mt_username">your-mt-username</string>
    <string name="development_mt_password">your-mt-password</string>
</resources>
```

### Unit Test用

`ApiSdkDev/mt-data-api-sdk-android/src/test/resourcesserver.txt`にサーバーを定義してください。以下サンプルです。

```
https://192.168.1.20/mt/mt-data-api.cgi
your-mt-username
your-mt-password
```
