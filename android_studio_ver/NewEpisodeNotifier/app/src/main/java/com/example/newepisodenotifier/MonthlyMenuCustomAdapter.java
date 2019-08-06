package com.example.newepisodenotifier;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MonthlyMenuCustomAdapter extends RecyclerView.Adapter<MonthlyMenuCustomAdapter.MyViewHolder> {
    private final List<String[]> menus;

    public MonthlyMenuCustomAdapter(List<String[]> menus) {
        this.menus = menus;
    }

    @NonNull
    @Override
    public MonthlyMenuCustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.monthly_menu_row_item, parent, false);

        return new MonthlyMenuCustomAdapter.MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull MonthlyMenuCustomAdapter.MyViewHolder myViewHolder, int i) {
        String menu = menus.get(i)[0];
        myViewHolder.date.setText(menus.get(i)[1]);
        myViewHolder.menu.setText(menu);
        myViewHolder.urlInfo.setText(menus.get(i)[2]);

    }


    @Override
    public int getItemCount() {
        return menus.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView date;
        final TextView menu;
        final TextView urlInfo;
        MyViewHolder(@NonNull View view) {
            super(view);
            urlInfo = view.findViewById(R.id.urlInfo);
            menu = view.findViewById(R.id.menu);
            date = view.findViewById(R.id.date);

        }
    }

}

