package pw.anothersky.sitereader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Test
        String apiURL = getString(R.string.development);
        String apiUsername = getString(R.string.development_mt_username);
        String apiPassword = getString(R.string.development_mt_password);
        EntryLoader loader = new EntryLoader(MainActivity.this, apiURL, apiUsername, apiPassword);
        loader.forceLoad();
    }
}
