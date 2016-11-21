package pw.anothersky.sitereader;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pw.anothersky.sitereader.data.Entry;

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
