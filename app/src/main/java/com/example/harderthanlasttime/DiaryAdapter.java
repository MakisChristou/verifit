package com.example.harderthanlasttime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.MyViewHolder> {

    Context ct;
    ArrayList<String> Dates;
    ArrayList<Double> Volumes;

    // Adapter Constructor 7 minute mark
    public DiaryAdapter(Context ct, ArrayList<String> Dates, ArrayList<Double> Volumes)
    {
        this.ct = ct;
        this.Dates = Dates;
        this.Volumes = Volumes;
        System.out.println("Debug5");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(this.ct);
        View view = inflater.inflate(R.layout.diary_row,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        holder.tv_date.setText(Dates.get(position));
        holder.tv_volume.setText(Volumes.get(position).toString());
        //holder.img_view.setImageResource();

    }

    @Override
    public int getItemCount()
    {
        System.out.println("Debug6");
        return Volumes.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        TextView tv_date;
        TextView tv_volume;
        ImageView img_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.date);
            tv_volume = itemView.findViewById(R.id.volume);
            img_view = itemView.findViewById(R.id.diaryImage);
        }
    }
}
