package com.akzubarev.homedoctor.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);


        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.setGraph(R.navigation.mobile_navigation);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.ProfilesListFragment, R.id.OwnedMedicationsListFragment,
                R.id.MedicationsListFragment, R.id.OldTreatmentListFragment, R.id.QRFragment
        ).setOpenableLayout(drawer).build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        String destination = getIntent().getStringExtra("destination");
        if (destination != null)
            if (destination.equals("QR")) {
                String treatmentID = getIntent().getStringExtra("treatmentID");
                Bundle outBundle = new Bundle();
                Log.d(TAG, treatmentID);
                outBundle.putString("treatmentID", treatmentID);
                navController.navigate(R.id.QRFragment, outBundle);
            }

    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_delete).setVisible(false);
        menu.findItem(R.id.action_info).setVisible(false);
        menu.findItem(R.id.action_settings).setVisible(true);
        return true;
    }
}