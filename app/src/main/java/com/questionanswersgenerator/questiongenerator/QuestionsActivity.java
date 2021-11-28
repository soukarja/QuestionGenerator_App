package com.questionanswersgenerator.questiongenerator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;

public class QuestionsActivity extends AppCompatActivity {

    databaseHelper db;
    RecyclerView subjectListRecycler;
    ArrayList<ArrayList<String>> subjectList;
    boolean showAnswers;
    MaterialCheckBox showAllAnswersCheckbox;

    String chapterID, chapterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        showAnswers = false;

        db = new databaseHelper(QuestionsActivity.this);
        showAllAnswersCheckbox = findViewById(R.id.showAnswersCheckBox);

        Intent intent = getIntent();
        chapterID = intent.getStringExtra("subject_id");
        chapterName = intent.getStringExtra("subject_name");

        ((TextView) findViewById(R.id.chapterName)).setText(chapterName);

        subjectListRecycler = findViewById(R.id.subjectList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(QuestionsActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        subjectListRecycler.setLayoutManager(layoutManager);

        refreshList();


        ((Button) findViewById(R.id.createNew)).setOnClickListener(view -> {
            Intent intent1 = new Intent(QuestionsActivity.this, NewQuestionActivity.class);
            intent1.putExtra("chapter_id", chapterID);
            intent1.putExtra("question_id", "-1");
            startActivity(intent1);
        });

        ((ImageView) findViewById(R.id.editBtn)).setOnClickListener(view -> {
            final Dialog dialog = new Dialog(QuestionsActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.create_new_input);

            TextView title = dialog.findViewById(R.id.title);
            title.setText("Edit Chapter");

            AutoCompleteTextView input = dialog.findViewById(R.id.input);
            input.setText(chapterName);

            Button saveChanges = dialog.findViewById(R.id.saveChanges);
            saveChanges.setOnClickListener(view1 -> {
                String text = input.getText().toString().trim();
                if (text.equals(""))
                    Toast.makeText(QuestionsActivity.this, "Please Enter the Chapter Name", Toast.LENGTH_SHORT).show();
                else {
                    if (db.updateChapter(chapterID, text)) {
                        chapterName = text;
                        ((TextView) findViewById(R.id.chapterName)).setText(text);
                    } else {
                        Toast.makeText(QuestionsActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                }
            });

            dialog.show();
        });


        ((ImageView) findViewById(R.id.delete)).setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(QuestionsActivity.this);
            builder.setMessage("Do you want to delete this chapter?")
                    .setCancelable(true)
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (db.deleteChapter(chapterID)) {
                                finish();
                            } else {
                                Toast.makeText(QuestionsActivity.this, "Cannot Delete this Chapter", Toast.LENGTH_SHORT).show();
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
            alert.setTitle("Delete Chapter '" + chapterName + "'");
            alert.show();
        });

        showAllAnswersCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showAnswers = b;
                subjectListRecycler.setAdapter(new QuestionsAdapter(subjectList, QuestionsActivity.this, showAnswers));
            }
        });


        ((Button) findViewById(R.id.startExam)).setOnClickListener(view -> {
            Intent intent2 = new Intent(QuestionsActivity.this, StartExamActivity.class);
            String subjectID = db.getChapterParent(chapterID);
            intent2.putExtra("subject", subjectID);
            intent2.putExtra("chapter", chapterID);
            startActivity(intent2);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        subjectList = db.getAllQuestions(chapterID);
        subjectListRecycler.setAdapter(new QuestionsAdapter(subjectList, QuestionsActivity.this, showAnswers));

        if (subjectList.size() <= 0) {
            ((Button) findViewById(R.id.startExam)).setVisibility(View.GONE);
            showAllAnswersCheckbox.setVisibility(View.GONE);
        } else {
            ((Button) findViewById(R.id.startExam)).setVisibility(View.VISIBLE);
            showAllAnswersCheckbox.setVisibility(View.VISIBLE);
        }
    }
}