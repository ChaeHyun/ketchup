package com.ketchup;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.ketchup.tasklist.TaskListViewModel;
import com.ketchup.utils.ToolbarController;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.view.Menu;
import android.widget.Toast;

import java.util.Locale;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import timber.log.Timber;

public class MainActivity extends DaggerAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavController navController;
    private TaskListViewModel viewModel;

    @Inject
    DaggerViewModelFactory viewModelFactory;
    ToolbarController toolbarController;

    public static Locale DEVICE_LOCALE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup ViewModel
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TaskListViewModel.class);
        setupNavController();
        setupDrawerLayout();

        DEVICE_LOCALE = getCurrentLocale();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navController.removeOnDestinationChangedListener(destinationChangedListener);
        toolbarController.removeDrawerListener();
    }

    private void setupDrawerLayout() {
        // Setup Toolbar
        toolbarController = new ToolbarController(this);
        setSupportActionBar(toolbarController.getToolbar());

        /// Setup Navigation Drawer
        NavigationView navigationView = findViewById(R.id.nav_view);
        toolbarController.setDrawerIndicatorEnabled(true);
        toolbarController.addDrawerListener();
        toolbarController.toggleSyncState();
        navigationView.setNavigationItemSelectedListener(this);
        NavigationUI.setupActionBarWithNavController(this, navController, toolbarController.getDrawer());

        navigationView.getMenu().getItem(0).setChecked(true);
        viewModel.setTaskType(1);
    }

    private void setupNavController() {
        navController = Navigation.findNavController(this, R.id.activity_nav_host_fragment);
        navController.addOnDestinationChangedListener(destinationChangedListener);
    }

    NavController.OnDestinationChangedListener destinationChangedListener = new NavController.OnDestinationChangedListener() {
        @Override
        public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
            /* Remove later : BackStack Logging */
            Timber.d("[onDestinationChanged ] : %s", destination.getLabel());
            //backstackLog();
        }
    };

    private void backstackLog() {
        // To check BackStack state
        Timber.d("BackStack Cnt : %d", getSupportFragmentManager().findFragmentById(R.id.activity_nav_host_fragment).getChildFragmentManager().getBackStackEntryCount());
        int size = getSupportFragmentManager().findFragmentById(R.id.activity_nav_host_fragment).getChildFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < size; i++) {
            Timber.d(getSupportFragmentManager().findFragmentById(R.id.activity_nav_host_fragment).getChildFragmentManager().getBackStackEntryAt(i).getName());
        }
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

        if (id == R.id.nav_today) {
            Timber.d("1번메뉴 선택");
            Toast.makeText(this, "1번 메뉴 선택", Toast.LENGTH_LONG).show();
            //toolbarController.setTitle("Today");
            viewModel.setTaskType(1);

        } else if (id == R.id.nav_upcoming) {
            Toast.makeText(this, "2번 메뉴 선택", Toast.LENGTH_LONG).show();
            toolbarController.setTitle("Upcoming");
            viewModel.setTaskType(2);

        } else if (id == R.id.nav_overdue) {
            Toast.makeText(this, "3번 메뉴 선택", Toast.LENGTH_LONG).show();
            toolbarController.setTitle("Overdue");
            viewModel.setTaskType(3);

        } else if (id == R.id.nav_memo) {
            toolbarController.setTitle("Memo");
            viewModel.setTaskType(4);

        } else if (id == R.id.nav_tomorrow) {
            toolbarController.setTitle("Tomorrow");
            viewModel.setTaskType(5);

        } else if (id == R.id.nav_all_tasks) {
            viewModel.setTaskType(6);
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

    public Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return getApplicationContext().getResources().getConfiguration().getLocales().get(0);
        else
            return getApplicationContext().getResources().getConfiguration().locale;
    }

}

