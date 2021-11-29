package com.questionanswersgenerator.questiongenerator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class ExamActivity extends AppCompatActivity {

    databaseHelper db;
    TextView timerText;
    int hours, min, sec, count;
    Handler handler;
    boolean isExamOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        db = new databaseHelper(ExamActivity.this);

        Intent intent = getIntent();
        String subject = intent.getStringExtra("subject");
        String chapter = intent.getStringExtra("chapter");
        int timeLimit = Integer.parseInt(intent.getStringExtra("time_limit"));

        timerText = findViewById(R.id.timerText);

        ArrayList<ArrayList<String>> questionList = db.getQuestions(subject, chapter);
        Collections.shuffle(questionList);

        RecyclerView questionsRecycler = findViewById(R.id.questionsRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ExamActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        questionsRecycler.setLayoutManager(layoutManager);
        questionsRecycler.setAdapter(new examQuestionAdapter(questionList, isExamOver));


        if (timeLimit > 0) {
            count = timeLimit * 60;
            hours = timeLimit / 60;
            min = timeLimit - (hours * 60);
            sec = 0;
            handler = new Handler();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    sec--;
                    count--;
                    if (sec < 0) {
                        min--;
                        sec = 59;
                    }


                    if (min < 0) {
                        hours--;
                        min = 59;
                    }

                    if (count < 0) {
                        examover();
                        try {

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                AudioManager mgr = null;
                                mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                int valuess = 15;//range(0-15)
                                mgr.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0);
                            }

                            MediaPlayer mp = MediaPlayer.create(ExamActivity.this, R.raw.examover);
                            if (mp.isPlaying())
                                mp.stop();
                            mp.setLooping(false);
                            mp.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }


                    String temp = "";
                    if (hours > 0 && hours < 10)
                        temp += "0";
                    if (hours > 0)
                        temp += String.valueOf(hours) + ":";

                    if (min >= 0 && min < 10)
                        temp += "0";

                    temp += String.valueOf(min) + ":";


                    if (sec >= 0 && sec < 10)
                        temp += "0";
                    temp += String.valueOf(sec);

                    timerText.setText(temp);

                    if (min < 10 && hours <= 0)
                        timerText.setTextColor(getResources().getColor(R.color.red));


                    handler.postDelayed(this, 1000);
                }
            });
        }


        ((Button) findViewById(R.id.endExamBtn)).setOnClickListener(view -> {

            if (!isExamOver) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ExamActivity.this);
                builder.setMessage("Are you sure you want to end this Examination?")
                        .setCancelable(true)
                        .setPositiveButton("End Exam", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                examover();
                                dialog.dismiss();
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.setTitle("End Exam");
                alert.show();
            } else if (((Button) findViewById(R.id.endExamBtn)).getText().toString().trim().toLowerCase().equals("show answers")) {
                questionsRecycler.setAdapter(new examQuestionAdapter(questionList, isExamOver));
                ((Button) findViewById(R.id.endExamBtn)).setText("Go Back");
            } else {
                finish();
            }
        });


    }

    private void examover() {
        try {
            handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        isExamOver = true;
        timerText.setText("EXAM OVER");
        timerText.setTextColor(getResources().getColor(R.color.red));
        ((Button) findViewById(R.id.endExamBtn)).setText("Show Answers");
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        Toast.makeText(ExamActivity.this, "End Exam Before going Back", Toast.LENGTH_SHORT).show();
        ((Button) findViewById(R.id.endExamBtn)).callOnClick();
    }
}