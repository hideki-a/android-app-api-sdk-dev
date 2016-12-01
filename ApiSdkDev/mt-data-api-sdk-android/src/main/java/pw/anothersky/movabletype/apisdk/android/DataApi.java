/*
 * MIT License
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

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Movable Type DataAPIにアクセスするためのクラスです。
 */
public class DataApi {
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
    /** mt-data-api.cgiまでのURL （例）http://localhost/cgi-bin/mt/mt-data-api.cgi */
    public String apiBaseUrl = "http://localhost/cgi-bin/mt/mt-data-api.cgi";
    /** DataAPI利用時のクライアントID */
    public String clientId = "MTDataAPIJavaClient";

    /** DataAPIのインスタンス */
    public static final DataApi sharedInstance = new DataApi();

    public interface Callback {
        /**
         * リクエスト成功時に実行するメソッド
         *
         * @param json APIからのレスポンス
         */
        public void onResponse(JSONObject json);
    }

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
                break;

            case POST:
                buildedRequest = request.url(url).post(formBody).build();
                break;

            case PUT:
                buildedRequest = request.url(url).put(formBody).build();
                break;

            case DELETE:
                buildedRequest = request.url(url).delete().build();
                break;
        }

        try {
            Response response = client.newCall(buildedRequest).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void sendRequestWithCb(HttpMethod method, String url, RequestBody formBody, boolean useSession, final Callback callback) {
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
                break;

            case POST:
                buildedRequest = request.url(url).post(formBody).build();
                break;

            case PUT:
                buildedRequest = request.url(url).put(formBody).build();
                break;

            case DELETE:
                buildedRequest = request.url(url).delete().build();
                break;
        }

        client.newCall(buildedRequest).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    callback.onResponse(new JSONObject(responseBody));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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

    private void authenticationCommon(String url, HashMap<String, String> params, final Callback callback) {
        RequestBody formBody = this.parsePostParams(params);

        if (callback != null) {
            sendRequestWithCb(HttpMethod.POST, url, formBody, false, new Callback() {
                @Override
                public void onResponse(JSONObject json) {
                    try {
                        if (json.has("accessToken")) {
                            token = json.getString("accessToken");
                            sessionId = json.getString("sessionId");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i("DataApi Debug", String.valueOf(json));
                    callback.onResponse(json);
                }
            });
        } else {
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
    }

    /**
     * 新しいセッションを確立し、アクセストークンを取得します。
     *
     * @param params 認証情報とclientId
     * @param callback リクエスト成功時に実行するメソッド
     */
    public void authentication(HashMap<String, String> params, final Callback callback) {
        String url = this.apiUrl() + "/authentication";
        this.authenticationCommon(url, params, callback);
    }

    /**
     * エンドポイントバージョン2で新しいセッションを確立し、アクセストークンを取得します。
     *
     * @param params 認証情報とclientId
     * @param callback リクエスト成功時に実行するメソッド
     */
    public void authenticationV2(HashMap<String, String> params, final Callback callback) {
        String url = this.apiurlV2() + "/authentication";
        this.authenticationCommon(url, params, callback);
    }

    private JSONObject fetchList(String url, final Callback callback) {
        if (callback != null) {
            sendRequestWithCb(HttpMethod.GET, url, null, false, callback);
            return null;
        } else {
            String responseBody = sendRequest(HttpMethod.GET, url, null, false);
            return buildJSON(responseBody);
        }
    }

    private JSONObject entryAction(HttpMethod method, String entryClass, int siteId, int entryId, HashMap<String, String> params, final Callback callback) {
        String url = this.apiUrl() + "/sites/" + siteId + "/";
        String paramStr = null;
        String json = null;
        HashMap<String, String> requestBody = new HashMap<String, String>();
        RequestBody formBody = null;
        String responseBody = null;

        switch (entryClass) {
            case "entry":
                url = url + "entries";
                break;
            case "page":
                url = url + "pages";
                break;
        }

        if (entryId != -1) {
            url = url + "/" + entryId;
        }

        if (HttpMethod.GET == method) {
            paramStr = this.parseParams(params);
            url = url + paramStr;
        } else {
            if (params != null) {
                try {
                    json = this.convertJSON(params);
                    requestBody.put(entryClass, json);
                    formBody = this.parsePostParams(requestBody);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }

        if (callback != null) {
            if (HttpMethod.GET == method) {
                sendRequestWithCb(method, url, null, false, callback);
            } else {
                if (params != null) {                     
                    sendRequestWithCb(method, url, formBody, false, callback);
                } else {
                    sendRequestWithCb(method, url, null, false, callback);
                }
            }

            return null;
        } else {
            if (HttpMethod.GET == method) {
                responseBody = sendRequest(method, url, null, false);
            } else {
                if (params != null) {
                    responseBody = sendRequest(method, url, formBody, false);
                } else {
                    responseBody = sendRequest(method, url, null, false);
                }
            }

            return buildJSON(responseBody);
        }
    }

    /**
     * サイトの一覧を取得します。
     *
     * @param callback リクエスト成功時に実行するメソッド
     * @return JSONObject APIからのレスポンス
     */
    public JSONObject listSites(final Callback callback) {
        String url = this.apiUrl() + "/sites";
        return this.fetchList(url, callback);
    }

    /**
     * ブログ記事の一覧を取得します。
     *
     * @param siteId ブログID
     * @param params 抽出条件
     * @param callback リクエスト成功時に実行するメソッド
     * @return JSONObject APIからのレスポンス
     */
    public JSONObject listEntries(int siteId, HashMap<String, String> params, final Callback callback) {
        String paramStr = this.parseParams(params);
        String url = this.apiUrl() + "/sites/" + siteId + "/entries" + paramStr;
        return this.fetchList(url, callback);
    }

    /**
     * 指定したIDの記事を取得します。
     *
     * @param siteId ブログID
     * @param entryId 記事ID
     * @param params 取得内容の設定（フィールド設定）
     * @param callback リクエスト成功時に実行するメソッド
     * @return JSONObject APIからのレスポンス
     */
    public JSONObject getEntry(int siteId, int entryId, HashMap<String, String> params, final Callback callback) {
        return this.entryAction(HttpMethod.GET, "entry", siteId, entryId, params, callback);
    }

    /**
     * 新規記事を作成します。
     *
     * @param siteId ブログID
     * @param params 記事データ
     * @param callback リクエスト成功時に実行するメソッド
     * @return JSONObject APIからのレスポンス
     */
    public JSONObject createEntry(int siteId, HashMap<String, String> params, final Callback callback) {
        return this.entryAction(HttpMethod.POST, "entry", siteId, -1, params, callback);
    }

    /**
     * 指定したIDの記事を編集します。
     *
     * @param siteId ブログID
     * @param entryId 記事ID
     * @param params 記事データ
     * @param callback リクエスト成功時に実行するメソッド
     * @return JSONObject APIからのレスポンス
     */
    public JSONObject updateEntry(int siteId, int entryId, HashMap<String, String> params, final Callback callback) {
        return this.entryAction(HttpMethod.PUT, "entry", siteId, entryId, params, callback);
    }

    /**
     * 指定したIDの記事を削除します。
     *
     * @param siteId ブログID
     * @param entryId 記事ID
     * @param callback リクエスト成功時に実行するメソッド
     * @return JSONObject APIからのレスポンス
     */
    public JSONObject deleteEntry(int siteId, int entryId, final Callback callback) {
        return this.entryAction(HttpMethod.DELETE, "entry", siteId, entryId, null, callback);
    }

    /**
     * ウェブページの一覧を取得します。
     *
     * @param siteId ブログID
     * @param params 抽出条件
     * @param callback リクエスト成功時に実行するメソッド
     * @return JSONObject APIからのレスポンス
     */
    public JSONObject listPages(int siteId, HashMap<String, String> params, final Callback callback) {
        String paramStr = this.parseParams(params);
        String url = this.apiUrl() + "/sites/" + siteId + "/pages" + paramStr;
        return this.fetchList(url, callback);
    }

    /**
     * 指定したIDのウェブページを取得します。
     *
     * @param siteId ブログID
     * @param pageId ウェブページID
     * @param params 取得内容の設定（フィールド設定）
     * @param callback リクエスト成功時に実行するメソッド
     * @return JSONObject APIからのレスポンス
     */
    public JSONObject getPage(int siteId, int pageId, HashMap<String, String> params, final Callback callback) {
        return this.entryAction(HttpMethod.GET, "page", siteId, pageId, params, callback);
    }

    /**
     * 新規ウェブページを作成します。
     *
     * @param siteId ブログID
     * @param params ウェブページデータ
     * @param callback リクエスト成功時に実行するメソッド
     * @return JSONObject APIからのレスポンス
     */
    public JSONObject createPage(int siteId, HashMap<String, String> params, final Callback callback) {
        return this.entryAction(HttpMethod.POST, "page", siteId, -1, params, callback);
    }

    /**
     * 指定したIDのウェブページを編集します。
     *
     * @param siteId ブログID
     * @param pageId ウェブページID
     * @param params 記事データ
     * @param callback リクエスト成功時に実行するメソッド
     * @return JSONObject APIからのレスポンス
     */
    public JSONObject updatePage(int siteId, int pageId, HashMap<String, String> params, final Callback callback) {
        return this.entryAction(HttpMethod.PUT, "page", siteId, pageId, params, callback);
    }

    /**
     * 指定したIDのウェブページを削除します。
     *
     * @param siteId ブログID
     * @param pageId ウェブページID
     * @param callback リクエスト成功時に実行するメソッド
     * @return JSONObject APIからのレスポンス
     */
    public JSONObject deletePage(int siteId, int pageId, final Callback callback) {
        return this.entryAction(HttpMethod.DELETE, "page", siteId, pageId, null, callback);
    }

    /**
     * カテゴリの一覧を取得します。
     *
     * @param siteId ブログID
     * @param params 抽出条件
     * @param callback リクエスト成功時に実行するメソッド
     * @return JSONObject APIからのレスポンス
     */
    public JSONObject listCategories(int siteId, HashMap<String, String> params, final Callback callback) {
        String paramStr = this.parseParams(params);
        String url = this.apiUrl() + "/sites/" + siteId + "/categories" + paramStr;
        return this.fetchList(url, callback);
    }

    private JSONObject listEntriesForObject(String objectName, int objectId, String entryClass, int siteId, String paramStr, final Callback callback) {
        String url = this.apiUrl() + "/sites/" + siteId + "/"
                + objectName + "/" + objectId + "/";

        switch (entryClass) {
            case "entry":
                url = url + "entries";
                break;
            case "page":
                url = url + "pages";
                break;
        }

        url = url + paramStr;

        if (callback != null) {
            sendRequestWithCb(HttpMethod.GET, url, null, false, callback);
            return null;
        } else {
            String responseBody = sendRequest(HttpMethod.GET, url, null, false);
            return buildJSON(responseBody);
        }
    }

    /**
     * 指定カテゴリに属する記事を取得します。
     *
     * @param siteId ブログID
     * @param categoryId カテゴリID
     * @param params 抽出条件
     * @param callback リクエスト成功時に実行するメソッド
     * @return JSONObject APIからのレスポンス
     */
    public JSONObject listEntriesForCategory(int siteId, int categoryId, HashMap<String, String> params, final Callback callback) {
        String paramStr = this.parseParams(params);
        return this.listEntriesForObject("categories", categoryId, "entry", siteId, paramStr, callback);
    }

    /**
     * 指定フォルダに属する記事を取得します。
     *
     * @param siteId ブログID
     * @param folderId フォルダID
     * @param params 抽出条件
     * @param callback リクエスト成功時に実行するメソッド
     * @return JSONObject APIからのレスポンス
     */
    public JSONObject listPagesForFolder(int siteId, int folderId, HashMap<String, String> params, final Callback callback) {
        String paramStr = this.parseParams(params);
        return this.listEntriesForObject("folders", folderId, "page", siteId, paramStr, callback);
    }

    /**
     * 検索結果を取得します。
     *
     * @param params 検索条件
     * @param callback リクエスト成功時に実行するメソッド
     * @return JSONObject APIからのレスポンス
     */
    public JSONObject search(HashMap<String, String> params, final Callback callback) {
        String paramStr = this.parseParams(params);
        String url = this.apiUrl() + "/search" + paramStr;
        return this.fetchList(url, callback);
    }
}
