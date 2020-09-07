package com.example.harderthanlasttime;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WorkoutDayAdapter extends RecyclerView.Adapter<WorkoutDayAdapter.WorkoutDayViewHolder> {

    ArrayList<WorkoutDay> Workout_Days;
    static Context ct;

    // Constructor
    public WorkoutDayAdapter(Context ct, ArrayList<WorkoutDay> Workout_Days)
    {
        this.Workout_Days = new ArrayList<>(Workout_Days);
        WorkoutDayAdapter.ct = ct;
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

    // Magic Happens here
    static class WorkoutDayViewHolder extends RecyclerView.ViewHolder
    {
        private TextView tv_date;

        public WorkoutDayViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
        }

        // Set All Data
        void setData(WorkoutDay today)
        {
            String Date_Str1 = today.getDate();

            // Find which exercices were performed that given date
            ArrayList<WorkoutExercise> Today_Execrises = new ArrayList<WorkoutExercise>();
            for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
            {
                if(Date_Str1.equals(MainActivity.Workout_Days.get(i).getDate()))
                {
                    Today_Execrises = MainActivity.Workout_Days.get(i).getExercises();
                }
            }

            // Convert Date To Something Sensible
            try
            {
                Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(Date_Str1); //potential exception
                DateFormat date2 = new SimpleDateFormat("EEEE dd MMMM yyyy");
                String Date_Str2 = date2.format(date1);
                tv_date.setText(Date_Str2);

            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

            // Set Recycler View
            RecyclerView recyclerView = itemView.findViewById(R.id.recyclerView);
            WorkoutExerciseAdapter2 workoutExerciseAdapter = new WorkoutExerciseAdapter2(ct, Today_Execrises);
            recyclerView.setAdapter(workoutExerciseAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(ct));

        }
    }




}
