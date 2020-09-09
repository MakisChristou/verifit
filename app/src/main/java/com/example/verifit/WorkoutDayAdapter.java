package com.example.verifit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WorkoutDayAdapter extends RecyclerView.Adapter<WorkoutDayAdapter.WorkoutDayViewHolder> {

    ArrayList<WorkoutDay> Workout_Days;
    Context ct;

    // Constructor
    public WorkoutDayAdapter(Context ct, ArrayList<WorkoutDay> Workout_Days)
    {
        this.Workout_Days = new ArrayList<>(Workout_Days);
        this.ct = ct;
    }

    @NonNull
    @Override
    public WorkoutDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new WorkoutDayViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_view_pager,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutDayViewHolder holder, int position)
    {

        // Set Data Method used to be here :D
        String Date_Str1 = Workout_Days.get(position).getDate();

        // Find which exercises were performed that given date
        ArrayList<WorkoutExercise> Today_Execrises = new ArrayList<WorkoutExercise>();
        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
        {
            if(Date_Str1.equals(MainActivity.Workout_Days.get(i).getDate()))
            {
                Today_Execrises = MainActivity.Workout_Days.get(i).getExercises();
            }
        }

        // Set Recycler View
        ViewPagerExerciseAdapter workoutExerciseAdapter = new ViewPagerExerciseAdapter(ct, Today_Execrises);
        holder.recyclerView_Main.setAdapter(workoutExerciseAdapter);
        holder.recyclerView_Main.setLayoutManager(new LinearLayoutManager(ct));

        // Convert Date To Something Sensible
        try
        {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(Date_Str1); //potential exception
            DateFormat date2 = new SimpleDateFormat("EEEE");
            DateFormat date3 = new SimpleDateFormat("MMMM dd yyyy");
            String Date_Str2 = date2.format(date1);
            String Date_Str3 = date3.format(date1);
            holder.tv_date.setText(Date_Str2);
            holder.tv_full_date.setText(Date_Str3);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        holder.tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent in = new Intent(ct,ExercisesActivity.class);
                MainActivity.date_selected = Date_Str1;
                ct.startActivity(in);
            }
        });


    }


    @Override
    public int getItemCount() {
        return Workout_Days.size();
    }

    // Magic Happens here
    static class WorkoutDayViewHolder extends RecyclerView.ViewHolder
    {
        private TextView tv_date;
        private TextView tv_full_date;
        private RecyclerView recyclerView_Main;
        private CardView cardView;

        public WorkoutDayViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
            cardView = itemView.findViewById(R.id.cardview_main);
            recyclerView_Main = itemView.findViewById(R.id.recyclerView_Main);
            tv_full_date = itemView.findViewById(R.id.tv_full_date);
        }
    }
}
