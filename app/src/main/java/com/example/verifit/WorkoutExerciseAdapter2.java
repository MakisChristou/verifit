package com.example.verifit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
        this.Exercises = new ArrayList<>(Exercises);
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


        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                showExerciseDialog(position);
            }
        });


    }

    // Blatant copy of Fitnotes but ohh well ;)
    public void showExerciseDialog(int position) {

        Intent in = new Intent(ct,AddExerciseActivity.class);
        in.putExtra("exercise",Exercises.get(position).getExercise());
        ct.startActivity(in);

        // Show Exercise Stats (Not used but decided to leave it there)
        if(false)
        {
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

            // Set Values

            // Double -> Integer
            int sets = (int)Math.round(Exercises.get(position).getTotalSets());
            int reps = (int)Math.round(Exercises.get(position).getTotalReps());
            int max_reps = (int)Math.round(Exercises.get(position).getMaxReps());

            totalsets.setText(String.valueOf(sets));
            totalreps.setText(String.valueOf(reps));
            maxreps.setText(String.valueOf(max_reps));


            totalvolume.setText(Exercises.get(position).getVolume().toString());
            maxweight.setText(Exercises.get(position).getMaxWeight().toString());
            onerepmax.setText(Exercises.get(position).getEstimatedOneRepMax().toString());
            name.setText(Exercises.get(position).getExercise());
            maxsetvolume.setText(Exercises.get(position).getMaxSetVolume().toString());

            // Show Exercise Dialog Box
            alertDialog.show();
        }

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
        ImageButton editButton;
        View blue_line;
        CardView cardview_exercise2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_exercise_name = itemView.findViewById(R.id.tv_date);
            recyclerView = itemView.findViewById(R.id.recycler_view_day);
            editButton = itemView.findViewById(R.id.editButton);
            blue_line = itemView.findViewById(R.id.blue_line);
            cardview_exercise2 = itemView.findViewById(R.id.cardview_exercise2);

                // Expand More/Less
                cardview_exercise2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerView.getVisibility() == View.GONE)
                    {
                        // Expand Button Animation
                        recyclerView.setVisibility(View.VISIBLE);
                        blue_line.setVisibility(View.VISIBLE);
                        notifyItemChanged(getAdapterPosition());

//                         Expand Button Animation
//                        RotateAnimation rotate = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF, 0.5f,          Animation.RELATIVE_TO_SELF, 0.5f);
//                        rotate.setDuration(200);
//                        rotate.setInterpolator(new LinearInterpolator());
//                        expandButton.startAnimation(rotate);
//                        expandButton.setImageResource(R.drawable.ic_expand_less_24px);
                    }
                    else if(recyclerView.getVisibility() == View.VISIBLE)
                    {
                        recyclerView.setVisibility(View.GONE);
                        blue_line.setVisibility(View.INVISIBLE);


//                         Expand Button Animation
//                        RotateAnimation rotate = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f,          Animation.RELATIVE_TO_SELF, 0.5f);
//                        rotate.setDuration(200);
//                        rotate.setInterpolator(new LinearInterpolator());
//                        expandButton.startAnimation(rotate);
//                        expandButton.setImageResource(R.drawable.ic_expand_more_24px);
                    }
                }
            });

        }
    }
}
