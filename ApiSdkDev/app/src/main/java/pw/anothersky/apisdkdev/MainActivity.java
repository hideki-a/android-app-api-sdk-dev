package pw.anothersky.apisdkdev;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

import pw.anothersky.apisdkdev.data.Entry;
import pw.anothersky.movabletype.apisdk.android.DataApi;
import pw.anothersky.sitereader.R;

public class MainActivity extends AppCompatActivity implements EntryListFragment.EntryListFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Test: 記事の取得
        final DataApi api = DataApi.sharedInstance;
        api.apiBaseUrl = getString(R.string.development);

        DataApi.Callback cb = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                Log.i("On MainActivity", String.valueOf(json));
            }
        };
        api.listEntries(1, null, cb);

        // Test: 記事の投稿
//        HashMap<String, String> authParams = new HashMap<String, String>();
//        authParams.put("username", getString(R.string.development_mt_username));
//        authParams.put("password", getString(R.string.development_mt_password));
//        authParams.put("remember", "1");
//        DataApi.Callback authCb = new DataApi.Callback() {
//            @Override
//            public void onResponse(JSONObject json) {
//                HashMap<String, String> createParams = new HashMap<String, String>();
//                createParams.put("title", "コールバック内で記事を投稿するテスト");
//                createParams.put("body", "JavaSDKからの投稿テストです。ただ今SDKを開発中です。コールバック内で投稿します。");
//                createParams.put("status", "Publish");
//                DataApi.Callback cb = new DataApi.Callback() {
//                    @Override
//                    public void onResponse(JSONObject json) {
//                        Log.i("On MainActivity", String.valueOf(json));
//                    }
//                };
//                api.createEntry(3, createParams, cb);
//            }
//        };
//        api.authentication(authParams, authCb);

    }

    @Override
    public void onEntryClicked(@NonNull Entry entry) {
        // リンクがタップされたら、リンク先のページを開く
    }
}
