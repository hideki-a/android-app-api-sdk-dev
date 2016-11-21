package pw.anothersky.sitereader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import pw.anothersky.movabletype.apisdk.DataApi;
import pw.anothersky.sitereader.data.Entries;

/**
 * Created by Hideki on 2016/11/17.
 */

public class EntryLoader extends AsyncTaskLoader<Entries> {
    private static Context mContext;

    public EntryLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Entries loadInBackground() {
        DataApi api = DataApi.sharedInstance;
        api.apiBaseUrl = mContext.getString(R.string.development);

        // Test: 1記事の取得
        JSONObject json = api.getEntry(1, 838, null);
        Log.i("API Response", String.valueOf(json));

        // Test: Authentication
        HashMap<String, String> authParams = new HashMap<String, String>();
        authParams.put("username", mContext.getString(R.string.development_mt_username));
        authParams.put("password", mContext.getString(R.string.development_mt_password));
        authParams.put("remember", "1");
        api.authentication(authParams);

        // Test: 1記事の作成
//        HashMap<String, String> createParams = new HashMap<String, String>();
//        createParams.put("title", "JavaSDKからの投稿テスト");
//        createParams.put("body", "JavaSDKからの投稿テストです。ただ今SDKを開発中です。");
//        createParams.put("status", "Publish");
//        JSONObject createEntry = api.createEntry(3, createParams);
//        Log.i("API Response", String.valueOf(createEntry));

        // Test: 1記事の編集
//        HashMap<String, String> createParams = new HashMap<String, String>();
//        createParams.put("status", "Draft");
//        JSONObject createEntry = api.updateEntry(3, 859, createParams);
//        Log.i("API Response", String.valueOf(createEntry));

        // Test: 1記事の削除
//        JSONObject createEntry = api.deleteEntry(3, 859);
//        Log.i("API Response", String.valueOf(createEntry));

        return null;
    }
}
