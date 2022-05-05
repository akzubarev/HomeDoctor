package com.akzubarev.homedoctor.ui.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton fab;
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
                R.id.HomeFragment, R.id.MedicationsListFragment
//                R.id.MedicationFragment
        ).setOpenableLayout(drawer).build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_info:
                navController.navigate(R.id.InfoFragment);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}