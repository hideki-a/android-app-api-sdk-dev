package pw.anothersky.movabletype.apisdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

    private String sendRequest(String method, String url) {
        OkHttpClient client = new OkHttpClient();

        switch (method) {
            case "GET":
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return null;
    }

    private JSONObject fetchList(String url) {
        String responseBody = sendRequest("GET", url);

        if (responseBody != null) {
            try {
                return new JSONObject(responseBody);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
