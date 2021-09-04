package com.example.verifit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;


// Adapter for Exercise Class
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.MyViewHolder> implements Filterable {

    Context ct;
    ArrayList<Exercise> Exercises;
    ArrayList<Exercise> Exercises_Full; // for search functionality

    // Adapter Constructor 7 minute mark
    public ExerciseAdapter(Context ct, ArrayList<Exercise> Exercises)
    {
        this.ct = ct;
        this.Exercises = new ArrayList<>(Exercises); // If you this is changed to: this.Exercises = Exercises; then on search diary activity will not recognize known exercises
        this.Exercises_Full = new ArrayList<>(Exercises);
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
        holder.tv_exercise_bodypart.setText(Exercises.get(position).getBodyPart());

        // Goto Add Exercise Activity
        holder.cardview_exercise_1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent in = new Intent(ct,AddExerciseActivity.class);
                in.putExtra("exercise",holder.tv_exercise_name.getText().toString());
                ct.startActivity(in);
            }
        });


//        // Open Contextual Action Mode Menu
//        holder.cardview_exercise_1.setOnLongClickListener(new View.OnLongClickListener()
//        {
//            @Override
//            public boolean onLongClick(View view)
//            {
//                // Delete Exercise from MainActivity Data Structures
//                MainActivity.deleteExercise(Exercises.get(position).getName());
//
//                // Delete Exercise from Adapter's local data structure
//                deleteExercise(Exercises.get(position).getName());
//
//
//                // Save Results
//                MainActivity.saveKnownExerciseData(ct);
//                MainActivity.saveWorkoutData(ct);
//
//
//                return true;
//            }
//        });

    }


    public void deleteExercise(String exercise_name)
    {
        for (Iterator<Exercise> exerciseIterator = this.Exercises.iterator(); exerciseIterator.hasNext(); )
        {
            Exercise current_exercise = exerciseIterator.next();

            if(current_exercise.getName().equals(exercise_name))
            {
                exerciseIterator.remove();
                notifyDataSetChanged();
            }
        }
    }


    @Override
    public int getItemCount()
    {
        return this.Exercises.size();
    }


    // Implement Filterable Methods
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {

        // Return our filtered results
        @Override
        protected FilterResults performFiltering(CharSequence charSequence)
        {
            ArrayList<Exercise> Exercises_Filtered = new ArrayList<Exercise>();

            // Don't filter anything
            if(charSequence == null || charSequence.length() == 0)
            {
                Exercises_Filtered.addAll(Exercises_Full);
            }
            // Something was typed so filter results
            else
            {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(int i = 0; i < Exercises.size(); i++)
                {
                    // If search patterns is contained in exercise name then show it
                    if(Exercises.get(i).getName().toLowerCase().contains(filterPattern))
                    {
                        Exercises_Filtered.add(Exercises.get(i));
                    }
                }
            }

            // Return results object to the publishResults method below
            FilterResults results = new FilterResults();
            results.values = Exercises_Filtered;
            return results;
        }

        // Publish Results to the UI thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults)
        {
            Exercises.clear();
            Exercises.addAll((ArrayList)filterResults.values); // Add only the filtered items
            notifyDataSetChanged(); // Update Recycler View
        }
    };

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnLongClickListener, PopupMenu.OnMenuItemClickListener, AdapterView.OnItemSelectedListener {
        TextView tv_exercise_name;
        TextView tv_exercise_bodypart;
        CardView cardview_exercise_1;
        String exercise_name;
        String new_exercise_category;
        int current_exercise_category_position;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tv_exercise_name = itemView.findViewById(R.id.tv_date);
            tv_exercise_bodypart = itemView.findViewById(R.id.exercise_bodypart);
            cardview_exercise_1 = itemView.findViewById(R.id.cardview_exercise_1);

            // For PopUp Menu
            cardview_exercise_1.setOnLongClickListener(this);
        }


        // Triggered when someone long presses on cardview_exercise_1
        @Override
        public boolean onLongClick(View view)
        {
            showPopupMenu(view);
            return true;
        }


        private void showPopupMenu(View view)
        {
            PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
            popupMenu.inflate(R.menu.exercises_activity_floating_context_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }


        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            // Edit Exercise
            if(item.getItemId() == R.id.edit)
            {
                int position = getAdapterPosition();
                System.out.println("Edit");

                // Prepare to show exercise dialog box
                LayoutInflater inflater = LayoutInflater.from(ct);
                View view = inflater.inflate(R.layout.edit_exercise_dialog,null);
                AlertDialog alertDialog = new AlertDialog.Builder(ct).setView(view).create();

                // Find views
                Button bt_save = view.findViewById(R.id.bt_save);
                Button bt_cancel = view.findViewById(R.id.bt_cancel);
                EditText et_exercise_name = view.findViewById(R.id.et_exercise_name);
                Spinner spinner = view.findViewById(R.id.spinner);


                // Setup Spinner Stuff
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ct,R.array.Categories, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(this);

                // Get Array from xml
                String[] listValue;
                listValue = ct.getResources().getStringArray(R.array.Categories);

                // Find Current Category position
                for(int i = 0; i < listValue.length; i++)
                {
                    if(listValue[i].equals(Exercises.get(position).getBodyPart()))
                    {
                        current_exercise_category_position = i;
                        System.out.println(listValue[i]);
                    }
                }

                // Set edit text and spinner initial values
                exercise_name = Exercises.get(position).getName();
                et_exercise_name.setText(exercise_name);
                spinner.setSelection(current_exercise_category_position);

                // Dismiss Dialog Box
                bt_cancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        alertDialog.dismiss();
                    }
                });

                // Replace all instances with new exercise
                bt_save.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        // Get User defined new name
                        String new_exercise_name = et_exercise_name.getText().toString();

                        // Call Edit Exercise if user gave reasonable input
                        if(new_exercise_category != null && !new_exercise_category.isEmpty() && new_exercise_category.length() > 0 && new_exercise_name != null && !new_exercise_name.isEmpty() && new_exercise_name.length() > 0)
                        {
                            System.out.println(new_exercise_category + " " + et_exercise_name.getText().toString());
                            MainActivity.editExercise(exercise_name,new_exercise_name,new_exercise_category);
                            MainActivity.saveWorkoutData(ct);
                            MainActivity.saveKnownExerciseData(ct);
                            notifyDataSetChanged();
                            alertDialog.dismiss();
                        }

                        // Tell user to stop fucking around
                        else
                        {
                            Toast.makeText(ct,"Please choose an apropriate name", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alertDialog.show();
                return true;
            }

            // Delete Exercise
            else if(item.getItemId() == R.id.delete)
            {
                int position = getAdapterPosition();

                // Prepare to show exercise dialog box
                LayoutInflater inflater = LayoutInflater.from(ct);
                View view = inflater.inflate(R.layout.delete_exercise_dialog,null);
                AlertDialog alertDialog = new AlertDialog.Builder(ct).setView(view).create();


                Button bt_yes = view.findViewById(R.id.bt_yes3);
                Button bt_no = view.findViewById(R.id.bt_no3);


                bt_no.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        alertDialog.dismiss();
                    }
                });

                bt_yes.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        // Delete Exercise from MainActivity Data Structures
                        MainActivity.deleteExercise(Exercises.get(position).getName());

                        // Delete Exercise from Adapter's local data structure
                        deleteExercise(Exercises.get(position).getName());

                        // Save Results
                        MainActivity.saveKnownExerciseData(ct);
                        MainActivity.saveWorkoutData(ct);

                        alertDialog.dismiss();
                    }
                });


                alertDialog.show();
                return true;
            }


            return false;
        }

        // Spinner On Item Selected Methods
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
        {
            new_exercise_category = adapterView.getItemAtPosition(i).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView)
        {
            new_exercise_category = "";
        }
    }
}
