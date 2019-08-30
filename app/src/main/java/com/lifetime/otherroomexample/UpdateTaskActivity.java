package com.lifetime.otherroomexample;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UpdateTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String DATE_FORMAT = "[0-9]{2}/[0-9]{2}/[0-9]{4}";
    private EditText editTextTask,editTextDesc,editTextFinishBy,editTextBirth;
    private CheckBox checkBoxFinished;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);

        final TaskDao taskDao = DatabaseClient
                .getInstance(getApplicationContext())
                .getAppDatabase()
                .taskDao();

        editTextTask = findViewById(R.id.update_name);
        editTextDesc = findViewById(R.id.update_address);
        editTextFinishBy = findViewById(R.id.update_subjects);
        editTextBirth = findViewById(R.id.update_birthday);

        checkBoxFinished = findViewById(R.id.checkBoxFinished);

        spinner = findViewById(R.id.update_gender);

        editTextBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate();
            }
        });

        final Task task = (Task) getIntent().getSerializableExtra("task");

        //----- spinner area

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.numbers,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        int malePosition = adapter.getPosition("Male");
        int femalePosition = adapter.getPosition("Female");

//        createSpinner();
        //----- spinner area

        loadTask(task);
        boolean male = task.getGender().equals("Male");
        if(male){
            spinner.setSelection(malePosition);
        }else{
            spinner.setSelection(femalePosition);
        }

        findViewById(R.id.button_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTask(task,taskDao);
            }
        });

        findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateTaskActivity.this);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteTask(task,taskDao);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog ad = builder.create();
                ad.show();
            }
        });
    }

    private void createSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.numbers,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private int createSpinnerAndGetResult(String compareValue){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.numbers,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        return adapter.getPosition(compareValue);
    }

    private void pickDate(){
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                //i: year i1: month i2:day
                calendar.set(i,i1,i2);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                editTextBirth.setText(simpleDateFormat.format(calendar.getTime()));
            }
        },year,month,day);
        datePickerDialog.show();
    }

    private void loadTask(Task task) {
        editTextTask.setText(task.getTask());
        editTextDesc.setText(task.getDesc());
        editTextFinishBy.setText(task.getFinishBy());
        editTextBirth.setText(task.getBirthday());
        checkBoxFinished.setChecked(task.isFinished());
    }

    private void updateTask(final Task task,final TaskDao taskDao){
        final String sTask = editTextTask.getText().toString().trim();
        final String sDesc = editTextDesc.getText().toString().trim();
        final String sFinishBy = editTextFinishBy.getText().toString().trim();
        final String birthday = editTextBirth.getText().toString().trim();
        final String sSpinner = spinner.getSelectedItem().toString();
        final boolean sChecked = checkBoxFinished.isChecked();

        if(sTask.isEmpty()) {
            editTextTask.setError("Task required");
            editTextTask.requestFocus();
            return;
        }

        if(sDesc.isEmpty()){
            editTextDesc.setError("Desc required");
            editTextDesc.requestFocus();
            return;
        }

        if(sFinishBy.isEmpty()){
            editTextFinishBy.setError("Finish by required");
            editTextFinishBy.requestFocus();
            return;
        }

        if(birthday.isEmpty()){
            editTextBirth.setError("Birthday required");
            editTextBirth.requestFocus();
            return;
        }

        boolean invalidDate = !birthday.matches(DATE_FORMAT);
        if(invalidDate){
            editTextBirth.setError("Format date");
            editTextBirth.requestFocus();
            return;
        }

        if(sSpinner.equals("Gender")){
            TextView errorText = (TextView)spinner.getSelectedView();
            errorText.setError("required");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("required");
            spinner.requestFocus();
            return;
        }

        class UpdateTask extends AsyncTask<Void,Void,Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                task.setTask(sTask);
                task.setDesc(sDesc);
                task.setFinishBy(sFinishBy);
                task.setBirthday(birthday);
                task.setFinished(sChecked);
                task.setGender(sSpinner);
                taskDao.update(task);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(UpdateTaskActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(UpdateTaskActivity.this,MainActivity.class));
            }
        }

        UpdateTask ut = new UpdateTask();
        ut.execute();
    }

    private void deleteTask(final Task task,final TaskDao taskDao){
        class DeleteTask extends AsyncTask<Void,Void,Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                taskDao.delete(task);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(UpdateTaskActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(UpdateTaskActivity.this,MainActivity.class));
            }
        }

        DeleteTask dt = new DeleteTask();
        dt.execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
