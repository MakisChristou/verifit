package com.example.harderthanlasttime;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

        // Double -> Integer
        int reps = (int)Math.round(Exercises.get(position).getTotalReps());
        int sets = (int)Math.round(Exercises.get(position).getTotalSets());

        holder.reps.setText(String.valueOf(reps));
        holder.sets.setText(String.valueOf(sets));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
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
        TextView totalsets = view.findViewById(R.id.volume);
        TextView totalreps = view.findViewById(R.id.totalreps);
        TextView totalvolume = view.findViewById(R.id.totalvolume);
        TextView maxweight = view.findViewById(R.id.maxweight);
        TextView maxreps = view.findViewById(R.id.maxreps);
        TextView maxsetvolume = view.findViewById(R.id.maxsetvolume);
        TextView name = view.findViewById(R.id.exercise_name);
        TextView onerepmax = view.findViewById(R.id.onerepmax);

        // Set Values

        // Double -> Integer
        int sets = (int)Math.round(Exercises.get(position).getTotalSets());
        int reps = (int)Math.round(Exercises.get(position).getTotalReps());
        int max_reps = (int)Math.round(Exercises.get(position).getMaxReps());

        totalsets.setText(String.valueOf(sets));
        totalreps.setText(String.valueOf(reps));
        maxreps.setText(String.valueOf(max_reps));

        // Double
        totalvolume.setText(Exercises.get(position).getVolume().toString());
        maxweight.setText(Exercises.get(position).getMaxWeight().toString());
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
        CardView cardView;
        TextView sets;
        TextView reps;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_exercise_name = itemView.findViewById(R.id.day);
            cardView = itemView.findViewById(R.id.cardview_exercise);
            sets = itemView.findViewById(R.id.sets);
            reps = itemView.findViewById(R.id.reps);


        }
    }
}
