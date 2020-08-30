package com.example.harderthanlasttime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


// Adapter for WorkoutExercise Class
public class WorkoutExerciseAdapter2 extends RecyclerView.Adapter<WorkoutExerciseAdapter2.MyViewHolder> {

    Context ct;
    ArrayList<WorkoutExercise> Exercises;

    public WorkoutExerciseAdapter2(Context ct, ArrayList<WorkoutExercise> Exercises)
    {
        this.ct = ct;
        this.Exercises = Exercises;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(this.ct);
        View view = inflater.inflate(R.layout.workout_exercise_row2,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        // Change TextView text
        holder.tv_exercise_name.setText(Exercises.get(position).getExercise());

        // Recycler View Stuff
        // Change RecyclerView items
        WorkoutSetAdapter workoutSetAdapter = new WorkoutSetAdapter(ct, Exercises.get(position).getSets());
        holder.recyclerView.setAdapter(workoutSetAdapter);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(ct));
    }

    @Override
    public int getItemCount()
    {
        return this.Exercises.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        TextView tv_exercise_name;
        RecyclerView recyclerView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_exercise_name = itemView.findViewById(R.id.set_weight);
            recyclerView = itemView.findViewById(R.id.recycler_view_day);

        }
    }
}
