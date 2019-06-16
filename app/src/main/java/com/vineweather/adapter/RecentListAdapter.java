package com.vineweather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vineweather.R;
import com.vineweather.adapter.RecentListAdapter.RecentLocationsViewHolder;
import com.vineweather.helper.Helper;

import java.util.ArrayList;

public class RecentListAdapter extends RecyclerView.Adapter<RecentLocationsViewHolder> {

    private ArrayList<String> locations;
    private RecentSelectedListener listener;

    public RecentListAdapter(ArrayList<String> locations, RecentSelectedListener listener) {
        this.locations = locations;
        this.listener = listener;
    }

    @Override
    public RecentLocationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecentLocationsViewHolder(parent.inflate(parent.getContext(), R.layout.row_recents, null));
    }

    @Override
    public void onBindViewHolder(RecentLocationsViewHolder holder, final int position) {
        final String location = locations.get(position);
        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRecentSelected(location);
            }
        });
        holder.name.setText(Helper.filterCityName(location));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    class RecentLocationsViewHolder extends RecyclerView.ViewHolder {

        private TextView name;

        RecentLocationsViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.location_name);
        }
    }

    public interface RecentSelectedListener {
        void onRecentSelected(String selectedLocationName);
    }
}
