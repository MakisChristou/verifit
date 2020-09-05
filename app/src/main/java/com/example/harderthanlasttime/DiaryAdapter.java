package com.example.harderthanlasttime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
        this.Workout_Days = new ArrayList<WorkoutDay>(Workout_Days);


//        // Reverse Workout List only when you have to
//        try {
//            String date1 = this.Workout_Days.get(0).getDate();
//            String date2 = this.Workout_Days.get(this.Workout_Days.size()-1).getDate();
//            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
//            Date date_object1 = parser.parse(date1);
//            Date date_object2 = parser.parse(date2);
//
//            if (date1.compareTo(date2) < 0)
//            {
//                // System.out.println(date1 + " is before " + date2);
////                System.out.println("Reversing Workout_Days");
////                Collections.reverse(this.Workout_Days);
//            }
//
//            }catch (ParseException e){
//                System.out.println(e.getMessage());
//                e.printStackTrace();
//            }
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
            SimpleDateFormat formatter1 = new SimpleDateFormat("EEEE");
            SimpleDateFormat formatter2 = new SimpleDateFormat("MMMM dd YYYY");

            // Change TextView texts
            holder.tv_day.setText(formatter1.format(date));
            holder.date.setText(formatter2.format(date));


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
        Button bt_viewDay = view.findViewById(R.id.bt_viewDay);
        Button bt_deleteDay = view.findViewById(R.id.bt_deleteDay);


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

        // Goto Day Activity
        bt_viewDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(ct,DayActivity.class);

                // Update Date Selected in MainActivity
                MainActivity.date_selected = Workout_Days.get(position).getDate();
                in.putExtra("date",Workout_Days.get(position).getDate());
                ct.startActivity(in);
            }
        });

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
        TextView date;
        RecyclerView recyclerView;
        ImageButton expand_button;
        CardView cardview_diary;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_day = itemView.findViewById(R.id.day);
            date = itemView.findViewById(R.id.date);

            // Find Recycler View Object
            recyclerView = itemView.findViewById(R.id.recycler_view_diary);
            expand_button = itemView.findViewById(R.id.expand_button);
            cardview_diary = itemView.findViewById(R.id.cardview_diary);


            // Expand More/Less Button
            expand_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerView.getVisibility() == View.GONE)
                    {
                        recyclerView.setVisibility(View.VISIBLE);
                        notifyItemChanged(getAdapterPosition());

                        // Expand Button Animation
                        RotateAnimation rotate = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF, 0.5f,          Animation.RELATIVE_TO_SELF, 0.5f);
                        rotate.setDuration(200);
                        rotate.setInterpolator(new LinearInterpolator());
                        expand_button.startAnimation(rotate);
                        expand_button.setImageResource(R.drawable.ic_expand_less_24px);
                    }
                    else if(recyclerView.getVisibility() == View.VISIBLE)
                    {
                        recyclerView.setVisibility(View.GONE);

                        // Expand Button Animation
                        RotateAnimation rotate = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f,          Animation.RELATIVE_TO_SELF, 0.5f);
                        rotate.setDuration(200);
                        rotate.setInterpolator(new LinearInterpolator());
                        expand_button.startAnimation(rotate);
                        expand_button.setImageResource(R.drawable.ic_expand_more_24px);
                    }
                }
            });

        }
    }
}
