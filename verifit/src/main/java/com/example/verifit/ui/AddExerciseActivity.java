package com.example.verifit.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.verifit.KeyboardHider;
import com.example.verifit.LoadingDialog;
import com.example.verifit.MonthXAxisFormatter;
import com.example.verifit.SnackBarWithMessage;
import com.example.verifit.adapters.AddExerciseWorkoutSetAdapter;
import com.example.verifit.adapters.ExerciseHistoryExerciseAdapter;
import com.example.verifit.R;
import com.example.verifit.model.WorkoutDay;
import com.example.verifit.model.WorkoutExercise;
import com.example.verifit.model.WorkoutSet;
import com.example.verifit.verifitrs.WorkoutSetsApi;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddExerciseActivity extends AppCompatActivity {

    // Helper Data Structures
    public RecyclerView recyclerView;
    public static String exercise_name;
    public static ArrayList<WorkoutSet> Todays_Exercise_Sets = new ArrayList<WorkoutSet>();
    public static AddExerciseWorkoutSetAdapter workoutSetAdapter2;
    public static int Clicked_Set = 0;
    public static Boolean isEditMode = false;

    // Add Exercise Activity Specifics
    public static EditText et_reps;
    public static EditText et_weight;
    public ImageButton plus_reps;
    public ImageButton minus_reps;
    public ImageButton plus_weight;
    public ImageButton minus_weight;
    public static Button bt_save;
    public static Button bt_clear;

    // For Alarm
    public long START_TIME_IN_MILLIS = 180000;
    public CountDownTimer countDownTimer;
    public boolean TimerRunning;
    public long TimeLeftInMillis = START_TIME_IN_MILLIS;

    // Timer Dialog Components
    public EditText et_seconds;
    public ImageButton minus_seconds;
    public ImageButton plus_seconds;
    public Button bt_start;
    public Button bt_reset;

    private AlertDialog currentDialog = null;



    // Comment Items
    Button bt_save_comment;
    Button bt_clear_comment;
    EditText et_exercise_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        // find views
        et_reps = findViewById(R.id.et_reps);
        et_weight = findViewById(R.id.et_seconds);
        plus_reps = findViewById(R.id.plus_reps);
        minus_reps = findViewById(R.id.minus_reps);
        plus_weight = findViewById(R.id.plus_weight);
        minus_weight = findViewById(R.id.minus_weight);
        bt_clear = findViewById(R.id.bt_clear);
        bt_save = findViewById(R.id.bt_login_signup);

        // Self Explanatory I guess
        initActivity();

        // Self Explanatory I guess
        initrecyclerView();

        // User can modify data structures, possible race condition, thus temporary disable autobackup
        MainActivity.inAddExerciseActivity = true;

        com.example.verifit.SharedPreferences sharedPreferences = new com.example.verifit.SharedPreferences(getApplicationContext());
        sharedPreferences.save("true", "inAddExerciseActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // User can modify data structures, possible race condition, thus temporary disable autobackup
        MainActivity.inAddExerciseActivity = true;

        com.example.verifit.SharedPreferences sharedPreferences = new com.example.verifit.SharedPreferences(getApplicationContext());
        sharedPreferences.save("true", "inAddExerciseActivity");
    }

    // Save / Update
    public void clickSave(View view)
    {
        KeyboardHider keyboardHider = new KeyboardHider(AddExerciseActivity.this);
        keyboardHider.hideKeyboard();

        // Let backup service know that something has changed
        MainActivity.autoBackupRequired = true;

        com.example.verifit.SharedPreferences sharedPreferences = new com.example.verifit.SharedPreferences(getApplicationContext());
        sharedPreferences.save("true", "autoBackupRequired");

        // Save Functionality
        if(!isEditMode)
        {
            if(et_weight.getText().toString().isEmpty() || et_reps.getText().toString().isEmpty())
            {
                Toast.makeText(getApplicationContext(),"Please write Weight and Reps",Toast.LENGTH_SHORT).show();
            }
            else
            {
                // Get user sets && reps
                Double reps = Double.parseDouble(et_reps.getText().toString());
                Double weight = Double.parseDouble(et_weight.getText().toString());

                // Create New Set Object
                WorkoutSet workoutSet = new WorkoutSet(MainActivity.dateSelected,exercise_name, MainActivity.dataStorage.getExerciseCategory(exercise_name),reps,weight);

                // Ignore wrong input
                if(reps == 0 || weight == 0 || reps < 0 || weight < 0)
                {
                    Toast.makeText(getApplicationContext(),"Please write correct Weight and Reps",Toast.LENGTH_SHORT).show();
                }
                // Save set
                else
                {
                    // Find if workout day already exists
                    int position = MainActivity.dataStorage.getDayPosition(MainActivity.dateSelected);

                    // If workout day exists
                    if(position >= 0)
                    {
                        // Find comment of that workout day/exercise
                        WorkoutDay workoutDay = MainActivity.dataStorage.getWorkoutDays().get(position);

                        for(int i = 0; i < workoutDay.getSets().size(); i++)
                        {
                            WorkoutSet workoutSet1 = workoutDay.getSets().get(i);

                            if(workoutSet1.getExerciseName().equals(exercise_name))
                            {
                                String exerciseComment = workoutSet1.getComment();
                                if(exerciseComment == "null" || exerciseComment == null)
                                {
                                    workoutSet.setComment("");
                                }
                                else
                                {
                                    workoutSet.setComment(exerciseComment);
                                }
                            }
                        }


                        // Offline
                        if(sharedPreferences.isOfflineMode())
                        {
                            addSetExistingWorkoutDay(workoutSet, position);
                        }
                        else
                        {
                            final LoadingDialog loadingDialog = new LoadingDialog(AddExerciseActivity.this);
                            loadingDialog.loadingAlertDialog();

                            WorkoutSetsApi workoutSetsApi = new WorkoutSetsApi(getApplicationContext(), getString(R.string.API_ENDPOINT));
                            workoutSetsApi.postWorkoutSet(workoutSet, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    loadingDialog.dismissDialog();
                                    showSnackbarMessage(e.toString());
                                }

                                @Override
                                public void onResponse(Call call, okhttp3.Response response) throws IOException {

                                    loadingDialog.dismissDialog();

                                    if (200 == response.code())
                                    {
                                        Integer set_id = getSetIdFromResponse(response);
                                        workoutSet.setId(set_id);
                                        addSetExistingWorkoutDay(workoutSet, position);
                                    }
                                    else
                                    {
                                        showSnackbarMessage(response.message().toString());
                                    }
                                }
                            });
                        }
                    }
                    // If not construct new workout day
                    else
                    {
                        // Offline
                        if(sharedPreferences.isOfflineMode())
                        {
                            addSetNewWorkoutDay(workoutSet);
                        }
                        else
                        {
                            final LoadingDialog loadingDialog = new LoadingDialog(AddExerciseActivity.this);
                            loadingDialog.loadingAlertDialog();

                            WorkoutSetsApi workoutSetsApi = new WorkoutSetsApi(getApplicationContext(), getString(R.string.API_ENDPOINT));
                            workoutSetsApi.postWorkoutSet(workoutSet, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    // Show error
                                    loadingDialog.dismissDialog();
                                    showSnackbarMessage(e.toString());
                                }

                                @Override
                                public void onResponse(Call call, okhttp3.Response response) throws IOException {

                                    loadingDialog.dismissDialog();

                                    if (200 == response.code())
                                    {
                                        Integer set_id = getSetIdFromResponse(response);
                                        workoutSet.setId(set_id);
                                        addSetNewWorkoutDay(workoutSet);
                                    }
                                    else
                                    {
                                        runOnUiThread(()->{
                                            showSnackbarMessage(response.message());
                                        });
                                    }
                                }
                            });
                        }
                    }
                }
            }

            // Fixed Myria induced bug
            AddExerciseActivity.Clicked_Set = Todays_Exercise_Sets.size()-1;
        }
        // Update Functionality
        else
        {
            WorkoutSet to_be_updated_set = Todays_Exercise_Sets.get(Clicked_Set);

            to_be_updated_set.getDate();
            to_be_updated_set.getExerciseName();

            // Find the set in main data structure and delete it
            for (int i = 0; i < MainActivity.dataStorage.getWorkoutDays().size(); i++)
            {
                for (int j = 0; j < MainActivity.dataStorage.getWorkoutDays().get(i).getSets().size(); j++)
                {
                    if (MainActivity.dataStorage.getWorkoutDays().get(i).getSets().get(j).equals(to_be_updated_set))
                    {
                        final int finalI = i;
                        final int finalJ = j;

                        Double reps = Double.parseDouble(String.valueOf(et_reps.getText()));
                        Double weight = Double.parseDouble(String.valueOf(et_weight.getText()));

                        // Create temp set
                        WorkoutSet set = new WorkoutSet();
                        set.setComment(to_be_updated_set.getComment());
                        set.setExerciseName(to_be_updated_set.getExerciseName());
                        set.setCategory(to_be_updated_set.getCategory());
                        set.setId(to_be_updated_set.getId());
                        set.setReps(reps);
                        set.setWeight(weight);

                        if(sharedPreferences.isOfflineMode())
                        {
                            updateSet(finalI, finalJ, reps, weight);
                        }
                        else
                        {
                            final LoadingDialog loadingDialog = new LoadingDialog(AddExerciseActivity.this);
                            loadingDialog.loadingAlertDialog();

                            WorkoutSetsApi workoutSetsApi = new WorkoutSetsApi(getApplicationContext(), getString(R.string.API_ENDPOINT));
                            workoutSetsApi.updateWorkoutSet(set, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e)
                                {
                                    loadingDialog.dismissDialog();
                                    showSnackbarMessage(e.toString());
                                }

                                @Override
                                public void onResponse(Call call, okhttp3.Response response) throws IOException {

                                    loadingDialog.dismissDialog();

                                    if (200 == response.code())
                                    {
                                        updateSet(finalI, finalJ, reps, weight);
                                    }
                                    else
                                    {
                                        showSnackbarMessage(response.message().toString());
                                    }
                                }
                            });
                        }
                        break;
                    }
                }
            }
        }

    }


    public void showSnackbarMessage(String message)
    {
        runOnUiThread(() -> {
            SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(AddExerciseActivity.this);
            snackBarWithMessage.showSnackbar(message);
        });
    }

    public void updateSet(Integer finalI, Integer finalJ, Double reps, Double weight)
    {
        MainActivity.dataStorage.getWorkoutDays().get(finalI).getSets().get(finalJ).setReps(reps);
        MainActivity.dataStorage.getWorkoutDays().get(finalI).getSets().get(finalJ).setWeight(weight);

        // Manually update data because of bad design choices
        MainActivity.dataStorage.getWorkoutDays().get(finalI).UpdateData();

        // Let the user know I guess
        runOnUiThread(() -> {
            showSnackbarMessage("Set Updated");
            updateTodaysExercises();
        });


        bt_save.setText("Save");
        bt_clear.setText("Clear");
        AddExerciseActivity.isEditMode = false;
    }

    public void addSetExistingWorkoutDay(WorkoutSet workoutSet, Integer position)
    {
        MainActivity.dataStorage.getWorkoutDays().get(position).addSet(workoutSet);
        updateViewAndShowMessage();
    }

    public void addSetNewWorkoutDay(WorkoutSet workoutSet)
    {
        WorkoutDay workoutDay = new WorkoutDay();
        workoutDay.addSet(workoutSet);
        MainActivity.dataStorage.getWorkoutDays().add(workoutDay);
        updateViewAndShowMessage();
    }


    public void updateViewAndShowMessage()
    {
        runOnUiThread(()->{
            updateTodaysExercises();
            showSnackbarMessage("Set Added");
        });
    }

    public Integer getSetIdFromResponse(okhttp3.Response response) throws IOException
    {
        String jsonString = response.body().string();
        Gson gson = new Gson();
        Type listType = new TypeToken<Integer>() {}.getType();
        Integer set_id = gson.fromJson(jsonString, listType);

        return  set_id;
    }

    // Clear
    public void clickClear(View view)
    {
        bt_clear.setText("Clear");
        et_reps.setText("");
        et_weight.setText("");
    }

    public static void deleteSet(Context ct)
    {
        // Let backup service know that something has changed
        MainActivity.autoBackupRequired = true;
        com.example.verifit.SharedPreferences sharedPreferences = new com.example.verifit.SharedPreferences(ct);
        sharedPreferences.save("true", "autoBackupRequired");

        // Show confirmation dialog  box
        // Prepare to show exercise dialog box
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view1 = inflater.inflate(R.layout.delete_set_dialog,null);
        AlertDialog alertDialog = new AlertDialog.Builder(ct).setView(view1).create();

        Button bt_yes = view1.findViewById(R.id.bt_yes3);
        Button bt_no = view1.findViewById(R.id.bt_no3);

        // Dismiss dialog box
        bt_no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alertDialog.dismiss();
            }
        });

        // Actually Delete set and update local data structure
        bt_yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Get soon to be deleted set
                WorkoutSet to_be_removed_set = Todays_Exercise_Sets.get(Clicked_Set);

                // Find the set in main data structure and delete it
                for(int i = 0; i < MainActivity.dataStorage.getWorkoutDays().size(); i++)
                {
                    if(MainActivity.dataStorage.getWorkoutDays().get(i).getSets().contains(to_be_removed_set))
                    {
                        final int finalI = i;

                        if(sharedPreferences.isOfflineMode())
                        {
                            deleteSetLogic(ct, finalI, to_be_removed_set);
                            alertDialog.dismiss();
                        }
                        else
                        {
                            final LoadingDialog loadingDialog = new LoadingDialog((Activity) ct);
                            loadingDialog.loadingAlertDialog();

                            WorkoutSetsApi workoutSetsApi = new WorkoutSetsApi(ct, ct.getString(R.string.API_ENDPOINT));
                            workoutSetsApi.deleteWorkoutSet(to_be_removed_set, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    // Show error
                                    ((Activity) ct).runOnUiThread(() -> {
                                        loadingDialog.dismissDialog();
                                        alertDialog.dismiss();
                                        SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(((Activity) ct));
                                        snackBarWithMessage.showSnackbar("Can't connect to server");
                                    });
                                }

                                @Override
                                public void onResponse(Call call, okhttp3.Response response) throws IOException {

                                    loadingDialog.dismissDialog();
                                    alertDialog.dismiss();

                                    if (200 == response.code())
                                    {
                                        deleteSetLogic(ct, finalI, to_be_removed_set);
                                    }
                                    else
                                    {
                                        ((Activity) ct).runOnUiThread(() -> {
                                            SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(((Activity) ct));
                                            snackBarWithMessage.showSnackbar(response.message().toString());
                                        });
                                    }
                                }
                            });
                        }
                        break;
                    }
                }
            }
        });

        // Show delete confirmation dialog box
        alertDialog.show();
    }

    public static void deleteSetLogic(Context ct, int finalI, WorkoutSet to_be_removed_set)
    {
        MainActivity.dataStorage.getWorkoutDays().get(finalI).removeSet(to_be_removed_set);

        // Cleanup potential days with 0 sets
        for(int i = 0; i < MainActivity.dataStorage.getWorkoutDays().size(); i++)
        {
            if(MainActivity.dataStorage.getWorkoutDays().get(i).getSets().size() == 0)
            {
                MainActivity.dataStorage.getWorkoutDays().remove(i);
            }
        }

        // Bug: If this last set of the day this is required
        MainActivity.dataStorage.saveWorkoutData(ct);
        MainActivity.dataStorage.saveKnownExerciseData(ct);

        ((Activity) ct).runOnUiThread(() -> {
            SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(((Activity) ct));
            snackBarWithMessage.showSnackbar("Set Deleted");
            updateTodaysExercises();
        });
    }

    public static void editSet(AddExerciseWorkoutSetAdapter.MyViewHolder holder, View view, int position)
    {
        System.out.println("Edit Set on position " + position + " clicked");

        AddExerciseActivity.bt_clear.setText("Delete");
        AddExerciseActivity.bt_save.setText("Update");

        // Populate Edit Texts
        AddExerciseActivity.Clicked_Set = position;
        UpdateViewOnClick();

        AddExerciseActivity.isEditMode = true;
    }

    // Update this activity when a set is clicked
    public static void UpdateViewOnClick()
    {
        // Get selected set
        WorkoutSet clicked_set = Todays_Exercise_Sets.get(AddExerciseActivity.Clicked_Set);

        // Update Edit Texts
        et_weight.setText(clicked_set.getWeight().toString());
        et_reps.setText(String.valueOf(clicked_set.getReps().intValue()));
    }

    // Save Changes in main data structure, save data structure in shared preferences
    @Override
    protected void onStop() {
        super.onStop();
        // Sort Before Saving
        MainActivity.dataStorage.sortWorkoutDaysDate();

        // Actually Save Changes in shared preferences
        MainActivity.dataStorage.saveWorkoutData(getApplicationContext());

        // User cannot modify data structures, thus we can let service auto backup without race conditions
        MainActivity.inAddExerciseActivity = false;

        com.example.verifit.SharedPreferences sharedPreferences = new com.example.verifit.SharedPreferences(getApplicationContext());
        sharedPreferences.save("false", "inAddExerciseActivity");
    }

    // Do I even need to explain this?
    public void clickPlusWeight(View view)
    {
        if(!et_weight.getText().toString().isEmpty())
        {
            Double weight = Double.parseDouble(et_weight.getText().toString());
            weight = weight + 1;
            et_weight.setText(weight.toString());
        }
        else
        {
            et_weight.setText("1.0");
        }

    }

    // Do I even need to explain this?
    public void clickPlusReps(View view)
    {
        if(!et_reps.getText().toString().isEmpty())
        {
            int reps = Integer.parseInt(et_reps.getText().toString());
            reps = reps + 1;
            et_reps.setText(String.valueOf(reps));
        }
        else
        {
            et_reps.setText("1");
        }

    }

    // Do I even need to explain this?
    public void clickMinusWeight(View view)
    {
        if(!et_weight.getText().toString().isEmpty())
        {
            Double weight = Double.parseDouble(et_weight.getText().toString());
            weight = weight - 1;
            if(weight < 0)
            {
                weight = 0.0;
            }
            et_weight.setText(weight.toString());
        }
    }

    // Do I even need to explain this?
    public void clickMinusReps(View view)
    {
        if(!et_reps.getText().toString().isEmpty())
        {
            int reps = Integer.parseInt(et_reps.getText().toString());
            reps = reps - 1;
            if(reps < 0)
            {
                reps = 0;
            }
            et_reps.setText(String.valueOf(reps));
        }

    }

    // Handles Intent Stuff
    public void initActivity()
    {
        Intent in = getIntent();
        exercise_name = in.getStringExtra("exercise");
        getSupportActionBar().setTitle(exercise_name);
    }

    // Updates Local Data Structure
    public static void updateTodaysExercises()
    {
        // Clear since we don't want duplicates
        Todays_Exercise_Sets.clear();

        // Find Sets for a specific date and exercise
        for(int i = 0; i < MainActivity.dataStorage.getWorkoutDays().size(); i++)
        {
            // If date matches
            if(MainActivity.dataStorage.getWorkoutDays().get(i).getDate().equals(MainActivity.dateSelected))
            {
                for(int j = 0; j < MainActivity.dataStorage.getWorkoutDays().get(i).getSets().size(); j++)
                {
                    // If exercise matches
                    if(AddExerciseActivity.exercise_name.equals(MainActivity.dataStorage.getWorkoutDays().get(i).getSets().get(j).getExerciseName()))
                    {
                        Todays_Exercise_Sets.add(MainActivity.dataStorage.getWorkoutDays().get(i).getSets().get(j));
                    }
                }
            }
        }

        bt_clear.setText("Clear");

        // Update Recycler View
        AddExerciseActivity.workoutSetAdapter2.notifyDataSetChanged();
    }

    // Initialize Recycler View Object
    public void initrecyclerView()
    {
        // Clear since we don't want duplicates
        Todays_Exercise_Sets.clear();

        // Find Sets for a specific date and exercise
        for(int i = 0; i < MainActivity.dataStorage.getWorkoutDays().size(); i++)
        {
            // If date matches
            if(MainActivity.dataStorage.getWorkoutDays().get(i).getDate().equals(MainActivity.dateSelected))
            {
                for(int j = 0; j < MainActivity.dataStorage.getWorkoutDays().get(i).getSets().size(); j++)
                {
                    // If exercise matches
                    if(exercise_name.equals(MainActivity.dataStorage.getWorkoutDays().get(i).getSets().get(j).getExerciseName()))
                    {
                        Todays_Exercise_Sets.add(MainActivity.dataStorage.getWorkoutDays().get(i).getSets().get(j));
                    }
                }
            }
        }

        // Find Recycler View Object
        recyclerView = findViewById(R.id.recycler_view);
        workoutSetAdapter2 = new AddExerciseWorkoutSetAdapter(this,Todays_Exercise_Sets);
        recyclerView.setAdapter(workoutSetAdapter2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Set Edit Text values to max set volume if possible
        initEditTexts();

        bt_clear.setText("Clear");

        // Initialize Integer position or else we get a crash
        AddExerciseActivity.Clicked_Set = Todays_Exercise_Sets.size() - 1;

    }

    // Set Edit Text values to max set volume if sets exist
    public void initEditTexts()
    {
        Double max_weight = 0.0;
        int max_reps = 0;
        Double max_exercise_volume = 0.0;

        // Find Max Weight and Reps for a specific exercise
        for(int i = 0; i < MainActivity.dataStorage.getWorkoutDays().size(); i++)
        {
            for(int j = 0; j < MainActivity.dataStorage.getWorkoutDays().get(i).getSets().size(); j++)
            {
                if(MainActivity.dataStorage.getWorkoutDays().get(i).getSets().get(j).getVolume() > max_exercise_volume && MainActivity.dataStorage.getWorkoutDays().get(i).getSets().get(j).getExerciseName().equals(exercise_name))
                {
                    max_exercise_volume = MainActivity.dataStorage.getWorkoutDays().get(i).getSets().get(j).getVolume();
                    max_reps = (int)Math.round(MainActivity.dataStorage.getWorkoutDays().get(i).getSets().get(j).getReps());
                    max_weight = MainActivity.dataStorage.getWorkoutDays().get(i).getSets().get(j).getWeight();
                }
            }
        }

        // If never performed the exercise leave Edit Texts blank
        if(max_reps == 0 || max_weight == 0.0)
        {
            et_reps.setText("");
            et_weight.setText("");
        }else
        {
            et_reps.setText(String.valueOf(max_reps));
            et_weight.setText(max_weight.toString());
        }
    }

    // Menu Stuff
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_exercise_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        // Timer
        if(item.getItemId() == R.id.timer)
        {

            setupTimer();
        }

        // Exercise History
        else if(item.getItemId() == R.id.history)
        {
            return setupExerciseHistory(item);
        }

        // Exercise Stats Chart
        else if (item.getItemId() == R.id.graph) {


            // Prepare to show exercise history dialog box
            LayoutInflater inflater = LayoutInflater.from(AddExerciseActivity.this);
            View view = inflater.inflate(R.layout.exercise_graph_dialog, null);
            AlertDialog alertDialog = new AlertDialog.Builder(AddExerciseActivity.this)
                    .setView(view)
                    .create();

            // Get Chart Object
            LineChart lineChart = view.findViewById(R.id.lineChart);

            // Create Array List that will hold graph data
            ArrayList<Entry> volumeValues = new ArrayList<>();
            ArrayList<Entry> maxWeightValues = new ArrayList<>();
            ArrayList<Entry> totalRepsValues = new ArrayList<>();


            ArrayList<String> workoutDates = new ArrayList<>();
            ArrayList<String> workoutMonths = new ArrayList<>();

            int x = 0;

            // Get Exercise Volume
            for (int i = 0; i < MainActivity.dataStorage.getWorkoutDays().size(); i++) {
                for (int j = 0; j < MainActivity.dataStorage.getWorkoutDays().get(i).getExercises().size(); j++) {
                    WorkoutExercise currentExercise = MainActivity.dataStorage.getWorkoutDays().get(i).getExercises().get(j);

                    if (currentExercise.getExercise().equals(exercise_name)) {
                        volumeValues.add(new Entry(x, currentExercise.getVolume().floatValue()));

                        maxWeightValues.add(new Entry(x, currentExercise.getMaxWeight().floatValue()));

                        totalRepsValues.add(new Entry(x, currentExercise.getTotalReps().floatValue()));

                        // Add the date corresponding to the data point
                        workoutDates.add(MainActivity.dataStorage.getWorkoutDays().get(i).getDate());

                        // Add the month corresponding to the data point
                        String date = MainActivity.dataStorage.getWorkoutDays().get(i).getDate();
                        String month = getMonthFromDateString(date);
                        workoutMonths.add(month);

                        x++;
                    }
                }
            }

            if(x == 0)
            {
                SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(AddExerciseActivity.this);
                snackBarWithMessage.showSnackbar("No data found");
                return super.onOptionsItemSelected(item);
            }


            // Chart Data Spinner
            Spinner graph_value_spinner = view.findViewById(R.id.graph_spinner);
            String[] graph_value_spinner_options = {"Volume", "Weight", "Reps"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, graph_value_spinner_options);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            graph_value_spinner.setAdapter(adapter);
            graph_value_spinner.setSelection(0);


            // Chart Timeframe Spinner
            Spinner graph_time_spinner = view.findViewById(R.id.graph_time_spinner);
            String[] graph_time_spinner_options = {"All Time", "Last Year", "6 months", "3 months", "1 month"};
            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, graph_time_spinner_options);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            graph_time_spinner.setAdapter(adapter1);
            graph_time_spinner.setSelection(0);


            graph_value_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedOption1 = graph_time_spinner.getSelectedItem().toString();
                    String selectedOption2 = graph_value_spinner_options[i];

                    setupChartWithOptions(selectedOption1, selectedOption2, volumeValues, maxWeightValues,totalRepsValues, workoutDates, workoutMonths, alertDialog, lineChart);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // Do nothing
                }
            });


            graph_time_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedOption1 = graph_time_spinner_options[i];
                    String selectedOption2 = graph_value_spinner.getSelectedItem().toString();

                    setupChartWithOptions(selectedOption1, selectedOption2, volumeValues, maxWeightValues,totalRepsValues, workoutDates, workoutMonths, alertDialog, lineChart);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // Do nothing
                }
            });


            // Default option is volume
            setupLineChart(alertDialog, volumeValues, lineChart, workoutMonths, workoutDates,"kg", false);
        }

        // Exercise Comments
        else if(item.getItemId() == R.id.comment)
        {
            // Prepare to show exercise history dialog box
            LayoutInflater inflater = LayoutInflater.from(AddExerciseActivity.this);
            View view = inflater.inflate(R.layout.add_exercise_comment_dialog,null);
            AlertDialog alertDialog = new AlertDialog.Builder(AddExerciseActivity.this).setView(view).create();


            bt_save_comment = view.findViewById(R.id.bt_save_comment);
            bt_clear_comment = view.findViewById(R.id.bt_clear_comment);
            et_exercise_comment = view.findViewById(R.id.et_exercise_comment);

            // Check if exercise exists (to show the comment if it has one)
            // Find if workout day already exists
            int exercise_position = MainActivity.dataStorage.getExercisePosition(MainActivity.dateSelected,exercise_name);

            // Exists, then show the comment
            if(exercise_position >= 0)
            {
                System.out.println("We can comment, exercise exists");

                int day_position = MainActivity.dataStorage.getDayPosition(MainActivity.dateSelected);

                String comment = MainActivity.dataStorage.getWorkoutDays().get(day_position).getExercises().get(exercise_position).getComment();

                et_exercise_comment.setText(comment);
            }



            bt_clear_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    clearComment();
                }
            });

            bt_save_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveComment(alertDialog);
                }
            });

            // Show Chart Dialog box
            alertDialog.show();

        }

        return super.onOptionsItemSelected(item);
    }

    private void setupChartWithOptions(String selectedOption1, String selectedOption2, ArrayList<Entry> volumeValues, ArrayList<Entry> maxWeightValues,ArrayList<Entry> totalRepsValues, ArrayList<String> workoutDates, ArrayList<String> workoutMonths, AlertDialog alertDialog, LineChart lineChart){
        String label = new String();
        ArrayList<Entry> actualValues = new ArrayList<>();
        ArrayList<Entry> finalValues = new ArrayList<>();


        if(selectedOption2.equals("Weight"))
        {
            actualValues = maxWeightValues;
            label = "kg";
        }
        else if(selectedOption2.equals("Volume"))
        {
            actualValues = volumeValues;
            label = "kg";
        }
        else if(selectedOption2.equals("Reps"))
        {
            actualValues = totalRepsValues;
            label = "reps";
        }

        int index = -1;

        if(selectedOption1.equals("All Time"))
        {
            index = 0;
        }
        else if(selectedOption1.equals("Last Year"))
        {
            index = getIndexOfLastXMonths(workoutDates, 12);
        }
        else if(selectedOption1.equals("6 months"))
        {
            index = getIndexOfLastXMonths(workoutDates, 6);
        }
        else if(selectedOption1.equals("3 months"))
        {
            index = getIndexOfLastXMonths(workoutDates, 3);
        }
        else if(selectedOption1.equals("1 month"))
        {
            index = getIndexOfLastXMonths(workoutDates, 1);
        }

        // Show all
        if(index == -1)
        {
            index = actualValues.size();
        }

        finalValues = new ArrayList<>(actualValues.subList(index, actualValues.size()));
        setupLineChart(alertDialog, finalValues , lineChart, workoutMonths, workoutDates, label, true);
    }

    private int getIndexOfLastXMonths(ArrayList<String> workoutDates, int months){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -months);
        Date oneMonthBefore = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String givenDate = dateFormat.format(oneMonthBefore);
        return findFirstDateAfter(workoutDates, givenDate);
    }

    public int findFirstDateAfter(ArrayList<String> workoutDates, String givenDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date givenDateObj;
        try {
            givenDateObj = dateFormat.parse(givenDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // If the given date cannot be parsed
        }

        for (int i = 0; i < workoutDates.size(); i++) {
            Date currentDateObj;
            try {
                currentDateObj = dateFormat.parse(workoutDates.get(i));
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }

            if (currentDateObj.after(givenDateObj)) {
                return i;
            }
        }

        return -1; // If no date is found after the given date
    }

    private String getMonthFromDateString(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateString);
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
            return monthFormat.format(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
            return "";
        }
    }


    public boolean setupExerciseHistory(MenuItem item) {
        // Prepare to show exercise history dialog box
        LayoutInflater inflater = LayoutInflater.from(AddExerciseActivity.this);
        View view = inflater.inflate(R.layout.exercise_history_dialog,null);
        AlertDialog alertDialog = new AlertDialog.Builder(AddExerciseActivity.this).setView(view).create();


        // Declare local data structure
        ArrayList<WorkoutExercise> allPerformedSessions = new ArrayList<>();

        // Find all performed sessions of a specific exercise and add them to local data structure
        for(int i = MainActivity.dataStorage.getWorkoutDays().size()-1; i >= 0; i--)
        {
            for(int j = 0; j < MainActivity.dataStorage.getWorkoutDays().get(i).getExercises().size(); j++)
            {
                if(MainActivity.dataStorage.getWorkoutDays().get(i).getExercises().get(j).getExercise().equals(exercise_name))
                {
                    allPerformedSessions.add(MainActivity.dataStorage.getWorkoutDays().get(i).getExercises().get(j));
                }
            }
        }

        if(allPerformedSessions.size() == 0){
            SnackBarWithMessage snackBarWithMessage = new SnackBarWithMessage(AddExerciseActivity.this);
            snackBarWithMessage.showSnackbar("No data found");
            return super.onOptionsItemSelected(item);
        }


        // Set Exercise Name
        TextView tv_exercise_name = view.findViewById(R.id.tv_exercise_name);
        tv_exercise_name.setText(exercise_name);


        // Set Exercise History Recycler View
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_Exercise_History);
        ExerciseHistoryExerciseAdapter workoutExerciseAdapter4 = new ExerciseHistoryExerciseAdapter(AddExerciseActivity.this, allPerformedSessions);


        // Crash Here
        recyclerView.setAdapter(workoutExerciseAdapter4);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddExerciseActivity.this));


        alertDialog.show();

        return super.onOptionsItemSelected(item);
    }


    public void setupLineChart(AlertDialog alertDialog, ArrayList<Entry> volumeValues, LineChart lineChart, ArrayList<String> workoutMonths, ArrayList<String> workoutDates, String label, boolean showYaxis) {
        LineDataSet volumeSet = new LineDataSet(volumeValues, label);
        LineData data = new LineData(volumeSet);

        // Style the line and the values
        volumeSet.setLineWidth(3f);
        volumeSet.setColor(ContextCompat.getColor(AddExerciseActivity.this, R.color.colorPrimary));
        volumeSet.setCircleColor(ContextCompat.getColor(AddExerciseActivity.this, R.color.colorPrimary));
        volumeSet.setCircleRadius(2f);
        volumeSet.setCircleHoleColor(ContextCompat.getColor(AddExerciseActivity.this, R.color.colorPrimary));
        volumeSet.setValueTextSize(10f);
        volumeSet.setValueTextColor(Color.BLACK);
//        volumeSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // Remove circles around data points
        volumeSet.setDrawCircles(true);

        // Hide data values next to points
        volumeSet.setDrawValues(false);

        // Style the chart
        lineChart.setData(data);
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);

        // Enable horizontal grid lines
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setGridColor(Color.LTGRAY);
        lineChart.getAxisLeft().setGridLineWidth(1f);


        // Disable Y-axis line
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getAxisLeft().setDrawLabels(false);

        // Hide Y-axis values
        lineChart.getAxisLeft().setEnabled(true);
        lineChart.getAxisLeft().setTextSize(0f);
        lineChart.getXAxis().setEnabled(true);

        // Set the custom X-axis value formatter
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new MonthXAxisFormatter(workoutMonths));
        xAxis.setGranularity(2f); // Set minimum interval to 1
        xAxis.setGranularityEnabled(true); // Enable granularity

        xAxis.setLabelCount(5, false); // Display only 5 labels on the X-axis


        // Enable pinch zooming
        lineChart.setPinchZoom(true);

        // Enable scaling (zooming) on both X and Y axes
        lineChart.setScaleEnabled(true);

        lineChart.animateXY(1000, 1000, Easing.EaseInOutCubic);

        // Reset chart
        lineChart.fitScreen();
        lineChart.getViewPortHandler().refresh(new Matrix(), lineChart, true);
        lineChart.invalidate();


        // Zoom in to show the last X points
