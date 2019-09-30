package com.ketchup;


import android.app.FragmentManager;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.ketchup.model.task.Task;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import android.view.Menu;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import timber.log.Timber;

public class MainActivity extends DaggerAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavController navController;

    TaskViewModel taskViewModel;

    @Inject
    DaggerViewModelFactory viewModelFactory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout ctl = findViewById(R.id.activity_main_collapsing_toolbar);
        ctl.setTitle("Collapsing Toolbar");

        /** Floating Action Button */
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.i("[ Refresh() ]");
                taskViewModel.refresh();

                Snackbar.make(view, "Load All Tasks", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /** Drawer & NavigationView */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navController = Navigation.findNavController(this, R.id.activity_nav_host_fragment);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                Timber.d("[onDestinationChanged ] : " + destination.getId());
            }
        });

        taskViewModel = ViewModelProviders.of(this, viewModelFactory).get(TaskViewModel.class);


        // Observe
        taskViewModel.getLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Timber.v("[ LiveData loading is TRUE ]");
                    Toast.makeText(getApplicationContext(), "Loading : TRUE", Toast.LENGTH_SHORT).show();
                } else {
                    Timber.v("[ LiveData loading is FALSE ]");
                    Toast.makeText(getApplicationContext(), "Loading : FALSE", Toast.LENGTH_SHORT).show();
                }
            }
        });

        taskViewModel.getTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                Timber.v("[ Observer got a change - List<Task> ]");
                for (Task t : tasks)
                    Timber.v(t.getTitle());
            }
        });

        taskViewModel.getTask().observe(this, new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                Timber.v("[ Observer got a change - Single Task ]");
                if (task == null) {
                    Timber.v("Not found");
                }
                else {
                    Timber.v(task.getTitle());
                }
            }
        });

    }

    private void showFragment(@NonNull final Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_nav_host_fragment, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
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

            taskViewModel.setTaskType(1);

        } else if (id == R.id.nav_gallery) {
            Toast.makeText(this, "2번 메뉴 선택", Toast.LENGTH_LONG).show();

            Bundle bundle = new Bundle();
            bundle.putInt(TaskListFragment.TASK_FILTER, 2);

            Navigation.findNavController(this, R.id.activity_nav_host_fragment).navigate(R.id.action_task_list_self, bundle);
            taskViewModel.setTaskType(2);

        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(this, "3번 메뉴 선택", Toast.LENGTH_LONG).show();

            taskViewModel.setTaskType(3);

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
        return Navigation.findNavController(this, R.id.activity_nav_host_fragment).navigateUp();
    }
}

