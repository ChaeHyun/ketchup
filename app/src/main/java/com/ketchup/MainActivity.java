package com.ketchup;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.ketchup.model.task.Task;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import timber.log.Timber;

public class MainActivity extends DaggerAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TaskViewModel taskViewModel;

    @Inject
    DaggerViewModelFactory viewModelFactory;

    TextView textView;
    EditText editTextSearch;
    EditText editTextInsert;

    Button buttonSearch;
    Button buttonInsert;
    Button buttonLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        textView = findViewById(R.id.textViewList);
        buttonLoad = findViewById(R.id.buttonLoad);
        buttonInsert = findViewById(R.id.buttonInsert);
        buttonSearch = findViewById(R.id.buttonSearch);

        editTextSearch = findViewById(R.id.editTextSearch);
        editTextInsert = findViewById(R.id.editTextInsert);

        taskViewModel = ViewModelProviders.of(this, viewModelFactory).get(TaskViewModel.class);

        buttonLoad.setOnClickListener(v -> {
            Timber.i("[ onClick ] Load All Tasks");
            //taskViewModel.loadTasks();
            taskViewModel.testLoadTaskAsync();
            Toast.makeText(getApplicationContext(), "[onClick] Load All List", Toast.LENGTH_LONG).show();
        });


        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextInsert.getText().toString();
                taskViewModel.insertTask(new Task(UUID.randomUUID().toString(), title));
                Timber.v("[ onClick ] Insert : " + title);
                Toast.makeText(getApplicationContext(), "[onClick] Insert : " + title, Toast.LENGTH_LONG).show();
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextSearch.getText().toString();
                taskViewModel.loadTasksByTitle(title);
                Timber.v("[ onClick ] Search Task : " + title);
                Toast.makeText(getApplicationContext(), "[onClick] Search : " + title, Toast.LENGTH_LONG).show();
            }
        });

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
                textView.setText("[ Result Tasks ]\n");
                for (Task t : tasks)
                    textView.append(t.getTitle() + "\n");
            }
        });

        taskViewModel.getTask().observe(this, new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                textView.setText("[ Result SingleTask ]\n");
                if (task == null) {
                    textView.append("No found" + "\n");
                }
                else {
                    textView.append(task.getTitle() + "\n");
                }
            }
        });

        Timber.d("[Test UUID]");
        UUID uuid = UUID.randomUUID();
        String uuidToString = uuid.toString();
        Timber.d("uuidToString ->    %s" , uuidToString);
        UUID uuidFromString = UUID.fromString(uuidToString);
        Timber.d("uuidFromString ->     %s" , uuidFromString);
        Timber.d(uuid.equals( uuidFromString) ? "TRUE" : "FALSE");

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
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
