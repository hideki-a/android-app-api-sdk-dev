/*
 * MIT License
 * http://choosealicense.com/licenses/mit/
 *
 * Copyright (c) 2016 Hideki Abe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package pw.anothersky.movabletype.apisdk.android;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
 * Movable Type DataAPIにアクセスするためのクラスです。
 */
public class DataApi extends JSONObject {
    private enum HttpMethod {
        GET,
        POST,
        PUT,
        DELETE,
        HEAD,
        OPTIONS
    }

    /** DataAPIエンドポイントのバージョン */
    public String endpointVersion = "v3";
    /** DataAPIエンドポイントのURL */
    public String apiBaseUrl = "http://localhost/cgi-bin/MT-6.1/mt-data-api.cgi";
    /** DataAPI利用時のクライアントID */
    public String clientId = "MTDataAPIJavaClient";

    public static final DataApi sharedInstance = new DataApi();

    private String token = "";
    private String sessionId = "";

    private String apiUrl() {
        return apiBaseUrl + "/" + endpointVersion;
    }

    private String apiurlV2() {
        return apiBaseUrl + "/v2";
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

            formBody.add("clientId", this.clientId);

            return formBody.build();
        }

        return null;
    }

    private String sendRequest(HttpMethod method, String url, RequestBody formBody, boolean useSession) {
        OkHttpClient client = new OkHttpClient();
        Request.Builder request = new Request.Builder();
        Request buildedRequest = null;

        if (this.token != "") {
            request.addHeader("X-MT-Authorization", "MTAuth accessToken=" + this.token);
        }

        if (useSession) {
            if (this.sessionId != "") {
                request.addHeader("X-MT-Authorization", "MTAuth sessionId" + this.sessionId);
            }
        }

        switch (method) {
            case GET:
                buildedRequest = request.url(url).build();

                try {
                    Response response = client.newCall(buildedRequest).execute();
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case POST:
                buildedRequest = request.url(url).post(formBody).build();

                try {
                    Response response = client.newCall(buildedRequest).execute();
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case PUT:
                buildedRequest = request.url(url).put(formBody).build();

                try {
                    Response response = client.newCall(buildedRequest).execute();
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case DELETE:
                buildedRequest = request.url(url).delete().build();

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

    private String convertJSON(HashMap<String, String> params) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(params);
    }

    private void authenticationCommon(String url, HashMap<String, String> params) {
        RequestBody formBody = this.parsePostParams(params);
        String responseBody = sendRequest(HttpMethod.POST, url, formBody, false);
        JSONObject json = buildJSON(responseBody);

        try {
            if (json.has("accessToken")) {
                this.token = json.getString("accessToken");
                this.sessionId = json.getString("sessionId");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("DataApi Debug", String.valueOf(json));
    }

    /** 新しいセッションを確立し、アクセストークンを取得します。 */
    public void authentication(HashMap<String, String> params) {
        String url = this.apiUrl() + "/authentication";
        this.authenticationCommon(url, params);
    }

    /** エンドポイントバージョン2で新しいセッションを確立し、アクセストークンを取得します。 */
    public void authenticationV2(HashMap<String, String> params) {
        String url = this.apiurlV2() + "/authentication";
        this.authenticationCommon(url, params);
    }

    private JSONObject fetchList(String url) {
        String responseBody = sendRequest(HttpMethod.GET, url, null, false);
        return buildJSON(responseBody);
    }

    private JSONObject entryAction(HttpMethod method, int siteId, int entryId, HashMap<String, String> params) {
        String url = this.apiUrl() + "/sites/" + siteId + "/entries";
        String responseBody = null;

        if (entryId != -1) {
            url = url + "/" + entryId;
        }

        if (HttpMethod.GET == method) {
            String paramStr = this.parseParams(params);
            url = url + paramStr;
            responseBody = sendRequest(method, url, null, false);
        } else {
            String json = null;

            if (params != null) {
                try {
                    json = this.convertJSON(params);
                    HashMap<String, String> requestBody = new HashMap<String, String>();
                    requestBody.put("entry", json);

                    RequestBody formBody = this.parsePostParams(requestBody);
                    responseBody = sendRequest(method, url, formBody, false);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                responseBody = sendRequest(method, url, null, false);
            }
        }

        return buildJSON(responseBody);
    }

    /** サイトの一覧を取得します。 */
    public JSONObject listSites() {
        String url = this.apiUrl() + "/sites";
        return this.fetchList(url);
    }

    /** ブログ記事の一覧を取得します。 */
    public JSONObject listEntries(int siteId, HashMap<String, String> params) {
        String paramStr = this.parseParams(params);
        String url = this.apiUrl() + "/sites/" + siteId + "/entries" + paramStr;
        return this.fetchList(url);
    }

    /**
     * 指定したIDの記事を取得します。
     *
     * @param siteId ブログID
     * @param entryId 記事ID
     * @param params 取得内容の設定（フィールド設定）
     * @return JSONObject APIのResponseBody
     */
    public JSONObject getEntry(int siteId, int entryId, HashMap<String, String> params) {
        return this.entryAction(HttpMethod.GET, siteId, entryId, params);
    }

    /**
     * 新規記事を作成します。
     *
     * @param siteId ブログID
     * @param params 記事データ
     * @return JSONObject APIのResponseBody
     */
    public JSONObject createEntry(int siteId, HashMap<String, String> params) {
        return this.entryAction(HttpMethod.POST, siteId, -1, params);
    }

    /**
     * 指定したIDの記事を編集します。
     *
     * @param siteId ブログID
     * @param entryId 記事ID
     * @param params 記事データ
     * @return JSONObject APIのResponseBody
     */
    public JSONObject updateEntry(int siteId, int entryId, HashMap<String, String> params) {
        return this.entryAction(HttpMethod.PUT, siteId, entryId, params);
    }

    /**
     * 指定したIDの記事を削除します。
     *
     * @param siteId ブログID
     * @param entryId 記事ID
     * @return JSONObject APIのResponseBody
     */
    public JSONObject deleteEntry(int siteId, int entryId) {
        return this.entryAction(HttpMethod.DELETE, siteId, entryId, null);
    }

    /** カテゴリの一覧を取得します。 */
    public JSONObject listCategories(int siteId, HashMap<String, String> params) {
        String paramStr = this.parseParams(params);
        String url = this.apiUrl() + "/sites/" + siteId + "/categories" + paramStr;
        return this.fetchList(url);
    }

    private JSONObject listEntriesForObject(String objectName, int objectId, int siteId, String paramStr) {
        String url = this.apiUrl() + "/sites/" + siteId + "/"
                + objectName + "/" + objectId + "/entries" + paramStr;
        String responseBody = sendRequest(HttpMethod.GET, url, null, false);
        return buildJSON(responseBody);
    }

    /**
     * 指定カテゴリに属する記事を取得します。
     *
     * @param params ブログID
     * @param params カテゴリID
     * @param params 抽出条件
     * @return JSONObject APIのResponseBody
     */
    public JSONObject listEntriesForCategory(int siteId, int categoryId, HashMap<String, String> params) {
        String paramStr = this.parseParams(params);
        return this.listEntriesForObject("categories", categoryId, siteId, paramStr);
    }

    /**
     * 検索結果を取得します。
     *
     * @param params 検索条件
     * @return JSONObject APIのResponseBody
     */
    public JSONObject search(HashMap<String, String> params) {
        String paramStr = this.parseParams(params);
        String url = this.apiUrl() + "/search" + paramStr;
        return this.fetchList(url);
    }
}
