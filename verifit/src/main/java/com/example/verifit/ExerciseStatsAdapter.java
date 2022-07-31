package com.example.verifit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;



// Adapter for WorkoutExercise Class
public class ExerciseStatsAdapter extends RecyclerView.Adapter<ExerciseStatsAdapter.MyViewHolder> {

    Context ct;
    ArrayList<ExercisePersonalStats> exercisePersonalStats;


    public ExerciseStatsAdapter(Context ct, ArrayList<ExercisePersonalStats> exercisePersonalStats)
    {
        this.ct = ct;
        this.exercisePersonalStats = new ArrayList<ExercisePersonalStats>(exercisePersonalStats);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(this.ct);
        View view = inflater.inflate(R.layout.exercise_stats_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        // Get exercise name
        String exercise_name = exercisePersonalStats.get(position).getExerciseName();
        String exercise_category = exercisePersonalStats.get(position).getExerciseCategory();
        String max_weight = exercisePersonalStats.get(position).getMaxWeight().toString();
        String max_reps = exercisePersonalStats.get(position).getMaxReps().toString();
        String max_set_volume = exercisePersonalStats.get(position).getMaxSetVolume().toString();
        String max_volume = exercisePersonalStats.get(position).getMaxVolume().toString();
        String estimated_1rm = exercisePersonalStats.get(position).getEstimated1RM().toString();
        String actual_1rm = exercisePersonalStats.get(position).getActual1RM().toString();
        Boolean isFavorite = exercisePersonalStats.get(position).getFavorite();


        holder.tv_exercise_name.setText(exercise_name);
        holder.tv_exercise_category.setText(exercise_category);
        holder.tv_maxweight.setText(max_weight + " kg");
        holder.tv_maxreps.setText(max_reps + " kg");
        holder.tv_maxsetvolume.setText(max_set_volume + " kg");
        holder.tv_maxvolume.setText(max_volume + " kg");
        holder.tv_estimated_1rm.setText(estimated_1rm + " kg");

        if(actual_1rm.equals("0.0"))
        {
            holder.tv_actual_1rm.setText("n/a");
        }
        else
        {
            holder.tv_actual_1rm.setText(actual_1rm + " kg");
        }


        // By default cards are expanded
        holder.moreButton.setImageResource(R.drawable.ic_expand_more_24px);
        holder.cardview_stats.setVisibility(View.VISIBLE);
        holder.blue_line.setVisibility(View.VISIBLE);


        holder.cardview_viewpager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Shrink Card
                if(holder.cardview_stats.getVisibility() == View.VISIBLE)
                {
                    holder.moreButton.setImageResource(R.drawable.ic_expand_less_24px);
                    holder.cardview_stats.setVisibility(View.GONE);
                    holder.blue_line.setVisibility(View.INVISIBLE);
                }
                // Expand Card
                else
                {
                    holder.moreButton.setImageResource(R.drawable.ic_expand_more_24px);
                    holder.cardview_stats.setVisibility(View.VISIBLE);
                    holder.blue_line.setVisibility(View.VISIBLE);
                }
            }
        });


        holder.cardview_viewpager.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view)
            {
                System.out.println("Long Clicked on " + exercise_name);
                showPopupMenu(holder, view, position);
                return true;
            }
        });

        setCategoryIconTint(holder, exercise_name, isFavorite, position);


    }

    private void showPopupMenu(ExerciseStatsAdapter.MyViewHolder holder, View view, int position)
    {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view, Gravity.NO_GRAVITY, R.attr.actionOverflowMenuStyle, 0);

        popupMenu.inflate(R.menu.exercise_personal_record_floating_context_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {

                // To Do: Edit Remote Webdav Resource
                if(item.getItemId() == R.id.charts)
                {
                    System.out.println("Charts Clicked");
                }

                // Delete Remote Webdav Resource
                else if(item.getItemId() == R.id.favorite)
                {
                    System.out.println("Favorite Clicked");

                    String exercise_name = exercisePersonalStats.get(position).getExerciseName();

                    Boolean isFavorite = MainActivity.isExerciseFavorite(exercise_name);


                    MainActivity.setFavoriteExercise(exercise_name, !isFavorite);


                    isFavorite = MainActivity.isExerciseFavorite(exercise_name);


                    PersonalRecordsActivity.calculatePersonalRecords();


                    setCategoryIconTint(holder, exercise_name, isFavorite, position);



                }
                return false;
            }
        });
        popupMenu.show();
    }

    // Simple
    public void setCategoryIconTint(ExerciseStatsAdapter.MyViewHolder holder, String exercise_name, Boolean isFavorite, int position)
    {
        String exercise_category = MainActivity.getExerciseCategory(exercise_name);

        if(!isFavorite)
        {
            holder.favoriteButton.setImageResource(R.drawable.ic_brightness_1_fill1_wght400_grad0_opsz20);

            // Set Category Color
            if(exercise_category.equals("Shoulders"))
            {
                holder.favoriteButton.setColorFilter(Color.argb(255, 	0, 116, 189)); // Primary Color
            }
            else if(exercise_category.equals("Back"))
            {
                holder.favoriteButton.setColorFilter(Color.argb(255, 40, 176, 192));
            }
            else if(exercise_category.equals("Chest"))
            {
                holder.favoriteButton.setColorFilter(Color.argb(255, 	92, 88, 157));
            }
            else if(exercise_category.equals("Biceps"))
            {
                holder.favoriteButton.setColorFilter(Color.argb(255, 	255, 50, 50));
            }
            else if(exercise_category.equals("Triceps"))
            {
                holder.favoriteButton.setColorFilter(Color.argb(255,    204, 154, 0));
            }
            else if(exercise_category.equals("Legs"))
            {
                holder.favoriteButton.setColorFilter(Color.argb(255, 	212, 	25, 97));
            }
            else if(exercise_category.equals("Abs"))
            {
                holder.favoriteButton.setColorFilter(Color.argb(255, 	255, 153, 171));
            }
            else
            {
                holder.favoriteButton.setColorFilter(Color.argb(255, 	52, 58, 64)); // Grey AF
            }
        }
        else
        {
            holder.favoriteButton.setImageResource(R.drawable.ic_star_fill0_wght400_grad0_opsz24);
            holder.favoriteButton.setColorFilter(Color.argb(255, 0xff, 0xd6, 0x24)); // Yellowish
        }
    }

    @Override
    public int getItemCount()
    {
        return this.exercisePersonalStats.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        TextView tv_exercise_name;
        TextView tv_exercise_category;
        TextView tv_maxweight;
        TextView tv_maxreps;
        TextView tv_maxsetvolume;
        TextView tv_maxvolume;
        TextView tv_estimated_1rm;
        TextView tv_actual_1rm;
        CardView cardview_viewpager;
        CardView cardview_stats;
        ImageButton favoriteButton;
        ImageButton moreButton;
        View blue_line;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_exercise_name = itemView.findViewById(R.id.tv_exercise_name);
            tv_exercise_category = itemView.findViewById(R.id.exerciseCategory);
            tv_maxweight = itemView.findViewById(R.id.tv_maxweight);
            tv_maxreps = itemView.findViewById(R.id.tv_maxreps);
            tv_maxsetvolume = itemView.findViewById(R.id.tv_max_set_volume);
            tv_maxvolume = itemView.findViewById(R.id.tv_max_volume);
            tv_estimated_1rm = itemView.findViewById(R.id.tv_estimated_1rm);
            tv_actual_1rm = itemView.findViewById(R.id.tv_actual_1rm);
            cardview_viewpager = itemView.findViewById(R.id.cardview_viewpager);
            cardview_stats = itemView.findViewById(R.id.cardview_stats);
            favoriteButton  = itemView.findViewById(R.id.favoriteButton);
            moreButton = itemView.findViewById(R.id.moreButton);
            blue_line = itemView.findViewById(R.id.blue_line);
        }
    }
}
