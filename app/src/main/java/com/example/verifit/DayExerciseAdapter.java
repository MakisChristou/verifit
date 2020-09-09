package com.example.verifit;

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
public class DayExerciseAdapter extends RecyclerView.Adapter<DayExerciseAdapter.MyViewHolder> {

    Context ct;
    ArrayList<WorkoutExercise> Exercises;

    public DayExerciseAdapter(Context ct, ArrayList<WorkoutExercise> Exercises)
    {
        this.ct = ct;
        this.Exercises = new ArrayList<>(Exercises);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(this.ct);
        View view = inflater.inflate(R.layout.day_exercise_row,parent,false);
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
                startIntent(position);
            }
        });


    }

    // Go to Add Exercise
    public void startIntent(int position)
    {
        Intent in = new Intent(ct,AddExerciseActivity.class);
        in.putExtra("exercise",Exercises.get(position).getExercise());
        ct.startActivity(in);
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
            cardview_exercise2 = itemView.findViewById(R.id.cardview_exercise_history);

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
