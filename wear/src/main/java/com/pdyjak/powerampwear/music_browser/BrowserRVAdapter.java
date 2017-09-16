package com.pdyjak.powerampwear.music_browser;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BrowserRVAdapter<T> extends RecyclerView.Adapter<ItemViewHolder> {

    @NonNull
    private final ViewHolderFactory mFactory;
    @NonNull
    private final List<T> mItems = new ArrayList<>();

    public BrowserRVAdapter(@NonNull ViewHolderFactory factory) {
        mFactory = factory;
    }

    public void setItems(@NonNull List<T> items) {
        if (mItems.equals(items)) return;
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    public List<T> getItems() {
        return Collections.unmodifiableList(mItems);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mFactory.createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bindWith(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
