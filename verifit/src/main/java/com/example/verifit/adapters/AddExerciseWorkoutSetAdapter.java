package com.example.verifit.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.verifit.R;
import com.example.verifit.model.WorkoutSet;
import com.example.verifit.ui.AddExerciseActivity;

import java.util.ArrayList;

// Adapter for WorkoutSet Class
public class AddExerciseWorkoutSetAdapter extends RecyclerView.Adapter<AddExerciseWorkoutSetAdapter.MyViewHolder> {

    Context ct;
    ArrayList<WorkoutSet> Workout_Sets;

    public AddExerciseWorkoutSetAdapter(Context ct, ArrayList<WorkoutSet> Workout_Sets)
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
        // Double -> String
        holder.tv_weight.setText(Workout_Sets.get(position).getWeight().toString());

        // Double -> Integer -> String
        holder.tv_reps.setText(String.valueOf(Workout_Sets.get(position).getReps().intValue()));

        // Updates Edit Texts and Buttons when clicked
        holder.cardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                updateView(position);
            }
        });


        holder.cardView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                AddExerciseActivity.Clicked_Set = position;
                showSetPopupMenu(holder, view, position);
                return true;
            }
        });

    }

    // Notify AddExerciseActivity of the clicked position
    public void updateView(int position)
    {
        AddExerciseActivity.bt_clear.setText("Clear");
        AddExerciseActivity.bt_save.setText("Save");

        // Updates the position of the user selected set in AddExerciseActivity
        AddExerciseActivity.Clicked_Set = position;

        // Updates ets buttons and sets in AddExerciseActivity
        AddExerciseActivity.UpdateViewOnClick();
    }

    // To Do: Implement Delete functionality
    private void showSetPopupMenu(AddExerciseWorkoutSetAdapter.MyViewHolder holder, View view, int position)
    {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view, Gravity.NO_GRAVITY, R.attr.actionOverflowMenuStyle, 0);

        popupMenu.inflate(R.menu.set_add_exercise_activity_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {

                if(item.getItemId() == R.id.delete)
                {
                    System.out.println("Delete Set Clicked");
                    AddExerciseActivity.deleteSet(view.getContext());
                }
                else if(item.getItemId() == R.id.edit)
                {
                    System.out.println("Edit Set Clicked");

                    // To Do:
                    // Keep Set highlighted in Recyclerview
                    // Save ---> Update and change color
                    // Notify data changed in adapter

                    AddExerciseActivity.editSet(holder, view, position);
                }

                return false;
            }
        });
        popupMenu.show();
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

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);

            tv_reps = itemView.findViewById(R.id.set_reps);
            tv_weight = itemView.findViewById(R.id.tv_date);
            cardView = itemView.findViewById(R.id.cardview_set);
        }
    }
}