//        int pointsToShow = 10;
//        lineChart.setVisibleXRange(pointsToShow, pointsToShow);
//        lineChart.moveViewToX(volumeValues.size() - pointsToShow);


        // Style legend
        Legend legend = lineChart.getLegend();
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setDrawInside(true);
        legend.setXOffset(30f); // Adjust this value to move the legend horizontally
        legend.setYOffset(-270); // Adjust this value to move the legend vertically
        legend.setTextSize(12f);
        legend.setTextColor(Color.BLACK);
        legend.setForm(Legend.LegendForm.LINE);
        legend.setFormLineWidth(3f);
        legend.setFormSize(14f);

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                popupChartAlertDialog(e, workoutMonths, label);
            }

            @Override
            public void onNothingSelected() {
                // Do nothing
            }
        });


        // Show Chart Dialog box
        alertDialog.show();
    }

    public void popupChartAlertDialog(Entry e, ArrayList<String> workoutMonths, String label) {
        float xValue = e.getX();

        // Dismiss the current dialog if it is showing
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(AddExerciseActivity.this);
        builder.setTitle(workoutMonths.get((int) xValue));

        LayoutInflater inflater = LayoutInflater.from(AddExerciseActivity.this);
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
        builder.setView(dialogView);

        TextView messageTextView = dialogView.findViewById(R.id.message_text_view);
        messageTextView.setText(String.valueOf(e.getY()) + " " + label);

        AlertDialog dialog = builder.create();

        // Set custom width (in pixels)
        int customWidth = 600; // You can change this value to your desired width

        // Set the layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = customWidth;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));


        // Show the dialog
        dialog.show();

        // Apply the layout parameters
        dialog.getWindow().setAttributes(layoutParams);

        // Set the current dialog to the newly created dialog
        currentDialog = dialog;
    }

    public void setupTimer() {

        // Prepare to show timer dialog box
        LayoutInflater inflater = LayoutInflater.from(AddExerciseActivity.this);
        View view = inflater.inflate(R.layout.timer_dialog,null);
        AlertDialog alertDialog = new AlertDialog.Builder(AddExerciseActivity.this).setView(view).create();

        // Get Objects (use view because dialog box from menu)
        et_seconds = view.findViewById(R.id.et_seconds);
        minus_seconds = view.findViewById(R.id.minus_seconds);
        plus_seconds = view.findViewById(R.id.plus_seconds);
        bt_start = view.findViewById(R.id.bt_start);
        bt_reset = view.findViewById(R.id.bt_close);

        // Set default seconds value to 180 i.e 3 minutes
        if(!TimerRunning)
        {
            // Derive String value from chosen start time
            // et_seconds.setText(String.valueOf((int) START_TIME_IN_MILLIS /1000));
            loadSeconds();
        }
        else
        {
            updateCountDownText();
        }

        // Reset Timer Button
        bt_reset.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                resetTimer();
            }
        });

        // Start Timer Button
        bt_start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(TimerRunning)
                {
                    pauseTimer();
                }
                else
                {
                    saveSeconds();
                    startTimer();
                }

            }
        });

        // Minus Button
        minus_seconds.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!et_seconds.getText().toString().isEmpty())
                {
                    Double seconds  = Double.parseDouble(et_seconds.getText().toString());
                    seconds = seconds - 1;
                    if(seconds < 0)
                    {
                        seconds = 0.0;
                    }
                    int seconds_int = seconds.intValue();
                    et_seconds.setText(String.valueOf(seconds_int));
                }
            }
        });

        // Plus Button
        plus_seconds.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!et_seconds.getText().toString().isEmpty())
                {
                    Double seconds  = Double.parseDouble(et_seconds.getText().toString());
                    seconds = seconds + 1;
                    if(seconds < 0)
                    {
                        seconds = 0.0;
                    }
                    int seconds_int = seconds.intValue();
                    et_seconds.setText(String.valueOf(seconds_int));
                }
            }
        });

        // Show Timer Dialog Box
        alertDialog.show();
    }


        // Makes necessary checks and saves comment
    public void saveComment(AlertDialog alertDialog)
    {
        // Let backup service know that something has changed
        MainActivity.autoBackupRequired = true;
        com.example.verifit.SharedPreferences sharedPreferences = new com.example.verifit.SharedPreferences(getApplicationContext());
        sharedPreferences.save("true", "autoBackupRequired");

        // Check if exercise exists (cannot comment on non-existant exercise)
        // Find if workout day already exists
        int exercise_position = MainActivity.dataStorage.getExercisePosition(MainActivity.dateSelected,exercise_name);

        if(exercise_position >= 0)
        {
            System.out.println("We can comment, exercise exists");
        }
        else
        {
            System.out.println("We can't comment, exercise doesn't exist");
            Toast.makeText(getApplicationContext(),"Can't comment without sets",Toast.LENGTH_SHORT).show();
            return;
        }

        String comment;

        if(et_exercise_comment.getText().toString().isEmpty())
        {
            comment = "";
        }
        else
        {
            comment = et_exercise_comment.getText().toString(); // Get user comment
        }

        // Get the date for today
        int day_position = MainActivity.dataStorage.getDayPosition(MainActivity.dateSelected);


        // Modify the data structure to add the comment
        MainActivity.dataStorage.getWorkoutDays().get(day_position).getExercises().get(exercise_position).setComment(comment);


        final int finalSize = MainActivity.dataStorage.getWorkoutDays().get(day_position).getExercises().get(exercise_position).getSets().size();

        // Also modify individual sets
        for(int i = 0; i < MainActivity.dataStorage.getWorkoutDays().get(day_position).getExercises().get(exercise_position).getSets().size(); i++)
        {
            final int finalI = i;

            WorkoutSet set_to_be_updated = MainActivity.dataStorage.getWorkoutDays().get(day_position).getExercises().get(exercise_position).getSets().get(i);
            set_to_be_updated.setComment(comment);

            if(sharedPreferences.isOfflineMode())
            {
                updateCommentInSet(day_position, exercise_position, finalI, finalSize, comment);
                alertDialog.dismiss();
            }
            else
            {
                final LoadingDialog loadingDialog = new LoadingDialog(AddExerciseActivity.this);
                loadingDialog.loadingAlertDialog();

                WorkoutSetsApi workoutSetsApi = new WorkoutSetsApi(getApplicationContext(), getString(R.string.API_ENDPOINT));
                workoutSetsApi.updateWorkoutSet(set_to_be_updated, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e)
                    {
                        loadingDialog.dismissDialog();
                        alertDialog.dismiss();
                        showSnackbarMessage(e.toString());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        loadingDialog.dismissDialog();
                        alertDialog.dismiss();

                        if(200 == response.code())
                        {
                            updateCommentInSet(day_position, exercise_position, finalI, finalSize, comment);

                        }
                        else
                        {
                            showSnackbarMessage(response.message().toString());
                        }
                    }
                });
            }
        }
    }

    public void updateCommentInSet(int day_position, int exercise_position, int finalI, int finalSize, String comment)
    {
        MainActivity.dataStorage.getWorkoutDays().get(day_position).getExercises().get(exercise_position).getSets().get(finalI).setComment(comment);

        runOnUiThread(() -> {
            updateTodaysExercises();
        });

        if (finalI == finalSize-1) // Show popup only when last set comment is saved
        {
            showSnackbarMessage("Comment Logged");
        }
    }


    // Makes necessary checks and clears comment
    public void clearComment()
    {
        // Let backup service know that something has changed
        MainActivity.autoBackupRequired = true;
        com.example.verifit.SharedPreferences sharedPreferences = new com.example.verifit.SharedPreferences(getApplicationContext());
        sharedPreferences.save("true", "autoBackupRequired");
        et_exercise_comment.setText("");
    }

    public void startTimer()
    {
        countDownTimer = new CountDownTimer(TimeLeftInMillis, 1000)
        {
            @Override
            public void onTick(long MillisUntilFinish)
            {
                TimeLeftInMillis = MillisUntilFinish;
                updateCountDownText();
            }

            @Override
            public void onFinish()
            {
                TimerRunning = false;
                bt_start.setText("Start");
            }
        }.start();

        TimerRunning = true;
        bt_start.setText("Pause");

    }

    public void loadSeconds()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",MODE_PRIVATE);
        String seconds = sharedPreferences.getString("seconds","180");

        // Change actual values that timer uses
        START_TIME_IN_MILLIS = Integer.parseInt(seconds) * 1000;
        TimeLeftInMillis = START_TIME_IN_MILLIS;

        et_seconds.setText(seconds);
    }

    public void saveSeconds()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(!et_seconds.getText().toString().isEmpty())
        {
            String seconds = et_seconds.getText().toString();

            // Change actual values that timer uses
            START_TIME_IN_MILLIS = Integer.parseInt(seconds) * 1000;
            TimeLeftInMillis = START_TIME_IN_MILLIS;

            // Save to shared preferences
            editor.putString("seconds",et_seconds.getText().toString());
            editor.apply();
        }
    }

    public void pauseTimer()
    {
        countDownTimer.cancel();
        TimerRunning = false;
        bt_start.setText("Start");
    }

    public void resetTimer()
    {
        if(TimerRunning)
        {
            pauseTimer();
            TimeLeftInMillis = START_TIME_IN_MILLIS;
            updateCountDownText();
        }

    }

    public void updateCountDownText()
    {
        int seconds = (int) TimeLeftInMillis / 1000;
        int minutes = (int) seconds / 60;
        et_seconds.setText(String.valueOf(seconds));
    }

}