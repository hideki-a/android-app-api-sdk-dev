package pw.anothersky.apisdkdev;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pw.anothersky.apisdkdev.data.Entry;
import pw.anothersky.sitereader.R;

public class MainActivity extends AppCompatActivity implements EntryListFragment.EntryListFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onEntryClicked(@NonNull Entry entry) {
        // リンクがタップされたら、リンク先のページを開く
    }
}
