package pw.anothersky.sitereader;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

import pw.anothersky.movabletype.apisdk.DataApi;

/**
 * Local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DataApiUnitTest {
    private static final DataApi api = DataApi.sharedInstance;
    private static String userName;
    private static String password;
    private static String comment;

    @BeforeClass
    public static void setupAPIConnection() throws Exception {
        InputStream in = DataApiUnitTest.class.getClassLoader().getResourceAsStream("server.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String data[] = new String[3];
        String line;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            data[i] = line;
            i += 1;
        }

        DataApiUnitTest.api.apiBaseUrl = data[0];
        DataApiUnitTest.userName = data[1];
        DataApiUnitTest.password = data[2];
    }

    @Test
    public void apiURLTest() throws Exception {
        assertNotEquals("http://localhost/cgi-bin/MT-6.1/mt-data-api.cgi", this.api.apiBaseUrl);
    }
}