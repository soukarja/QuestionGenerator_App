package com.questionanswersgenerator.questiongenerator;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class NewQuestionActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SPEECH_INPUT = 350;
    Bitmap bitmap;
    EditText scannedTextView;
    databaseHelper db;
    String chapterID, questionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);

        db = new databaseHelper(NewQuestionActivity.this);
        Intent intent = getIntent();
        chapterID = intent.getStringExtra("chapter_id");
        questionID = intent.getStringExtra("question_id");

        if (!questionID.equals("-1")) {
            ((TextView) findViewById(R.id.topBarTitle)).setText("Edit Question");
            ArrayList<String> data = db.getQuestion(questionID);
            ((EditText) findViewById(R.id.question)).setText(data.get(0));
            ((EditText) findViewById(R.id.answer)).setText(data.get(1));
            ((Button) findViewById(R.id.addQuestion)).setText("Save Changes");
            ((ImageView) findViewById(R.id.deleteBtn)).setVisibility(View.VISIBLE);
        }


        ((ImageView) findViewById(R.id.backBtn)).setOnClickListener(view -> {
            finish();
        });

        ((TextView) findViewById(R.id.scanQuestionBtn)).setOnClickListener(view -> {
            scannedTextView = (EditText) findViewById(R.id.question);
            askPermissions();
        });

        ((TextView) findViewById(R.id.scanAnswerBtn)).setOnClickListener(view -> {
            scannedTextView = (EditText) findViewById(R.id.answer);
            askPermissions();
        });

        ((Button) findViewById(R.id.addQuestion)).setOnClickListener(view -> {
            String question = ((EditText) findViewById(R.id.question)).getText().toString().trim();

            if (question.equals("")) {
                Toast.makeText(NewQuestionActivity.this, "Please Enter the Question", Toast.LENGTH_SHORT).show();
                return;
            }

            String answer = ((EditText) findViewById(R.id.answer)).getText().toString().trim();

            if (questionID.equals("-1")) {

                if (db.createNewQuestion(question, answer, chapterID)) {
                    finish();
                } else {
                    Toast.makeText(NewQuestionActivity.this, "Unable to add this Question", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (db.updateQuestion(question, answer, questionID)) {
                    finish();
                } else {
                    Toast.makeText(NewQuestionActivity.this, "Unable to add this Question", Toast.LENGTH_SHORT).show();
                }
            }
        });


        ((ImageView) findViewById(R.id.voiceInputQuestion)).setOnClickListener(view -> {
            scannedTextView = (EditText) findViewById(R.id.question);
            startVoiceTyping();
        });


        ((ImageView) findViewById(R.id.voiceInputAnswer)).setOnClickListener(view -> {
            scannedTextView = (EditText) findViewById(R.id.answer);
            startVoiceTyping();
        });


        ((ImageView) findViewById(R.id.deleteBtn)).setOnClickListener(view -> {
            if (!questionID.equals("-1")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewQuestionActivity.this);
                builder.setMessage("Do you want to delete this question?")
                        .setCancelable(true)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (db.deleteQuestion(questionID)) {
                                    finish();
                                } else {
                                    Toast.makeText(NewQuestionActivity.this, "Cannot Delete this Question", Toast.LENGTH_SHORT).show();
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
                alert.setTitle("Delete Question");
                alert.show();
            }
        });


    }

    private boolean askPermissions() {
        if (ContextCompat.checkSelfPermission(NewQuestionActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NewQuestionActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);

            return false;
        }

        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(NewQuestionActivity.this);

        return true;
    }

    private void startVoiceTyping() {
        Intent intent2 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent2.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent2.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent2.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Type");

        startActivityForResult(intent2, REQUEST_CODE_SPEECH_INPUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    getTextFromImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                scannedTextView.setText(Objects.requireNonNull(result).get(0));
            }
        }
    }

    private void getTextFromImage(Bitmap bitmap) {
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(NewQuestionActivity.this, "Unable to Scan Text", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < textBlockSparseArray.size(); i++) {
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }

            if (scannedTextView != null)
                scannedTextView.setText(stringBuilder.toString());
            else {
                Toast.makeText(NewQuestionActivity.this, stringBuilder.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}