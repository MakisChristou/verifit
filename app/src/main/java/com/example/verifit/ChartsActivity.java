package com.example.verifit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ChartsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

        // Basic Initialization
        onCreateStuff();
    }


    @Override
    protected void onRestart()
    {
        super.onRestart();
        onCreateStuff();
    }

    public void onCreateStuff()
    {
        // Bottom Navigation Bar Intents
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.charts);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        barChartVolumes();

        pieChartWorkouts();


    }

    public void barChartVolumes()
    {
        // Bar Chart Example
        BarChart barChart = findViewById(R.id.barChart);
        // Interaction Customization

        // Add Data pairs in List
        ArrayList<BarEntry> workouts = new ArrayList<BarEntry>();

        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
        {
            workouts.add(new BarEntry(i,MainActivity.Workout_Days.get(i).getDayVolume().floatValue()));
        }

        // ???
        BarDataSet barDataSet = new BarDataSet(workouts,"Workouts");
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(15f);

        // Profit
        BarData barData = new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("");
        barChart.invalidate();
        barChart.animateY(500);
    }

    public void pieChartWorkouts()
    {
        // Find Workout Years
        HashSet<String> Years = new HashSet<>();

        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
        {
            String date = MainActivity.Workout_Days.get(i).getDate();
            String year = date.substring(0,4);
            Years.add(year);
        }

        // Workout years and number of workouts per year
        HashMap<String,Integer> Years_Workouts = new HashMap<String, Integer>();

        // Iterate set years
        Years.forEach(year ->
        {
            Years_Workouts.put(year,0);
        });

        // Calculate number of workouts per year
        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
        {
            String date = MainActivity.Workout_Days.get(i).getDate();
            String year = date.substring(0,4);

            int workouts = Years_Workouts.get(year);
            Years_Workouts.put(year,workouts+1);
        }

        // Initialize Pie Chart
        PieChart pieChart = findViewById(R.id.pieChart);

        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(60f);

        ArrayList<PieEntry> yValues = new ArrayList<>();

        for (Map.Entry<String, Integer> stringIntegerEntry : Years_Workouts.entrySet())
        {
            Map.Entry pair = (Map.Entry) stringIntegerEntry;

            Integer workouts = (Integer) pair.getValue();
            String year = (String) pair.getKey();

            yValues.add(new PieEntry(workouts,year));
        }

        PieDataSet pieDataSet = new PieDataSet(yValues,"");

        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(pieDataSet);
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);

        pieChart.animateY(1000, Easing.EaseInOutCubic);

        pieChart.setNoDataText("No Workouts");



    }

//    public void pieChartExercises()
//    {
//        // Find Workout Years
//        HashSet<String> Exercises = new HashSet<>();
//
//        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
//        {
//            for(int j = 0; j < MainActivity.Workout_Days.get(i).getExercises().size(); j++)
//            {
//                String Exercise = MainActivity.Workout_Days.get(i).getExercises().get(j).getExercise();
//                Exercises.add(Exercise);
//            }
//        }
//
//        // Workout years and number of workouts per year
//        HashMap<String,Integer> Number_Exercises = new HashMap<String, Integer>();
//
//        // Iterate set years
//        Years.forEach(year ->
//        {
//            Years_Workouts.put(year,0);
//        });
//
//        // Calculate number of workouts per year
//        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
//        {
//            String date = MainActivity.Workout_Days.get(i).getDate();
//            String year = date.substring(0,4);
//
//            int workouts = Years_Workouts.get(year);
//            Years_Workouts.put(year,workouts+1);
//        }
//
//        // Initialize Pie Chart
//        PieChart pieChart = findViewById(R.id.pieChart);
//
//        pieChart.setUsePercentValues(false);
//        pieChart.getDescription().setEnabled(false);
//        pieChart.setExtraOffsets(5,10,5,5);
//        pieChart.setDragDecelerationFrictionCoef(0.95f);
//        pieChart.setDrawHoleEnabled(false);
//        pieChart.setHoleColor(Color.WHITE);
//        pieChart.setTransparentCircleRadius(60f);
//
//        ArrayList<PieEntry> yValues = new ArrayList<>();
//
//        for (Map.Entry<String, Integer> stringIntegerEntry : Years_Workouts.entrySet())
//        {
//            Map.Entry pair = (Map.Entry) stringIntegerEntry;
//
//            Integer workouts = (Integer) pair.getValue();
//            String year = (String) pair.getKey();
//
//            yValues.add(new PieEntry(workouts,year));
//        }
//
//        PieDataSet pieDataSet = new PieDataSet(yValues,"");
//
//        pieDataSet.setSliceSpace(3f);
//        pieDataSet.setSelectionShift(5f);
//        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
//
//        PieData data = new PieData(pieDataSet);
//        data.setValueTextSize(15f);
//        data.setValueTextColor(Color.WHITE);
//
//        pieChart.setData(data);
//
//        pieChart.animateY(1000, Easing.EaseInOutCubic);
//
//        pieChart.setNoDataText("No Workouts");
//    }


    // Navigates to given activity based on the selected menu item
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        if(item.getItemId() == R.id.home)
        {
            Intent in = new Intent(this,MainActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        else if(item.getItemId() == R.id.exercises)
        {
            Intent in = new Intent(this,ExercisesActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        else if(item.getItemId() == R.id.diary)
        {
            Intent in = new Intent(this,DiaryActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        else if(item.getItemId() == R.id.charts)
        {
            Intent in = new Intent(this,ChartsActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        else if(item.getItemId() == R.id.me)
        {
            Intent in = new Intent(this,MeActivity.class);
            startActivity(in);
            overridePendingTransition(0,0);
        }
        return true;
    }

    // Menu Stuff
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.charts_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.settings)
        {
            Intent in = new Intent(this,SettingsActivity.class);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }

}