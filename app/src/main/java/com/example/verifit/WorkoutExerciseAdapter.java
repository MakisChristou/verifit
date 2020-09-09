package com.example.verifit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
        this.Exercises = new ArrayList<>(Exercises);
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
        String exercise_name = Exercises.get(position).getExercise();

        // Change TextView text
        holder.tv_exercise_name.setText(exercise_name);

        int sets = (int)Math.round(Exercises.get(position).getTotalSets());

        holder.sets.setText(String.valueOf(sets));

        // Set icon tint based on exercise category
        setCategoryIconTint(holder, exercise_name);

        // Set Volume PR Icon if exercise was a Volume PR
        if(Exercises.get(position).isPR())
        {
            holder.pr_button.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.pr_button.setVisibility(View.GONE);
        }


        holder.pr_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                showVolumePRDialog();
            }
        });


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                showExerciseDialog(position);
            }
        });
    }


    public void showVolumePRDialog()
    {
        // Prepare to show exercise dialog box
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.personal_record_dialog,null);
        AlertDialog alertDialog = new AlertDialog.Builder(ct).setView(view).create();

        // Show Exercise Dialog Box
        alertDialog.show();
    }

    // Simple
    public void setCategoryIconTint(MyViewHolder holder, String exercise_name)
    {
        String exercise_category = MainActivity.getExerciseCategory(exercise_name);

        if(exercise_category.equals("Shoulders"))
        {
            holder.imageView.setColorFilter(Color.argb(255, 37, 90, 160)); // Primary Color
        }
        else if(exercise_category.equals("Back"))
        {
            holder.imageView.setColorFilter(Color.argb(255, 40, 176, 192));
        }
        else if(exercise_category.equals("Chest"))
        {
            holder.imageView.setColorFilter(Color.argb(255, 	92, 88, 157));
        }
        else if(exercise_category.equals("Biceps"))
        {
            holder.imageView.setColorFilter(Color.argb(255, 	255, 50, 50));
        }
        else if(exercise_category.equals("Triceps"))
        {
            holder.imageView.setColorFilter(Color.argb(255,    204, 154, 0));
        }
        else if(exercise_category.equals("Legs"))
        {
            holder.imageView.setColorFilter(Color.argb(255, 	212, 	25, 97));
        }
        else if(exercise_category.equals("Abs"))
        {
            holder.imageView.setColorFilter(Color.argb(255, 	255, 153, 171));
        }
        else
        {
            holder.imageView.setColorFilter(Color.argb(255, 	52, 58, 64)); // Grey AF
        }
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
        TextView name = view.findViewById(R.id.tv_date);
        TextView onerepmax = view.findViewById(R.id.onerepmax);
        Button bt_close = view.findViewById(R.id.bt_close);
        Button bt_edit_exercise = view.findViewById(R.id.bt_edit_exercise);

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


        // Navigate to AddExercise Activity
        bt_edit_exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent in = new Intent(ct,AddExerciseActivity.class);
                in.putExtra("exercise",Exercises.get(position).getExercise());
                MainActivity.date_selected = Exercises.get(position).getDate(); // this is required by AddExerciseActivity
                System.out.println(Exercises.get(position).getExercise());
                System.out.println(MainActivity.date_selected);
                ct.startActivity(in);
            }
        });

        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Dismiss Exercise Dialog Box
                alertDialog.dismiss();
            }
        });

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
        ImageView imageView;
        ImageButton pr_button;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_exercise_name = itemView.findViewById(R.id.day);
            cardView = itemView.findViewById(R.id.cardview_exercise);
            sets = itemView.findViewById(R.id.sets);
            imageView = itemView.findViewById(R.id.imageView);
            pr_button = itemView.findViewById(R.id.pr_button);
        }
    }
}
