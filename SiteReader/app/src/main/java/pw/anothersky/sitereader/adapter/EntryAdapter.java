package pw.anothersky.sitereader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pw.anothersky.sitereader.R;
import pw.anothersky.sitereader.data.Entry;

/**
 * Created by Hideki on 2016/11/21.
 */

public class EntryAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_ENTRY = 0;

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Entry> mEntries;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Entry entry);
    }

    public EntryAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mEntries = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mEntries.size();
    }

    public int getItemViewType(int position) {
        return VIEW_TYPE_ENTRY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ENTRY) {
            View view = mInflater.inflate(R.layout.item_entry, parent, false);
            return new EntryViewHolder(view, this);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EntryViewHolder) {
            EntryViewHolder entryHolder = (EntryViewHolder) holder;
            Entry entry = mEntries.get(position);

            entryHolder.title.setText(entry.getTitle());
            entryHolder.pubdate.setText(entry.getDate());
        }
    }

    public void addItems(List<Entry> entries) {
        mEntries.addAll(entries);
        notifyDataSetChanged();
    }

    private static class EntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private EntryAdapter adapter;
        private TextView title;
        private TextView pubdate;

        public EntryViewHolder(View itemView, EntryAdapter adapter) {
            super(itemView);

            this.adapter = adapter;
            title = (TextView) itemView.findViewById(R.id.Title);
            pubdate = (TextView) itemView.findViewById(R.id.PubDate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (adapter.mListener != null) {
                int position = getLayoutPosition();
                Entry data = adapter.mEntries.get(position);
                adapter.mListener.onItemClick(data);
            }
        }
    }
}
