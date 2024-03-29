package com.lifetime.otherroomexample;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    boolean aToZSortNextTime = true;
    private FloatingActionButton buttonAddTask,buttonSortTask;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TaskDao taskDao = DatabaseClient
                .getInstance(getApplicationContext())
                .getAppDatabase()
                .taskDao();

        recyclerView = findViewById(R.id.recycler_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        buttonAddTask = findViewById(R.id.floating_button_add);
        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AddTaskActivity.class);
                startActivity(intent);
            }
        });

        buttonSortTask = findViewById(R.id.floating_button_sort);
        buttonSortTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(aToZSortNextTime){
                    sortTaskAZ(taskDao);
                    aToZSortNextTime = false;
                }else {
                    sortTaskZA(taskDao);
                    aToZSortNextTime = true;
                }
            }
        });

        getTasks(taskDao);

    }

    private void sortTaskAZ(final TaskDao taskDao){
        class SortTasks extends AsyncTask<Void,Void,List<Task>>{

            @Override
            protected List<Task> doInBackground(Void... voids) {
                return taskDao.sortDataAtoZ();
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                TaskAdapter adapter = new TaskAdapter(MainActivity.this,tasks);
                recyclerView.setAdapter(adapter);
//                Collections.sort(tasks);
//                adapter.notifyDataSetChanged();
            }
        }

        SortTasks gt = new SortTasks();
        gt.execute();
    }

    private void sortTaskZA(final TaskDao taskDao){
        class SortTasks extends AsyncTask<Void,Void,List<Task>>{

            @Override
            protected List<Task> doInBackground(Void... voids) {
                return taskDao.sortDataZtoA();
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                TaskAdapter adapter = new TaskAdapter(MainActivity.this,tasks);
                recyclerView.setAdapter(adapter);
            }
        }

        SortTasks gt = new SortTasks();
        gt.execute();
    }

    private void getTasks(final TaskDao taskDao){
        class GetTasks extends AsyncTask<Void,Void, List<Task>>{

            @Override
            protected List<Task> doInBackground(Void... voids) {
                return taskDao.getAll();
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                TaskAdapter adapter = new TaskAdapter(MainActivity.this,tasks);
                recyclerView.setAdapter(adapter);
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();

    }

    private void searchTasks(final String taskName){
        class SearchTasks extends AsyncTask<Void,Void,List<Task>>{

            @Override
            protected List<Task> doInBackground(Void... voids) {
                return  DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .taskDao()
                        .searchTaskByTaskName(taskName.trim());
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                TaskAdapter adapter = new TaskAdapter(MainActivity.this,tasks);
                recyclerView.setAdapter(adapter);
            }
        }

        SearchTasks st = new SearchTasks();
        st.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu,menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchTasks(s);
                return false;
            }
        });
        return true;
    }
}
