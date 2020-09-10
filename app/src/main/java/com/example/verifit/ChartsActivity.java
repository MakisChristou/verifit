package com.example.verifit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;

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
import java.util.Iterator;
import java.util.Map;

public class ChartsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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


        barChart();

        pieChart();


    }

    public void barChart()
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
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        // Profit
        BarData barData = new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("");
        barChart.invalidate();
        barChart.animateY(500);
    }


    public void pieChart()
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
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

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

}