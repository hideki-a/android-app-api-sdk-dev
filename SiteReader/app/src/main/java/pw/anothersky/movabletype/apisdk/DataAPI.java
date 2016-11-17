package pw.anothersky.movabletype.apisdk;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hideki Abe on 2016/11/17.
 */

public class DataAPI extends JSONObject {
    private static final int CONNECT_TIMEOUT_MS = 3000;
    private static final int READ_TIMEOUT_MS = 5000;

    public String endpointVersion = "v3";
    public String APIBaseURL = "http://localhost/cgi-bin/MT-6.1/mt-data-api.cgi";

    public static final DataAPI sharedInstance = new DataAPI();

    private String APIURL() {
        return APIBaseURL + "/" + endpointVersion;
    }

    private String parseParams(HashMap<String, String> params) {
        String paramStr = "";

        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (paramStr.length() > 0) {
                    paramStr = paramStr + "&" + param.getKey() + "=" + param.getValue();
                } else {
                    paramStr = param.getKey() + "=" + param.getValue();
                }
            }

            return paramStr;
        }

        return "";
    }

    private HttpURLConnection makeRequest(String method, String urlString) {
        // todo: okHttp3(http://square.github.io/okhttp/)を使うようにしたい
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setInstanceFollowRedirects(true);
            connection.connect();

            return connection;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private JSONObject fetchList(String url) {
        HttpURLConnection connection = this.makeRequest("GET", url);

        if (connection == null) {
            return null;
        }

        try {
            Log.i("Test", String.valueOf(connection.getResponseCode()));
            if (connection.getResponseCode() == 200) {
                // 参考: http://stackoverflow.com/questions/6511880/how-to-parse-a-json-input-stream#answer-13267720
                InputStream in = new BufferedInputStream(connection.getInputStream());
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStringBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null) {
                    responseStringBuilder.append(inputStr);
                }

                try {
                    return new JSONObject(responseStringBuilder.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public JSONObject listSites() {
        String url = APIURL() + "/sites";
        return this.fetchList(url);
    }

    public JSONObject listEntries(int siteId) {
        String url = APIURL() + "/sites/" + siteId + "/entries";
        return this.fetchList(url);
    }

    public JSONObject listEntries(int siteId, HashMap<String, String> params) {
        String paramStr = this.parseParams(params);
        String url = APIURL() + "/sites/" + siteId + "/entries?" + paramStr;
        return this.fetchList(url);
    }
}
