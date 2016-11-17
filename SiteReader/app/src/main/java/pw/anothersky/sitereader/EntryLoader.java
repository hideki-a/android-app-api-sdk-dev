package pw.anothersky.sitereader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

import pw.anothersky.movabletype.apisdk.DataAPI;
import pw.anothersky.sitereader.data.Entries;

/**
 * Created by Hideki on 2016/11/17.
 */

public class EntryLoader extends AsyncTaskLoader<Entries> {
    private String apiURL;

    public EntryLoader(Context context, String apiURL) {
        super(context);
        this.apiURL = apiURL;
    }

    @Override
    public Entries loadInBackground() {
        DataAPI api = DataAPI.sharedInstance;
        api.APIBaseURL = this.apiURL;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("limit", "5");
        params.put("search", "アクセシビリティ");
        JSONObject json = api.listEntries(1, params);

        if (json == null) {
            return null;
        }

        Log.i("API Response", String.valueOf(json));

        return null;
    }
}
