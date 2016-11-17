package pw.anothersky.sitereader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;

import pw.anothersky.movabletype.apisdk.DataAPI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Test
        String apiURL = getString(R.string.development);
        EntryLoader loader = new EntryLoader(MainActivity.this, apiURL);
        loader.forceLoad();
    }
}
