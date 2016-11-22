package pw.anothersky.apisdkdev.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pw.anothersky.movabletype.apisdk.android.DataApi;
import pw.anothersky.sitereader.R;
import pw.anothersky.apisdkdev.data.Entry;


/**
 * Created by Hideki on 2016/11/21.
 */

public class EntryListLoader extends AsyncTaskLoader<List<Entry>> {
    private static Context mContext;

    public EntryListLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public List<Entry> loadInBackground() {
        List<Entry> entries = new ArrayList<>();

        DataApi api = DataApi.sharedInstance;
        api.apiBaseUrl = mContext.getString(R.string.development);

        // -- Test --------------------------------------
        // Test: Authentication
//        HashMap<String, String> authParams = new HashMap<String, String>();
//        authParams.put("username", mContext.getString(R.string.development_mt_username));
//        authParams.put("password", mContext.getString(R.string.development_mt_password));
//        authParams.put("remember", "1");
//        api.authentication(authParams, null);

        // Test: 1記事の作成
//        HashMap<String, String> createParams = new HashMap<String, String>();
//        createParams.put("title", "JavaSDKからの投稿テスト2");
//        createParams.put("body", "JavaSDKからの投稿テストです。ただ今SDKを開発中です。\nEnumを利用するようにしました。");
//        createParams.put("status", "Publish");
//        JSONObject createEntry = api.createEntry(3, createParams, null);
//        Log.i("API Response", String.valueOf(createEntry));

        // Test: 1記事の編集
//        HashMap<String, String> createParams = new HashMap<String, String>();
//        createParams.put("status", "Draft");
//        JSONObject createEntry = api.updateEntry(3, 859, createParams);
//        Log.i("API Response", String.valueOf(createEntry));

        // Test: 1記事の削除
//        JSONObject createEntry = api.deleteEntry(3, 859);
//        Log.i("API Response", String.valueOf(createEntry));
        // ----------------------------------------------

        JSONObject json = api.listEntries(1, null, null);

        try {
            JSONArray items = json.getJSONArray("items");
            int nItems = items.length();
            JSONObject[] entriesObject = new JSONObject[nItems];

            for (int i = 0; i < nItems; i += 1) {
                entriesObject[i] = items.getJSONObject(i);
            }

            for (int i = 0; i < nItems; i += 1) {
                Entry entry = new Entry();

                entry.setTitle(entriesObject[i].getString("title"));
                entry.setDate(entriesObject[i].getString("date"));

                entries.add(entry);
            }

            return entries;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
