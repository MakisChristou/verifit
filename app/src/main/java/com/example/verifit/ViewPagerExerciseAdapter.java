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
import java.util.ArrayList;


// Adapter for WorkoutExercise Class
public class ViewPagerExerciseAdapter extends RecyclerView.Adapter<ViewPagerExerciseAdapter.MyViewHolder> {

    Context ct;
    ArrayList<WorkoutExercise> Exercises;

    public ViewPagerExerciseAdapter(Context ct, ArrayList<WorkoutExercise> Exercises)
    {
        this.ct = ct;
        this.Exercises = new ArrayList<>(Exercises);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(this.ct);
        View view = inflater.inflate(R.layout.view_pager_exercise_row,parent,false);
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



        holder.cardview_exercise2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                System.out.println("Clicked Card!");
                Intent in = new Intent(ct,AddExerciseActivity.class);
                in.putExtra("exercise",Exercises.get(position).getExercise());
                MainActivity.date_selected = Exercises.get(position).getDate();
                ct.startActivity(in);
            }
        });

    }

    // Blatant copy of Fitnotes but ohh well ;)
    public void showExerciseDialog(int position)
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
        View blue_line;
        CardView cardview_exercise2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_exercise_name = itemView.findViewById(R.id.tv_date);
            recyclerView = itemView.findViewById(R.id.recycler_view_day);
            blue_line = itemView.findViewById(R.id.blue_line);
            cardview_exercise2 = itemView.findViewById(R.id.cardview_exercise_history);
        }
    }
}
