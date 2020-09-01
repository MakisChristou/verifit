package com.example.harderthanlasttime;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Adapter for WorkoutSet Class
public class WorkoutSetAdapter2 extends RecyclerView.Adapter<WorkoutSetAdapter2.MyViewHolder> {

    Context ct;
    ArrayList<WorkoutSet> Workout_Sets;

    public WorkoutSetAdapter2(Context ct, ArrayList<WorkoutSet> Workout_Sets)
    {
        this.ct = ct;
        this.Workout_Sets = Workout_Sets;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(this.ct);
        View view = inflater.inflate(R.layout.workout_set_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {

        // Double is fine
        holder.tv_weight.setText(Workout_Sets.get(position).getWeight().toString());
        // Double -> Integer
        int reps = (int)Math.round(Workout_Sets.get(position).getReps());
        holder.tv_reps.setText(String.valueOf(reps));


        // Updates Edit Texts and Buttons when clicked
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateView(position);
            }
        });
    }

    public void updateView(int position)
    {


    }


    @Override
    public int getItemCount()
    {
        return Workout_Sets.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        TextView tv_reps;
        TextView tv_weight;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_reps = itemView.findViewById(R.id.set_reps);
            tv_weight = itemView.findViewById(R.id.exercise_name);
            cardView = itemView.findViewById(R.id.cardview_set);


        }
    }
}
