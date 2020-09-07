package com.example.harderthanlasttime;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WorkoutDayAdapter extends RecyclerView.Adapter<WorkoutDayAdapter.WorkoutDayViewHolder> {

    private ArrayList<WorkoutDay> Workout_Days;

    // Constructor
    public WorkoutDayAdapter(ArrayList<WorkoutDay> Workout_Days)
    {
        this.Workout_Days = new ArrayList<>(Workout_Days);
    }

    @NonNull
    @Override
    public WorkoutDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new WorkoutDayViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_view_pager,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutDayViewHolder holder, int position)
    {
        holder.setData(Workout_Days.get(position));
    }

    @Override
    public int getItemCount() {
        return Workout_Days.size();
    }

    static class WorkoutDayViewHolder extends RecyclerView.ViewHolder
    {
        private TextView tv_date;

        public WorkoutDayViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
        }

        void setData(WorkoutDay today)
        {
            tv_date.setText(today.getDate());
        }
    }




}
