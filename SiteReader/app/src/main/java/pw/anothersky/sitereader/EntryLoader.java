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
    private String apiUrl;
    private String apiUsername;
    private String apiPassword;

    public EntryLoader(Context context, String apiUrl, String apiUsername, String apiPassword) {
        super(context);
        this.apiUrl = apiUrl;
        this.apiUsername = apiUsername;
        this.apiPassword = apiPassword;
    }

    @Override
    public Entries loadInBackground() {
        DataApi api = DataApi.sharedInstance;
        api.apiBaseUrl = this.apiUrl;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("limit", "5");
        params.put("search", "アクセシビリティ");
        JSONObject json = api.listEntries(1, params);

        if (json == null) {
            return null;
        }

        Log.i("API Response", String.valueOf(json));

        // Test: APIの内容を出力
        // http://d.hatena.ne.jp/androidprogram/20100622/1277229166
        try {
            JSONArray items = json.getJSONArray("items");

            int nItems = items.length();
            JSONObject[] entriesObject = new JSONObject[nItems];

            for (int i = 0; i < nItems; i += 1) {
                entriesObject[i] = items.getJSONObject(i);
            }

            for (int i = 0; i < nItems; i += 1) {
                String title = entriesObject[i].getString("title");
                Log.i("API Response", title);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Test: Authentication
        HashMap<String, String> authParams = new HashMap<String, String>();
        authParams.put("username", this.apiUsername);
        authParams.put("password", this.apiPassword);
        authParams.put("remember", "1");
        api.authentication(authParams);

        // Test: 認証を必要とするアクセスの試験
        HashMap<String, String> getDraftParams = new HashMap<String, String>();
        getDraftParams.put("status", "1");
        JSONObject draftEntries = api.listEntries(1, getDraftParams);
        Log.i("API Response", String.valueOf(draftEntries));

        return null;
    }
}
