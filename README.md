# SiteReader (for Android)

研究・開発中のアプリです。

## サーバーの定義

### 開発用

`SiteReader/app/src/main/res/values/server.xml`にサーバーを定義してください。以下サンプルです。

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="development">https://192.168.1.20/mt/mt-data-api.cgi</string>
    <string name="development_mt_username">your-mt-username</string>
    <string name="development_mt_password">your-mt-password</string>
</resources>
```

### Unit Test用

`SiteReader/app/src/test/resources/server.txt`にサーバーを定義してください。以下サンプルです。

```
https://192.168.1.20/mt/mt-data-api.cgi
your-mt-username
your-mt-password
```
