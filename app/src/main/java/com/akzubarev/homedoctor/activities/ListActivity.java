package com.akzubarev.homedoctor.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.StatisticMaker;
import com.akzubarev.homedoctor.databinding.ActivityListBinding;
import com.akzubarev.homedoctor.databinding.ActivityMainBinding;
import com.akzubarev.homedoctor.ui.list.MedicationAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ListActivity extends AppCompatActivity {
    private final Context l_context = this;
    ArrayList<Medication> medications = new ArrayList<>();
    private AppBarConfiguration mAppBarConfiguration;
    private @NonNull
    ActivityListBinding binding;
//    FireBaseUtils.StatisticsCallback callback = loaded_medications -> {
//        medications = loaded_medications;
////        fill();
//        oldFill(medications);
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initializeFragment() {
        binding = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarList.toolbar);
        binding.appBarList.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_list, R.id.nav_medication)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_list);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
//        setContentView(R.layout.activity_list);
//
//        Toolbar myToolbar = findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toast.makeText(this, "listAactivity created", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeFragment();

        Toast.makeText(this, "listAactivity resuming", Toast.LENGTH_LONG).show();
        ArrayList<Date> dates = new ArrayList<>();
        dates.add(Calendar.getInstance().getTime());
        medications.add(new Medication("Парацетамол",
                "2 недели", 2, dates));
        medications.add(new Medication("Ибупрофен",
                "1 неделя", 3, dates));
        medications.add(new Medication("Феназепам",
                "1 месяц", 1, dates));
        fill();
        Toast.makeText(this, "listActivity filled", Toast.LENGTH_LONG).show();

//            FireBaseUtils.getUserStats(medications->{ });
    }

    public void fill() {
        RecyclerView medicationsList = findViewById(R.id.medications_list);
        Log.d("ddd", medicationsList.toString());
        medicationsList.setHasFixedSize(true);
        medicationsList.addItemDecoration(new DividerItemDecoration(
                medicationsList.getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager medicationsLayoutManager = new LinearLayoutManager(this);
        MedicationAdapter medicationsAdapter = new MedicationAdapter(medications, this);

        medicationsList.setLayoutManager(medicationsLayoutManager);
        medicationsList.setAdapter(medicationsAdapter);
    }

    public void fill(ArrayList<Medication> medications) {
        this.medications = medications;
        fill();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
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
