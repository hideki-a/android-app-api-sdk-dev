package pw.anothersky.movabletype.apisdk.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

/**
 * Local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DataApiUnitTest {
    private static final DataApi api = DataApi.sharedInstance;
    private static String userName;
    private static String password;
    private static String webservicePassword;
    private static String comment;

    private static int objectId = 0;

    private static HashMap makeAuthParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", userName);
        params.put("password", webservicePassword);
        params.put("remember", "true");

        return params;
    }

    @BeforeClass
    public static void setupAPIConnection() throws Exception {
        InputStream in = DataApiUnitTest.class.getClassLoader().getResourceAsStream("server.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String data[] = new String[4];
        String line;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            data[i] = line;
            i += 1;
        }

        DataApiUnitTest.api.apiBaseUrl = data[0];
        DataApiUnitTest.userName = data[1];
        DataApiUnitTest.password = data[2];
        DataApiUnitTest.webservicePassword = data[3];
    }

    @Test
    public void apiURLTest() throws Exception {
        assertNotEquals("http://localhost/cgi-bin/MT-6.1/mt-data-api.cgi", api.apiBaseUrl);
    }

    @Test
    public void listSites() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                try {
                    int totalResults = json.getInt("totalResults");
                    assertEquals(2, totalResults);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.listSites(callback);
        await().untilTrue(finished);
    }

    @Test
    public void syncRequest() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject json = api.listEntries(1, null, null);
                int totalResults = 0;
                try {
                    totalResults = json.getInt("totalResults");
                    assertEquals(117, totalResults);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        }).start();
        await().untilTrue(finished);
    }

    @Test
    public void listEntries() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int totalResults = 0;
                try {
                    totalResults = json.getInt("totalResults");
                    assertEquals(117, totalResults);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.listEntries(1, null, callback);
        await().untilTrue(finished);
    }

    @Test
    public void listEntriesForAsset() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int totalResults = 0;
                try {
                    totalResults = json.getInt("totalResults");
                    assertEquals(1, totalResults);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.listEntriesForAsset(1, 2, null, callback);
        await().untilTrue(finished);
    }

    @Test
    public void listEntriesForSiteAndTag() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int totalResults = 0;
                try {
                    totalResults = json.getInt("totalResults");
                    assertEquals(22, totalResults);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.listEntriesForSiteAndTag(1, 8, null, callback);
        await().untilTrue(finished);
    }

    @Test
    public void listCategories() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int totalResults = 0;
                try {
                    totalResults = json.getInt("totalResults");
                    assertEquals(8, totalResults);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.listCategories(1, null, callback);
        await().untilTrue(finished);
    }

    @Test
    public void listParentCategories() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                System.out.print(json);
                try {
                    JSONArray items = json.getJSONArray("items");
                    JSONObject firstItem = items.getJSONObject(0);
                    int id = firstItem.getInt("id");
                    assertEquals(3, id);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.listParentCategories(1, 8, null, callback);
        await().untilTrue(finished);
    }

    @Test
    public void listSiblingCategories() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int totalResults = 0;
                try {
                    totalResults = json.getInt("totalResults");
                    assertEquals(8, totalResults);    // API Bug?: class="folder"の要素も含まれる
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.listSiblingCategories(1, 5, null, callback);
        await().untilTrue(finished);
    }

    @Test
    public void listChildCategories() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int totalResults = 0;
                try {
                    totalResults = json.getInt("totalResults");
                    assertEquals(2, totalResults);    // API Bug?: class="folder"の要素も含まれる
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.listChildCategories(1, 5, null, callback);
        await().untilTrue(finished);
    }

    @Test
    public void listEntriesForCategory() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int totalResults = 0;
                try {
                    totalResults = json.getInt("totalResults");
                    assertEquals(87, totalResults);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.listEntriesForCategory(1, 3, null, callback);
        await().untilTrue(finished);
    }

    @Test
    public void listCategoriesForEntry() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int totalResults = 0;
                try {
                    totalResults = json.getInt("totalResults");
                    assertEquals(2, totalResults);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.listCategoriesForEntry(1, 120, null, callback);
        await().untilTrue(finished);
    }

    @Test
    public void searchEntries() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int totalResults = 0;
                try {
                    totalResults = json.getInt("totalResults");
                    assertEquals(15, totalResults);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        HashMap<String, String> params = new HashMap<>();
        params.put("search", "マークアップ");
        params.put("blog_id", "1");
        api.search(params, callback);
        await().untilTrue(finished);
    }

    @Test
    public void authentication() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                assertTrue(json.has("accessToken"));
                finished.set(true);
            }
        };
        HashMap<String, String> params = this.makeAuthParams();
        api.authentication(params, callback);
        await().untilTrue(finished);
    }

    private void makeEntry() {
        final AtomicBoolean finished = new AtomicBoolean(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> authParams = makeAuthParams();
                api.authentication(authParams, null);

                HashMap<String, String> params = new HashMap<>();
                params.put("title", "Testing Movable Type Data API SDK for Java");
                params.put("body", "DataAPIを利用した投稿の試験です。");
                params.put("status", "Publish");
                JSONObject json = api.createEntry(2, params, null);
                try {
                    objectId = json.getInt("id");
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        }).start();

        await().untilTrue(finished);
        assertTrue(objectId > 0);
    }

    private void getEntry() {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                try {
                    String title = json.getString("title");
                    assertEquals("Testing Movable Type Data API SDK for Java", title);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.getEntry(2, objectId, null, callback);
        await().untilTrue(finished);
    }

    private void updateEntry() {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                try {
                    String title = json.getString("title");
                    assertEquals("Testing Movable Type Data API SDK for Android", title);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        HashMap<String, String> params = new HashMap<>();
        params.put("title", "Testing Movable Type Data API SDK for Android");
        api.updateEntry(2, objectId, params, callback);
        await().untilTrue(finished);
    }

    private void deleteEntry() {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int deletePostId = 0;
                try {
                    deletePostId = json.getInt("id");
                    assertEquals(objectId, deletePostId);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.deleteEntry(2, objectId, callback);
        await().untilTrue(finished);
    }

    @Test
    public void operateEntry() {
        this.makeEntry();
        this.getEntry();
        this.updateEntry();
        this.deleteEntry();
        objectId = 0;
    }

    private void makeCategory() {
        final AtomicBoolean finished = new AtomicBoolean(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> authParams = makeAuthParams();
                api.authentication(authParams, null);

                HashMap<String, String> params = new HashMap<>();
                params.put("label", "旅客機");
                params.put("basename", "aviation");
                JSONObject json = api.createCategory(2, params, null);
                try {
                    objectId = json.getInt("id");
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        }).start();

        await().untilTrue(finished);
        assertTrue(objectId > 0);
    }

    private void getCategory() {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                try {
                    String label = json.getString("label");
                    assertEquals("旅客機", label);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.getCategory(2, objectId, null, callback);
        await().untilTrue(finished);
    }

    private void updateCategory() {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                try {
                    String label = json.getString("label");
                    assertEquals("航空", label);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        HashMap<String, String> params = new HashMap<>();
        params.put("label", "航空");
        api.updateCategory(2, objectId, params, callback);
        await().untilTrue(finished);
    }

    private void deleteCategory() {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int deletePostId = 0;
                try {
                    deletePostId = json.getInt("id");
                    assertEquals(objectId, deletePostId);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.deleteCategory(2, objectId, callback);
        await().untilTrue(finished);
    }

    @Test
    public void operateCategory() {
        this.makeCategory();
        this.getCategory();
        this.updateCategory();
        this.deleteCategory();
        objectId = 0;
    }

    @Test
    public void listPages() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int totalResults = 0;
                try {
                    totalResults = json.getInt("totalResults");
                    assertEquals(3, totalResults);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.listPages(1, null, callback);
        await().untilTrue(finished);
    }

    @Test
    public void listPagesForAsset() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int totalResults = 0;
                try {
                    totalResults = json.getInt("totalResults");
                    assertEquals(1, totalResults);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.listPagesForAsset(1, 1, null, callback);
        await().untilTrue(finished);
    }

    @Test
    public void listPagesForSiteAndTag() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int totalResults = 0;
                try {
                    totalResults = json.getInt("totalResults");
                    assertEquals(2, totalResults);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.listPagesForSiteAndTag(1, 56, null, callback);
        await().untilTrue(finished);
    }

    private void makePage() {
        final AtomicBoolean finished = new AtomicBoolean(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> authParams = makeAuthParams();
                api.authentication(authParams, null);

                HashMap<String, String> params = new HashMap<>();
                params.put("title", "About Movable Type Data API SDK for Java");
                params.put("body", "DataAPIを利用したウェブページの作成試験です。");
                params.put("status", "Publish");
                JSONObject json = api.createPage(2, params, null);
                try {
                    objectId = json.getInt("id");
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        }).start();

        await().untilTrue(finished);
        assertTrue(objectId > 0);
    }

    private void getPage() {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                try {
                    String title = json.getString("title");
                    assertEquals("About Movable Type Data API SDK for Java", title);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.getPage(2, objectId, null, callback);
        await().untilTrue(finished);
    }

    private void updatePage() {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                try {
                    String title = json.getString("title");
                    assertEquals("About Movable Type Data API SDK for Android", title);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        HashMap<String, String> params = new HashMap<>();
        params.put("title", "About Movable Type Data API SDK for Android");
        api.updatePage(2, objectId, params, callback);
        await().untilTrue(finished);
    }

    private void deletePage() {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int deletePostId = 0;
                try {
                    deletePostId = json.getInt("id");
                    assertEquals(objectId, deletePostId);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.deletePage(2, objectId, callback);
        await().untilTrue(finished);
    }

    @Test
    public void operatePage() {
        this.makePage();
        this.getPage();
        this.updatePage();
        this.deletePage();
        objectId = 0;
    }

    @Test
    public void listPagesForFolder() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int totalResults = 0;
                try {
                    totalResults = json.getInt("totalResults");
                    assertEquals(2, totalResults);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.listPagesForFolder(1, 1, null, callback);
        await().untilTrue(finished);
    }

    @Test
    public void listFolders() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int totalResults = 0;
                try {
                    totalResults = json.getInt("totalResults");
                    assertEquals(7, totalResults);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.listFolders(1, null, callback);
        await().untilTrue(finished);
    }

    @Test
    public void listParentFolders() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                System.out.print(json);
                try {
                    JSONArray items = json.getJSONArray("items");
                    JSONObject firstItem = items.getJSONObject(0);
                    int id = firstItem.getInt("id");
                    assertEquals(13, id);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.listParentFolders(1, 15, null, callback);
        await().untilTrue(finished);
    }

    @Test
    public void listSiblingFolders() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int totalResults = 0;
                try {
                    totalResults = json.getInt("totalResults");
                    assertEquals(2, totalResults);
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.listSiblingFolders(1, 15, null, callback);
        await().untilTrue(finished);
    }

    @Test
    public void listChildFolders() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);

        DataApi.Callback callback = new DataApi.Callback() {
            @Override
            public void onResponse(JSONObject json) {
                int totalResults = 0;
                try {
                    totalResults = json.getInt("totalResults");
                    assertEquals(3, totalResults);    // API Bug?: class="category"の要素も含まれる
                    finished.set(true);
                } catch (JSONException e) {
                    fail();
                }
            }
        };
        api.listChildFolders(1, 13, null, callback);
        await().untilTrue(finished);
    }
}
