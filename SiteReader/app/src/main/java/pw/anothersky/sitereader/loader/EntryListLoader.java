package pw.anothersky.sitereader.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pw.anothersky.sitereader.R;
import pw.anothersky.sitereader.data.Entry;
import pw.anothersky.movabletype.apisdk.DataApi;

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
        JSONObject json = api.listEntries(1, null);

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
