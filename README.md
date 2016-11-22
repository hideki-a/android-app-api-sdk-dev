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

## サンプルコード例

### AsyncTaskなどで使用する場合

```
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

```
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
