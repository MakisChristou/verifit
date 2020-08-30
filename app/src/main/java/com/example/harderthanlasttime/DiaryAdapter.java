package com.example.harderthanlasttime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

// Adapter for WorkoutDay Class
public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.MyViewHolder> {

    Context ct;
    ArrayList<WorkoutDay> Workout_Days;

    // Adapter Constructor 7 minute mark
    public DiaryAdapter(Context ct, ArrayList<WorkoutDay> Workout_Days)
    {
        this.ct = ct;
        this.Workout_Days = Workout_Days;
        Collections.reverse(this.Workout_Days);
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
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");

        // Possible Error
        try {

            Date date = parser.parse(Workout_Days.get(position).getDate());
            SimpleDateFormat formatter2 = new SimpleDateFormat("EEEE, MMM dd YYYY");

            // Change TextView texts
            holder.tv_day.setText(formatter2.format(date));


            holder.tv_volume.setText(Workout_Days.get(position).getDayVolume().toString());
            holder.tv_sets.setText(String.valueOf(Workout_Days.get(position).getSets().size()));
            holder.tv_exercises.setText(String.valueOf(Workout_Days.get(position).getExercises().size()));
            holder.tv_reps.setText(String.valueOf(Workout_Days.get(position).getReps()));

            // Change RecyclerView items
            WorkoutExerciseAdapter workoutExerciseAdapter = new WorkoutExerciseAdapter(ct, MainActivity.Workout_Days.get(position).getExercises());
            holder.recyclerView.setAdapter(workoutExerciseAdapter);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(ct));


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount()
    {
        return Workout_Days.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        TextView tv_day;
        TextView tv_date;
        TextView tv_volume;
        TextView tv_reps;
        TextView tv_sets;
        TextView tv_exercises;
        RecyclerView recyclerView;
        ImageButton expand_button;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_day = itemView.findViewById(R.id.day);
            tv_date = itemView.findViewById(R.id.exercise_name);
            tv_volume = itemView.findViewById(R.id.totalvolume);
            tv_reps = itemView.findViewById(R.id.totalreps);
            tv_sets = itemView.findViewById(R.id.totalsets);
            tv_exercises = itemView.findViewById(R.id.totalexercises);

            // Find Recycler View Object
            recyclerView = itemView.findViewById(R.id.recycler_view_diary);
            expand_button = itemView.findViewById(R.id.expand_button);




            // Expand More/Less Button
            expand_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerView.getVisibility() == View.GONE)
                    {
                        recyclerView.setVisibility(View.VISIBLE);
                        expand_button.setImageResource(R.drawable.ic_expand_less_24px);
                    }
                    else if(recyclerView.getVisibility() == View.VISIBLE)
                    {
                        recyclerView.setVisibility(View.GONE);
                        expand_button.setImageResource(R.drawable.ic_expand_more_24px);
                    }
                }
            });

        }
    }
}
