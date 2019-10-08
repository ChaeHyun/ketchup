package com.ketchup;


import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.ketchup.tasklist.TaskListFragment;
import com.ketchup.tasklist.TaskListViewModel;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.view.Menu;
import android.widget.Toast;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import timber.log.Timber;

public class MainActivity extends DaggerAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavController navController;
    TaskListViewModel taskListViewModel;

    @Inject
    DaggerViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup ViewModel
        taskListViewModel = ViewModelProviders.of(this, viewModelFactory).get(TaskListViewModel.class);
        setupNavController();
        setupDrawerLayout();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // DestinationChangedListener listener 해제해줘야한다.
    }

    private void setupDrawerLayout() {
        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout ctl = findViewById(R.id.activity_main_collapsing_toolbar);
        ctl.setTitle("Collapsing Toolbar");

        /// Setup Navigation Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true); // Hamburger icon setting
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        NavigationUI.setupActionBarWithNavController(this, navController, drawer);
    }

    private void setupNavController() {
        navController = Navigation.findNavController(this, R.id.activity_nav_host_fragment);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                /* Remove later : BackStack Logging */
                Timber.d("[onDestinationChanged ] : %s", destination.getLabel());

                /*
                // To check BackStack state
                Timber.d("BackStack Cnt : %d", getSupportFragmentManager().findFragmentById(R.id.activity_nav_host_fragment).getChildFragmentManager().getBackStackEntryCount());
                int size = getSupportFragmentManager().findFragmentById(R.id.activity_nav_host_fragment).getChildFragmentManager().getBackStackEntryCount();
                for (int i = 0; i < size; i++) {
                    Timber.d(getSupportFragmentManager().findFragmentById(R.id.activity_nav_host_fragment).getChildFragmentManager().getBackStackEntryAt(i).getName());
                }
                */
            }
        });
    }

    @Override
    public void onBackPressed() {
        Timber.d("onBackPressed");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Timber.d("[onOptionItemSelected]  ID 값 : %d", id);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Toast.makeText(this, "1번 메뉴 선택", Toast.LENGTH_LONG).show();

            taskListViewModel.setTaskType(1);

        } else if (id == R.id.nav_gallery) {
            Toast.makeText(this, "2번 메뉴 선택", Toast.LENGTH_LONG).show();

            Bundle bundle = new Bundle();
            bundle.putInt(TaskListFragment.TASK_FILTER, 5);

            Navigation.findNavController(this, R.id.activity_nav_host_fragment).navigate(R.id.action_task_list_self, bundle);
            taskListViewModel.setTaskType(2);

        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(this, "3번 메뉴 선택", Toast.LENGTH_LONG).show();

            taskListViewModel.setTaskType(3);

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        Timber.d(" NAVIGATE UP is Called!!");
        return Navigation.findNavController(this, R.id.activity_nav_host_fragment).navigateUp();
    }


}

