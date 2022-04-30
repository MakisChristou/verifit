package com.example.verifit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thegrizzlylabs.sardineandroid.DavResource;

import java.util.ArrayList;
import java.util.List;


// Adapter for WorkoutExercise Class
public class WebdavAdapter extends RecyclerView.Adapter<WebdavAdapter.MyViewHolder> {

    Context ct;
    List<DavResource> Resources;

    public WebdavAdapter(Context ct, List<DavResource> Resources)
    {
        this.ct = ct;
        this.Resources = Resources;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(this.ct);
        View view = inflater.inflate(R.layout.webdav_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {

        holder.tv_name.setText(Resources.get(position).getName());

        Long kilobytes =  Resources.get(position).getContentLength() / 1000;
        holder.tv_size.setText(kilobytes.toString());
        holder.tv_date.setText(Resources.get(position).getCreation().toString());

        holder.cardview_webdav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(ct,Resources.get(position).getName() +" clicked!", Toast.LENGTH_LONG).show();

                // Load Webdav url and credentials
                SharedPreferences sharedPreferences = ct.getSharedPreferences("shared preferences", 0); // MODE_PRIVATE = 0
                String webdav_url = sharedPreferences.getString("webdav_url", "");
                String webdav_username = sharedPreferences.getString("webdav_username", "");
                String webdav_password = sharedPreferences.getString("webdav_password", "");
                String webdav_resource = Resources.get(position).getName();

                // Import remote file
                MainActivity.importWebDav(ct, webdav_url, webdav_username, webdav_password, webdav_resource);
            }
        });

//        // Change TextView text
//        holder.tv_exercise_name.setText(Exercises.get(position).getExercise());
//
//        // Recycler View Stuff
//        // Change RecyclerView items
//        WorkoutSetAdapter workoutSetAdapter = new WorkoutSetAdapter(ct, Exercises.get(position).getSets());
//        holder.recyclerView.setAdapter(workoutSetAdapter);
//        holder.recyclerView.setLayoutManager(new LinearLayoutManager(ct));
//
//
//        holder.editButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                startIntent(position);
//            }
//        });
//
//        // Colorize exercise icon accordingly
//        setCategoryIconTint(holder,Exercises.get(position).getExercise());
    }


//    // Go to Add Exercise
//    public void startIntent(int position)
//    {
//        Intent in = new Intent(ct,AddExerciseActivity.class);
//        in.putExtra("exercise",Resources.get(position).getName());
//        ct.startActivity(in);
//    }


    @Override
    public int getItemCount()
    {
        return this.Resources.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        TextView tv_name;
        TextView tv_date;
        TextView tv_size;
        RecyclerView recyclerView;
        View blue_line;
        CardView cardview_webdav;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_size = itemView.findViewById(R.id.tv_size);

            recyclerView = itemView.findViewById(R.id.recyclerView_Webdav);

            blue_line = itemView.findViewById(R.id.blue_line_webdav);

            cardview_webdav = itemView.findViewById(R.id.cardview_webdav);

        }
    }
}
