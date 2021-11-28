package com.questionanswersgenerator.questiongenerator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChaptersActivity extends AppCompatActivity {
    databaseHelper db;
    RecyclerView subjectListRecycler;
    ArrayList<ArrayList<String>> subjectList;

    String subjectID, subjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);

        db = new databaseHelper(ChaptersActivity.this);

        Intent intent = getIntent();
        subjectID = intent.getStringExtra("subject_id");
        subjectName = intent.getStringExtra("subject_name");

        ((TextView) findViewById(R.id.chapterName)).setText(subjectName);

        subjectListRecycler = findViewById(R.id.subjectList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChaptersActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        subjectListRecycler.setLayoutManager(layoutManager);

        refreshList();


        ((Button) findViewById(R.id.createNew)).setOnClickListener(view -> {

            final Dialog dialog = new Dialog(ChaptersActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.create_new_input);

            TextView title = dialog.findViewById(R.id.title);
            title.setText("Create New Chapter");

            AutoCompleteTextView input = dialog.findViewById(R.id.input);

            Button saveChanges = dialog.findViewById(R.id.saveChanges);
            saveChanges.setOnClickListener(view1 -> {
                String text = input.getText().toString().trim();
                if (text.equals(""))
                    Toast.makeText(ChaptersActivity.this, "Please Enter the Chapter Name", Toast.LENGTH_SHORT).show();
                else {
                    if (db.createNewChapter(text, subjectID)) {
                        refreshList();
                    } else {
                        Toast.makeText(ChaptersActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                }
            });

            dialog.show();

        });

        ((ImageView)findViewById(R.id.editBtn)).setOnClickListener(view -> {
            final Dialog dialog = new Dialog(ChaptersActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.create_new_input);

            TextView title = dialog.findViewById(R.id.title);
            title.setText("Edit Subject");

            AutoCompleteTextView input = dialog.findViewById(R.id.input);
            input.setText(subjectName);

            Button saveChanges = dialog.findViewById(R.id.saveChanges);
            saveChanges.setOnClickListener(view1 -> {
                String text = input.getText().toString().trim();
                if (text.equals(""))
                    Toast.makeText(ChaptersActivity.this, "Please Enter the Subject Name", Toast.LENGTH_SHORT).show();
                else {
                    if (db.updateSubject(subjectID, text)) {
                        subjectName = text;
                        ((TextView) findViewById(R.id.chapterName)).setText(text);
                    } else {
                        Toast.makeText(ChaptersActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                }
            });

            dialog.show();
        });


        ((ImageView) findViewById(R.id.delete)).setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChaptersActivity.this);
            builder.setMessage("Do you want to delete this subject?")
                    .setCancelable(true)
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (db.deleteSubject(subjectID)) {
                                finish();
                            } else {
                                Toast.makeText(ChaptersActivity.this, "Cannot Delete this Subject", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.setTitle("Delete Subject '" + subjectName + "'");
            alert.show();
        });

        ((Button) findViewById(R.id.startExam)).setOnClickListener(view -> {
            Intent intent2 = new Intent(ChaptersActivity.this, StartExamActivity.class);
            intent2.putExtra("subject", subjectID);
            intent2.putExtra("chapter", "-1");
            startActivity(intent2);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        subjectList = db.getAllChapters(subjectID);
        subjectListRecycler.setAdapter(new subjectAdapter(subjectList, ChaptersActivity.this, 2));

        if (subjectList.size() <= 0)
            ((Button) findViewById(R.id.startExam)).setVisibility(View.GONE);
        else
            ((Button) findViewById(R.id.startExam)).setVisibility(View.VISIBLE);
    }
}