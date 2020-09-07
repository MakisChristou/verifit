package com.example.harderthanlasttime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

//        Makes Card View Clickable and it's visually ugly
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                System.out.println("CardView Clicked!");
//            }
//        });

        // Back Button
        holder.imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                System.out.println("Back!");
            }
        });

        // Forward Button
        holder.imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                System.out.println("Forward!");
            }
        });

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

        MainActivity.date_selected = Date_Str1;


        // Set Recycler View
        WorkoutExerciseAdapter2 workoutExerciseAdapter = new WorkoutExerciseAdapter2(ct, Today_Execrises);
        holder.recyclerView_Main.setAdapter(workoutExerciseAdapter);
        holder.recyclerView_Main.setLayoutManager(new LinearLayoutManager(ct));

        // Convert Date To Something Sensible
        try
        {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(Date_Str1); //potential exception
            DateFormat date2 = new SimpleDateFormat("EEEE dd MMM yyyy");
            String Date_Str2 = date2.format(date1);
            holder.tv_date.setText(Date_Str2);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

    }



    @Override
    public int getItemCount() {
        return Workout_Days.size();
    }

    // Magic Happens here
    static class WorkoutDayViewHolder extends RecyclerView.ViewHolder
    {
        private TextView tv_date;
        private RecyclerView recyclerView_Main;
        private CardView cardView;
        private ImageButton imageButton2;
        private ImageButton imageButton3;

        public WorkoutDayViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
            cardView = itemView.findViewById(R.id.cardview_main);
            imageButton2 = itemView.findViewById(R.id.imageButton2);
            imageButton3 = itemView.findViewById(R.id.imageButton3);
            recyclerView_Main = itemView.findViewById(R.id.recyclerView_Main);
        }
    }
}
