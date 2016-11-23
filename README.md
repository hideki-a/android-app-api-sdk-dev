# ApiSdkDev

Movable Type Data API SDK for Android（仮称）の研究・開発用のアプリです。

## ソースコード

- [DataApi.java](/ApiSdkDev/mt-data-api-sdk-android/src/main/java/pw/anothersky/movabletype/apisdk/android/DataApi.java)

## サーバーの定義

### 開発用

`ApiSdkDev/app/src/main/res/values/server.xml`にサーバーを定義してください。以下サンプルです。

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="development">https://192.168.1.20/mt/mt-data-api.cgi</string>
    <string name="development_mt_username">your-mt-username</string>
    <string name="development_mt_password">your-mt-password</string>
</resources>
```

### Unit Test用

`ApiSdkDev/mt-data-api-sdk-android/src/test/resourcesserver.txt`にサーバーを定義してください。以下サンプルです。

```txt
https://192.168.1.20/mt/mt-data-api.cgi
your-mt-username
your-mt-password
```

## サンプルコード例

### AsyncTaskなどで使用する場合

```java
// Test: 記事の投稿
HashMap<String, String> authParams = new HashMap<String, String>();
authParams.put("username", mContext.getString(R.string.development_mt_username));
authParams.put("password", mContext.getString(R.string.development_mt_password));
authParams.put("remember", "1");
api.authentication(authParams, null);

HashMap<String, String> createParams = new HashMap<String, String>();
createParams.put("title", "DataAPI SDK for Androidからの投稿テスト");
createParams.put("body", "Movable Type Data API SDK for Androidからの投稿テストです。");
createParams.put("status", "Publish");
JSONObject createEntry = api.createEntry(3, createParams, null);
```

### それ以外の場合

MainActivityなどでは、以下の方法でDataAPIにアクセスが可能です。

```java
// Test: 記事の投稿
final DataApi api = DataApi.sharedInstance;
api.apiBaseUrl = getString(R.string.development);

HashMap<String, String> authParams = new HashMap<String, String>();
authParams.put("username", getString(R.string.development_mt_username));
authParams.put("password", getString(R.string.development_mt_password));
authParams.put("remember", "1");
DataApi.Callback authCb = new DataApi.Callback() {
    @Override
    public void onResponse(JSONObject json) {
        HashMap<String, String> createParams = new HashMap<String, String>();
        createParams.put("title", "コールバック内で記事を投稿するテスト");
        createParams.put("body", "Movable Type Data API SDK for Androidからの投稿テストです。コールバック内で投稿します。");
        createParams.put("status", "Publish");
        DataApi.Callback cb = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                Log.i("On MainActivity", String.valueOf(json));
            }
        };
        api.createEntry(3, createParams, cb);
    }
};
api.authentication(authParams, authCb);
```

## ドキュメント

- [クラス DataApi](https://hideki-a.github.io/android-app-api-sdk-dev/pw/anothersky/movabletype/apisdk/android/DataApi.html)
- [インタフェース DataApi.Callback](https://hideki-a.github.io/android-app-api-sdk-dev/pw/anothersky/movabletype/apisdk/android/DataApi.Callback.html)

## Movable Type Data API SDK for Android（仮称）の導入方法

ライブラリをお試し頂く場合の方法です。現在手動導入のみ可能です。手動導入の場合は依存するライブラリも自分で記述していただく必要があります。

1. `ApiSdkDev/mt-data-api-sdk-android/build/outputs/aar/mt-data-api-sdk-android-release.aar`をダウンロード
2. 導入したいプロジェクトの`app/libs`に`mt-data-api-sdk-android-release.aar`をコピー
3. `app/build.gradle`を編集しSync（サンプルを参照）

### `app/build.gradle`サンプル

```txt
apply plugin: 'com.android.application'

android {

    // 省略

    packagingOptions {
        exclude 'META-INF/LICENSE'
    }
}

dependencies {

    // 省略

    compile 'com.squareup.okhttp3:okhttp:3.4.2'
    compile 'com.fasterxml.jackson.core:jackson-databind:2.8.4'
    compile(name: 'mt-data-api-sdk-android-release', ext: 'aar')


    // 省略

}

repositories {
    flatDir {
        dirs 'libs'
    }
}
```
