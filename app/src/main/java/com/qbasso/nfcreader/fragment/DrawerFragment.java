package com.qbasso.nfcreader.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qbasso.nfcreader.R;
import com.qbasso.nfcreader.model.DrawerItem;

import java.util.ArrayList;
import java.util.List;

public class DrawerFragment extends Fragment {
    private RecyclerView list;
    private DrawerOptionsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_drawer, null);
        list = (RecyclerView) v;
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupDrawerOptions();
    }

    private void setupDrawerOptions() {
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<DrawerItem> items = new ArrayList<>();
        items.add(new DrawerItem(DrawerItem.TYPE_HEADER, ""));
        items.add(new DrawerItem(DrawerItem.TYPE_REGULAR, "Movies"));
        items.add(new DrawerItem(DrawerItem.TYPE_REGULAR, "Characters"));
        items.add(new DrawerItem(DrawerItem.TYPE_REGULAR, "My collection"));
        items.add(new DrawerItem(DrawerItem.TYPE_REGULAR, "My favorites"));
        items.add(new DrawerItem(DrawerItem.TYPE_REGULAR, "Discover"));
        items.add(new DrawerItem(DrawerItem.TYPE_REGULAR, "Reedem a code"));
        items.add(new DrawerItem(DrawerItem.TYPE_REGULAR, "Logout"));
        adapter = new DrawerOptionsAdapter(items);
        list.setAdapter(adapter);
    }

    public static DrawerFragment newInstance() {
        return new DrawerFragment();
    }

    private class DrawerOptionsAdapter extends RecyclerView.Adapter {
        private final List<DrawerItem> data;

        DrawerOptionsAdapter(List<DrawerItem> data) {
            this.data = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case DrawerItem.TYPE_HEADER:
                    View v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.drawer_header, parent, false);
                    return new HeaderViewHolder(v);
                case DrawerItem.TYPE_REGULAR:
                default:
                    View option = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.drawer_item, parent, false);
                    return new RegularViewHolder(option);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case DrawerItem.TYPE_HEADER:
                    break;
                case DrawerItem.TYPE_REGULAR:
                default:
                    DrawerItem item = data.get(position);
                    ((RegularViewHolder) holder).text.setText(item.getValue());
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public int getItemViewType(int position) {
            return data.get(position).getType();
        }
    }


    private class RegularViewHolder extends RecyclerView.ViewHolder {

        TextView text;

        RegularViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView;
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
