package com.questionanswersgenerator.questiongenerator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class StartExamActivity extends AppCompatActivity {
    databaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_exam);

        db = new databaseHelper(StartExamActivity.this);
        ((ImageView) findViewById(R.id.backBtn)).setOnClickListener(view -> {
            finish();
        });


        Intent intent = getIntent();
        String subject = intent.getStringExtra("subject");
        String chapter = intent.getStringExtra("chapter");
        ArrayList<Integer> timeLimitValue = new ArrayList<>();

        AutoCompleteTextView timeLimit = findViewById(R.id.timeLimit);

        ArrayList<String> timeLimitList = new ArrayList<>();
        timeLimitList.add("No Time Limit");
        timeLimitValue.add(0);

        timeLimitList.add("5 Minutes");
        timeLimitValue.add(5);

        timeLimitList.add("10 Minutes");
        timeLimitValue.add(10);

        int minutes = 0;
        int hours = 0;
        while (hours < 3) {
            String temp = "";
            minutes += 15;
            if (minutes >= 60) {
                minutes = 0;
                hours++;
            }
            if (hours > 1)
                temp += String.valueOf(hours) + " Hours ";
            else if (hours > 0)
                temp += String.valueOf(hours) + " Hour ";

            if (minutes > 0)
                temp += String.valueOf(minutes) + " Minutes";

            timeLimitList.add(temp);
            timeLimitValue.add((hours * 60) + minutes);
        }

        ArrayAdapter<String> timeLimitAdapter = new ArrayAdapter<>(StartExamActivity.this, R.layout.dropdown_row, timeLimitList);
        timeLimit.setAdapter(timeLimitAdapter);


        if (!subject.equals("-1")) {
            ((TextView) findViewById(R.id.subjectName)).setText(db.getSubject(subject));
        }

        if (!chapter.equals("-1")) {
            ((TextView) findViewById(R.id.chapterName)).setText(db.getChapter(chapter));
        }

        ArrayList<ArrayList<String>> questionList = db.getQuestions(subject, chapter);
        ((TextView) findViewById(R.id.totalQuestions)).setText(String.valueOf(questionList.size()) + " Questions");


        if (questionList.size() <= 0)
            ((Button) findViewById(R.id.startExamBtn)).setVisibility(View.GONE);

        ((Button) findViewById(R.id.startExamBtn)).setOnClickListener(view -> {
            int chosenValue = getTimeLimitMinutes(timeLimit.getText().toString(), timeLimitValue, timeLimitList);

            Intent intent1 = new Intent(StartExamActivity.this, ExamActivity.class);
            intent1.putExtra("subject", subject);
            intent1.putExtra("chapter", chapter);
            intent1.putExtra("time_limit", String.valueOf(chosenValue));
            startActivity(intent1);
            finish();
        });

    }

    private int getTimeLimitMinutes(String chosenValue, ArrayList<Integer> timeValue, ArrayList<String> timeLimitList) {
        for (int i = 0; i < timeLimitList.size(); i++) {
            if (chosenValue.trim().toLowerCase().equals(timeLimitList.get(i).trim().toLowerCase()))
                return timeValue.get(i);
        }

        return 0;
    }
}