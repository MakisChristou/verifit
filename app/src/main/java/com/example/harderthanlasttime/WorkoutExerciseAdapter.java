package com.example.harderthanlasttime;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;



// Adapter for WorkoutExercise Class
public class WorkoutExerciseAdapter extends RecyclerView.Adapter<WorkoutExerciseAdapter.MyViewHolder> {

    Context ct;
    ArrayList<WorkoutExercise> Exercises;

    public WorkoutExerciseAdapter(Context ct, ArrayList<WorkoutExercise> Exercises)
    {
        this.ct = ct;
        this.Exercises = Exercises;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(this.ct);
        View view = inflater.inflate(R.layout.workout_exercise_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        // Change TextView text
        holder.tv_exercise_name.setText(Exercises.get(position).getExercise());


        holder.tv_exercise_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                showExerciseDialog(position);
            }
        });

    }

    // Blatant copy of Fitnotes but ohh well ;)
    public void showExerciseDialog(int position) {

        // Prepare to show exercise dialog box
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.exercise_dialog,null);
        AlertDialog alertDialog = new AlertDialog.Builder(ct).setView(view).create();

        // Get TextViews
        TextView totalsets = view.findViewById(R.id.totalsets);
        TextView totalreps = view.findViewById(R.id.totalreps);
        TextView totalvolume = view.findViewById(R.id.totalvolume);
        TextView maxweight = view.findViewById(R.id.maxweight);
        TextView maxreps = view.findViewById(R.id.maxreps);
        TextView maxsetvolume = view.findViewById(R.id.maxsetvolume);
        TextView name = view.findViewById(R.id.set_weight);
        TextView onerepmax = view.findViewById(R.id.onerepmax);

        // Set Values
        totalsets.setText(Exercises.get(position).getTotalSets().toString());
        totalreps.setText(Exercises.get(position).getTotalReps().toString());
        totalvolume.setText(Exercises.get(position).getVolume().toString());
        maxweight.setText(Exercises.get(position).getMaxWeight().toString());
        maxreps.setText(Exercises.get(position).getMaxReps().toString());
        onerepmax.setText(Exercises.get(position).getEstimatedOneRepMax().toString());
        name.setText(Exercises.get(position).getExercise());
        maxsetvolume.setText(Exercises.get(position).getMaxSetVolume().toString());

        // Show Exercise Dialog Box
        alertDialog.show();

    }

    @Override
    public int getItemCount()
    {
        return this.Exercises.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        TextView tv_exercise_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_exercise_name = itemView.findViewById(R.id.day);

        }
    }
}
