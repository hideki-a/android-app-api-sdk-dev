package pw.anothersky.movabletype.apisdk;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Hideki Abe on 2016/11/17.
 */

public class DataAPI extends JSONObject {
    public String endpointVersion = "v3";
    public String APIBaseURL = "http://localhost/cgi-bin/MT-6.1/mt-data-api.cgi";
    public String clientID = "MTDataAPIJavaClient";

    public static final DataAPI sharedInstance = new DataAPI();

    private String token = "";
    private String sessionID = "";

    private String APIURL() {
        return APIBaseURL + "/" + endpointVersion;
    }

    private String APIURL_v2() {
        return APIBaseURL + "/v2";
    }

    private String parseParams(HashMap<String, String> params) {
        String paramStr = "";

        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (paramStr.length() > 0) {
                    paramStr = paramStr + "&" + param.getKey() + "=" + param.getValue();
                } else {
                    paramStr = "?" + param.getKey() + "=" + param.getValue();
                }
            }

            return paramStr;
        }

        return "";
    }

    private RequestBody parsePostParams(HashMap<String, String> params) {
        FormBody.Builder formBody = new FormBody.Builder();

        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                formBody.add(param.getKey(), param.getValue());
            }

            formBody.add("clientId", this.clientID);

            return formBody.build();
        }

        return null;
    }

    private String sendRequest(String method, String url, RequestBody formBody, boolean useSession) {
        OkHttpClient client = new OkHttpClient();
        Request.Builder request = new Request.Builder();
        Request buildedRequest = null;

        if (this.token != "") {
            request.addHeader("X-MT-Authorization", "MTAuth accessToken=" + this.token);
        }

        if (useSession) {
            if (this.sessionID != "") {
                request.addHeader("X-MT-Authorization", "MTAuth sessionId" + this.sessionID);
            }
        }

        switch (method) {
            case "GET":
                buildedRequest = request.url(url).build();

                try {
                    Response response = client.newCall(buildedRequest).execute();
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case "POST":
                buildedRequest = request.url(url).post(formBody).build();

                try {
                    Response response = client.newCall(buildedRequest).execute();
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
        }

        return null;
    }

    private JSONObject buildJSON(String responseBody) {
        if (responseBody != null) {
            try {
                return new JSONObject(responseBody);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private void authenticationCommon(String url, HashMap<String, String> params) {
        RequestBody formBody = this.parsePostParams(params);
        String responseBody = sendRequest("POST", url, formBody, false);
        JSONObject json = buildJSON(responseBody);

        try {
            if (json.has("accessToken")) {
                this.token = json.getString("accessToken");
                this.sessionID = json.getString("sessionId");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("DataAPI Debug", String.valueOf(json));
    }

    public void authentication(HashMap<String, String> params) {
        String url = this.APIURL() + "/authentication";
        this.authenticationCommon(url, params);
    }

    public void authenticationV2(HashMap<String, String> params) {
        String url = this.APIURL_v2() + "/authentication";
        this.authenticationCommon(url, params);
    }

    private JSONObject fetchList(String url) {
        String responseBody = sendRequest("GET", url, null, false);
        return buildJSON(responseBody);
    }

    public JSONObject listSites() {
        String url = this.APIURL() + "/sites";
        return this.fetchList(url);
    }

    public JSONObject listEntries(int siteID, HashMap<String, String> params) {
        String paramStr = this.parseParams(params);
        String url = this.APIURL() + "/sites/" + siteID + "/entries" + paramStr;
        return this.fetchList(url);
    }

    public JSONObject listCategories(int siteID, HashMap<String, String> params) {
        String paramStr = this.parseParams(params);
        String url = this.APIURL() + "/sites/" + siteID + "/categories" + paramStr;
        return this.fetchList(url);
    }
}
