package pw.anothersky.sitereader;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pw.anothersky.sitereader.R;
import pw.anothersky.sitereader.adapter.EntryAdapter;
import pw.anothersky.sitereader.data.Entry;
import pw.anothersky.sitereader.loader.EntryListLoader;

/**
 * Created by Hideki on 2016/11/21.
 */

public class EntryListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Entry>>, EntryAdapter.OnItemClickListener {
    private static final int LOADER_ENTRIES = 1;

    public interface EntryListFragmentListener {
        void onEntryClicked(@NonNull Entry entry);
    }

    private EntryAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof EntryListFragmentListener)) {
            throw new RuntimeException(context.getClass().getSimpleName() + " does not implement EntryListFragmentListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ENTRIES, null, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        getLoaderManager().destroyLoader(LOADER_ENTRIES);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_entries, container, false);

        Context context = inflater.getContext();
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.EntryList);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(context,
                        LinearLayoutManager.VERTICAL, false));

        mAdapter = new EntryAdapter(context);
        mAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onItemClick(Entry entry) {
        EntryListFragmentListener listener = (EntryListFragmentListener) getActivity();

        if (listener != null) {
            listener.onEntryClicked(entry);
        }
    }

    @Override
    public Loader<List<Entry>> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ENTRIES) {
            EntryListLoader loader = new EntryListLoader(getActivity());
            loader.forceLoad();
            return loader;
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Entry>> loader, List<Entry> data) {
        int id = loader.getId();

        if (id == LOADER_ENTRIES && data != null && data.size() > 0) {
            mAdapter.addItems(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Entry>> loader) {

    }
}
