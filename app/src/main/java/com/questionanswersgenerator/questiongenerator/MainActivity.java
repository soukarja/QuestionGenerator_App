package com.questionanswersgenerator.questiongenerator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    databaseHelper db;
    RecyclerView subjectListRecycler;
    ArrayList<ArrayList<String>> subjectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new databaseHelper(MainActivity.this);

        subjectListRecycler = findViewById(R.id.subjectList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        subjectListRecycler.setLayoutManager(layoutManager);

        refreshList();


        ((Button) findViewById(R.id.createNew)).setOnClickListener(view -> {

            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.create_new_input);

            TextView title = dialog.findViewById(R.id.title);
            title.setText("Create New Subject");

            AutoCompleteTextView input = dialog.findViewById(R.id.input);
//            input.setHint("Enter Name of the Subject");

            Button saveChanges = dialog.findViewById(R.id.saveChanges);
            saveChanges.setOnClickListener(view1 -> {
                String text = input.getText().toString().trim();
                if (text.equals(""))
                    Toast.makeText(MainActivity.this, "Please Enter the Subject Name", Toast.LENGTH_SHORT).show();
                else {
                    if (db.createNewSubject(text)) {
                        refreshList();
                    } else {
                        Toast.makeText(MainActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                }
            });

            dialog.show();

        });


        ((Button) findViewById(R.id.startExam)).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, StartExamActivity.class);
            intent.putExtra("subject", "-1");
            intent.putExtra("chapter", "-1");
            startActivity(intent);
        });
    }

    private void refreshList() {
        subjectList = db.getAllSubjects();
        subjectListRecycler.setAdapter(new subjectAdapter(subjectList, MainActivity.this));

        if (subjectList.size() <= 0)
            ((Button) findViewById(R.id.startExam)).setVisibility(View.GONE);
        else
            ((Button) findViewById(R.id.startExam)).setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to close the application?")
                .setCancelable(true)
                .setPositiveButton("Close App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle("Close App");
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }
}