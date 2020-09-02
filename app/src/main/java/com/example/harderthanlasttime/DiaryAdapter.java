package com.example.harderthanlasttime;

import android.app.AlertDialog;
import android.content.Context;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

            // Change RecyclerView items
            WorkoutExerciseAdapter workoutExerciseAdapter = new WorkoutExerciseAdapter(ct, MainActivity.Workout_Days.get(position).getExercises());
            holder.recyclerView.setAdapter(workoutExerciseAdapter);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(ct));


        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.cardview_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                showDayDialog(position);
            }

        });
    }

    // Shows Day's Stats when clicked
    public void showDayDialog(int position)
    {
        // Prepare to show exercise dialog box
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.day_dialog,null);
        AlertDialog alertDialog = new AlertDialog.Builder(ct).setView(view).create();

        // Get TextViews
        TextView totalsets = view.findViewById(R.id.volume);
        TextView totalreps = view.findViewById(R.id.totalreps);
        TextView totalvolume = view.findViewById(R.id.totalvolume);
        TextView date = view.findViewById(R.id.exercise_name);
        TextView totalexercises = view.findViewById(R.id.totalexercises);

        // Crash Here
        totalsets.setText(String.valueOf(Workout_Days.get(position).getSets().size()));
        totalreps.setText(String.valueOf(Workout_Days.get(position).getReps()));
        totalexercises.setText(String.valueOf(Workout_Days.get(position).getExercises().size()));
        totalvolume.setText(Workout_Days.get(position).getDayVolume().toString());

        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");

        // Possible Error
        try {

            Date date_object = parser.parse(Workout_Days.get(position).getDate());
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM dd YYYY");

            // Format Date like a human being
            // date.setText(Workout_Days.get(position).getDate());

            date.setText(formatter.format(date_object));

        }catch (ParseException e){
            e.printStackTrace();
        }



        // Show Exercise Dialog Box
        alertDialog.show();
    }


    @Override
    public int getItemCount()
    {
        return Workout_Days.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        TextView tv_day;

        RecyclerView recyclerView;
        ImageButton expand_button;
        CardView cardview_diary;

        // For Animation
        ViewGroup tcontainer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_day = itemView.findViewById(R.id.day);

            // Find Recycler View Object
            recyclerView = itemView.findViewById(R.id.recycler_view_diary);
            expand_button = itemView.findViewById(R.id.expand_button);
            cardview_diary = itemView.findViewById(R.id.cardview_diary);

            // For Animation
            tcontainer = itemView.findViewById(R.id.constraint_diary);


            // Expand More/Less Button
            expand_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerView.getVisibility() == View.GONE)
                    {
                        // For Animation
                        TransitionManager.beginDelayedTransition(tcontainer);

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
