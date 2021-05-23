package com.example.sports;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.LinkedList;

public class SportsAdapter extends RecyclerView.Adapter<SportsAdapter.SportsViewHolder> {
    private final ArrayList<Sport> sports;
    private LayoutInflater inflater;
    private Context context;

    public SportsAdapter(Context context, ArrayList<Sport> sports){
        this.sports = sports;
        this.inflater = LayoutInflater.from(context);
        this.context = context;

    }

    class SportsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView sportTitleView;
        public final TextView sportInfoView;
        public final ImageView sportImageView;
        private final SportsAdapter adapter;

        public SportsViewHolder(@NonNull View itemView, SportsAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            sportInfoView = itemView.findViewById(R.id.news_content);
            sportTitleView = itemView.findViewById(R.id.sport_title);
            sportImageView = itemView.findViewById(R.id.sports_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Sport current = sports.get(getAdapterPosition());
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("title", current.getName());
            intent.putExtra("image", current.getImage());

            context.startActivity(intent);

        }
    }
    @NonNull
    @Override
    public SportsAdapter.SportsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.sport_item, parent, false);

        return new SportsViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull SportsAdapter.SportsViewHolder holder, int position) {
        Sport current = sports.get(position);
        holder.sportTitleView.setText(current.getName());
        holder.sportInfoView.setText(current.getInfo());
        Glide.with(context).load(current.getImage()).into(holder.sportImageView);
    }

    @Override
    public int getItemCount() {
        return sports.size();
    }
}
