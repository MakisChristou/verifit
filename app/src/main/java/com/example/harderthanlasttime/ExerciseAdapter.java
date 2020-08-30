package com.example.harderthanlasttime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


// Adapter for Exercise Class
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.MyViewHolder> {

    Context ct;
    ArrayList<Exercise> Exercises;

    // Adapter Constructor 7 minute mark
    public ExerciseAdapter(Context ct, ArrayList<Exercise> Exercises)
    {
        this.ct = ct;
        this.Exercises = Exercises;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(this.ct);
        View view = inflater.inflate(R.layout.exercise_row,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        // Change TextView text
        holder.tv_exercise_name.setText(Exercises.get(position).getName());
        holder.tv_exercise_category.setText(Exercises.get(position).getCategory());
        holder.tv_exercise_bodypart.setText(Exercises.get(position).getBodyPart());


    }

    @Override
    public int getItemCount()
    {
        return this.Exercises.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        TextView tv_exercise_name;
        TextView tv_exercise_category;
        TextView tv_exercise_bodypart;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_exercise_name = itemView.findViewById(R.id.date);
            tv_exercise_category = itemView.findViewById(R.id.exercise_category);
            tv_exercise_bodypart = itemView.findViewById(R.id.exercise_bodypart);
        }
    }
}
