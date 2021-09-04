package com.example.verifit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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

        pieChartBodyparts();

        piChartExercises();


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

        // Add Date Labels to workout
        ArrayList<String> workoutDates = new ArrayList<String>();

//        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
//        {
//            workoutDates.add(MainActivity.Workout_Days.get(i).getDate());
//        }



        // Make it invisible because otherwise it looks like shit
        if(workouts.size() == 0)
        {
            barChart.setVisibility(View.INVISIBLE);
        }


        // Remove Legend
        Legend l = barChart.getLegend();
        l.setEnabled(false);


        // Show last X workouts only
        int last_workouts = 5;
        int counter = 0;
        ArrayList<BarEntry> workouts_pruned = new ArrayList<BarEntry>();


        for(int i = 0; i < workouts.size(); i++)
        {
            counter++;
            if(counter > workouts.size()-last_workouts)
            {
                workouts_pruned.add(workouts.get(i));
            }
        }




        // ???
        BarDataSet barDataSet = new BarDataSet(workouts_pruned,"Workouts");
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
//        barDataSet.setValueTextColor(Color.BLACK);
//        barDataSet.setValueTextSize(15f);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(workoutDates));

        // Profit
        BarData barData = new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("");
        barChart.invalidate();
        barChart.animateY(500);
        barChart.setScaleMinima(1f, 1f);
        //barChart.zoomIn();
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

        pieChart.animateY(1000, Easing.EaseInOutCubic);
        pieChart.setNoDataText("No Workouts");
        pieChart.getLegend().setEnabled(false);
        pieChart.setData(data);




    }

    public void pieChartBodyparts()
    {
        // Find Workout Years
        HashSet<String> Bodyparts = new HashSet<>();

        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
        {
            for(int j = 0; j < MainActivity.Workout_Days.get(i).getSets().size(); j++)
            {
                String Exercise = MainActivity.Workout_Days.get(i).getSets().get(j).getCategory();
                Bodyparts.add(Exercise);
            }
        }

        // Workout years and number of workouts per year
        HashMap<String,Integer> Number_Bodyparts = new HashMap<String, Integer>();

        // Iterate set years
        Bodyparts.forEach(exercise ->
        {
            Number_Bodyparts.put(exercise,0);
        });

        // Calculate number of workouts per year
        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
        {
            for(int j = 0; j < MainActivity.Workout_Days.get(i).getSets().size(); j++)
            {
                String Exercise = MainActivity.Workout_Days.get(i).getSets().get(j).getCategory();

                int Exercise_Workouts = Number_Bodyparts.get(Exercise);
                Number_Bodyparts.put(Exercise,Exercise_Workouts+1);
            }
        }


        // Initialize Pie Chart
        PieChart pieChart = findViewById(R.id.pieChartBodyparts);

        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(60f);

        ArrayList<PieEntry> yValues = new ArrayList<>();

        for (Map.Entry<String, Integer> stringIntegerEntry : Number_Bodyparts.entrySet())
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

        pieChart.getLegend().setEnabled(false);
        pieChart.animateY(1000, Easing.EaseInOutCubic);
        pieChart.setNoDataText("No Workouts");
        pieChart.setData(data);

    }

    public void piChartExercises()
    {
        // Find Workout Years
        HashSet<String> Exercises = new HashSet<>();

        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
        {
            for(int j = 0; j < MainActivity.Workout_Days.get(i).getSets().size(); j++)
            {
                String Exercise = MainActivity.Workout_Days.get(i).getSets().get(j).getExercise();
                Exercises.add(Exercise);
            }
        }

        // Workout years and number of workouts per year
        HashMap<String,Integer> Number_Exercises = new HashMap<String, Integer>();

        // Iterate set years
        Exercises.forEach(exercise ->
        {
            Number_Exercises.put(exercise,0);
        });

        // Calculate number of workouts per year
        for(int i = 0; i < MainActivity.Workout_Days.size(); i++)
        {
            for(int j = 0; j < MainActivity.Workout_Days.get(i).getSets().size(); j++)
            {
                String Exercise = MainActivity.Workout_Days.get(i).getSets().get(j).getExercise();

                //System.out.println(Exercise);

                int Exercise_Workouts = Number_Exercises.get(Exercise);
                Number_Exercises.put(Exercise,Exercise_Workouts+1);
            }
        }


        HashMap<String,Integer> Number_Exercises_Sorted = sortValues(Number_Exercises);


        System.out.println(Number_Exercises_Sorted.size());

        HashMap<String,Integer> Number_Exercises_Sorted_Pruned = new HashMap<String, Integer>();;

        int top_exercises = 5;
        int counter = 0;

        for (Map.Entry<String, Integer> entry : Number_Exercises_Sorted.entrySet()) {

            counter++;
            if(counter > Number_Exercises_Sorted.size()-top_exercises)
            {
                Number_Exercises_Sorted_Pruned.put(entry.getKey(),entry.getValue());
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }

        }


        // Initialize Pie Chart
        PieChart pieChart = findViewById(R.id.pieChartExercises);

        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(60f);

        ArrayList<PieEntry> yValues = new ArrayList<>();

        for (Map.Entry<String, Integer> stringIntegerEntry : Number_Exercises_Sorted_Pruned.entrySet())
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

        pieChart.getLegend().setEnabled(false);
        pieChart.animateY(1000, Easing.EaseInOutCubic);
        pieChart.setNoDataText("No Workouts");
        pieChart.setData(data);

    }



    private static HashMap sortValues(HashMap map)
    {
        List list = new LinkedList(map.entrySet());
        //Custom Comparator
        Collections.sort(list, new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
            }
        });
        //copying the sorted list in HashMap to preserve the iteration order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

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