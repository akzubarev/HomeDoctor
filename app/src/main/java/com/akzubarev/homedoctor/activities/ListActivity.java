package com.akzubarev.homedoctor.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.StatisticMaker;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private final Context l_context = this;
    ArrayList<Integer> stat = new ArrayList<>();
    ArrayList<Medication> medications = new ArrayList<>();
    int statistics = 0;
    int statsize = 10;

//    FireBaseUtils.StatisticsCallback callback = loaded_medications -> {
//        medications = loaded_medications;
////        fill();
//        oldFill(medications);
//    };

    public void tourClick(View v) {
        Intent i = new Intent(l_context, MedicationsActivity.class);
        i.putExtra("Tour", (Integer) (v.getTag()));
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        if (versioningTool().equals("decimalsBeta"))
//            findViewById(R.id.graphlayout).setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fill();
//            FireBaseUtils.getUserStats(medications->{ });
    }

    public void fill() {
        return;

    }

    public void fill(ArrayList<Medication> medications) {

        this.medications = medications;
        fill();
//        RecyclerView tourlist = findViewById(R.id.tourlist);
//        tourlist.setHasFixedSize(true);
//        tourlist.addItemDecoration(new DividerItemDecoration(
//                tourlist.getContext(), DividerItemDecoration.VERTICAL));
//        LinearLayoutManager userLayoutManager = new LinearLayoutManager(this);
//        TourAdapter tourAdapter = new TourAdapter(medications, this);
//
//        tourlist.setLayoutManager(userLayoutManager);
//        tourlist.setAdapter(tourAdapter);
//
//        tourAdapter.setOnUserClickListener(position -> {
//            Intent i = new Intent(l_context, StatTourActivity.class);
//            i.putExtra("Tour", position);
//            startActivity(i);
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_statisctics, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void DeleteStatistics() {
        StatisticMaker.removeStatistics(this);
        medications.clear();
        fill(medications);
    }

}
