package com.questionanswersgenerator.questiongenerator;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class databaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "qagenerator.db";

    public static final String SUBJECTS_TABLE_NAME = "subjects";
    public static final String SUBJECTS_COL_ID = "id";
    public static final String SUBJECTS_COL_NAME = "name";

    public static final String CHAPTERS_TABLE_NAME = "chapters";
    public static final String CHAPTERS_COL_ID = "id";
    public static final String CHAPTERS_COL_NAME = "name";
    public static final String CHAPTERS_COL_PARENTSUBJECT = "parent";

    public static final String QUESTIONS_TABLE_NAME = "questions";
    public static final String QUESTIONS_COL_ID = "id";
    public static final String QUESTIONS_COL_QUESTION = "question";
    public static final String QUESTIONS_COL_ANSWER = "answer";
    public static final String QUESTIONS_COL_PARENTCHAPTER = "parent";

    public databaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + SUBJECTS_TABLE_NAME +
                "(" + SUBJECTS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SUBJECTS_COL_NAME + " TEXT"
                + ")");

        db.execSQL("CREATE TABLE " + CHAPTERS_TABLE_NAME +
                "(" + CHAPTERS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CHAPTERS_COL_NAME + " TEXT,"
                + CHAPTERS_COL_PARENTSUBJECT + " INTEGER"
                + ")");

        db.execSQL("CREATE TABLE " + QUESTIONS_TABLE_NAME +
                "(" + QUESTIONS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + QUESTIONS_COL_QUESTION + " TEXT,"
                + QUESTIONS_COL_ANSWER + " TEXT,"
                + QUESTIONS_COL_PARENTCHAPTER + " INTEGER"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + SUBJECTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CHAPTERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QUESTIONS_TABLE_NAME);
        onCreate(db);
    }

    public boolean createNewSubject(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(SUBJECTS_COL_NAME, name);

        long res = db.insert(SUBJECTS_TABLE_NAME, null, contentValues);

        if (res == -1) {
            return false;
        }
        return true;
    }

    @SuppressLint("Range")
    public ArrayList<ArrayList<String>> getAllSubjects() {
        ArrayList<ArrayList<String>> subjects = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + SUBJECTS_TABLE_NAME + " ORDER BY " + SUBJECTS_COL_NAME + " ASC", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            ArrayList<String> row = new ArrayList<String>();

            row.add(String.valueOf(res.getInt(res.getColumnIndex(SUBJECTS_COL_ID))));
            row.add(res.getString(res.getColumnIndex(SUBJECTS_COL_NAME)));
            subjects.add(row);
            res.moveToNext();
        }

        return subjects;
    }

    public boolean deleteSubject(String subjectID) {
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete(SUBJECTS_TABLE_NAME,
                SUBJECTS_COL_ID + "= ? ",
                new String[]{subjectID});

        if (res == -1) {
            return false;
        }
        return true;
    }

    public boolean updateSubject(String subjectID, String subjectName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(SUBJECTS_COL_NAME, subjectName);

        long res = db.update(SUBJECTS_TABLE_NAME, contentValues, SUBJECTS_COL_ID + " = ?", new String[]{subjectID});

        if (res == -1) {
            return false;
        }

        return true;
    }

    public boolean createNewChapter(String name, String parentSubject) {
        int parent = Integer.parseInt(parentSubject);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CHAPTERS_COL_NAME, name);
        contentValues.put(CHAPTERS_COL_PARENTSUBJECT, parent);

        long res = db.insert(CHAPTERS_TABLE_NAME, null, contentValues);

        if (res == -1) {
            return false;
        }
        return true;
    }

    public boolean updateChapter(String chapterID, String chapterName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CHAPTERS_COL_NAME, chapterName);

        long res = db.update(CHAPTERS_TABLE_NAME, contentValues, CHAPTERS_COL_ID + " = ?", new String[]{chapterID});

        if (res == -1) {
            return false;
        }

        return true;
    }


    @SuppressLint("Range")
    public ArrayList<ArrayList<String>> getAllChapters(String parentSubject) {
//        int parent = Integer.parseInt(parentSubject);
        ArrayList<ArrayList<String>> chapters = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + CHAPTERS_TABLE_NAME + " WHERE " + CHAPTERS_COL_PARENTSUBJECT + " = " + parentSubject + " ORDER BY " + CHAPTERS_COL_NAME + " ASC", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            ArrayList<String> row = new ArrayList<String>();

            row.add(String.valueOf(res.getInt(res.getColumnIndex(CHAPTERS_COL_ID))));
            row.add(res.getString(res.getColumnIndex(CHAPTERS_COL_NAME)));
            chapters.add(row);
            res.moveToNext();
        }

        return chapters;
    }

    public boolean deleteChapter(String chapterID) {
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete(CHAPTERS_TABLE_NAME,
                CHAPTERS_COL_ID + "= ? ",
                new String[]{chapterID});

        if (res == -1) {
            return false;
        }
        return true;
    }

    public boolean createNewQuestion(String question, String answer, String parentChapter) {
        int parent = Integer.parseInt(parentChapter);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(QUESTIONS_COL_QUESTION, question);
        contentValues.put(QUESTIONS_COL_ANSWER, answer);
        contentValues.put(QUESTIONS_COL_PARENTCHAPTER, parent);

        long res = db.insert(QUESTIONS_TABLE_NAME, null, contentValues);

        if (res == -1) {
            return false;
        }
        return true;
    }

    public boolean updateQuestion(String question, String answer, String questionID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(QUESTIONS_COL_QUESTION, question);
        contentValues.put(QUESTIONS_COL_ANSWER, answer);

        long res = db.update(QUESTIONS_TABLE_NAME, contentValues, QUESTIONS_COL_ID + " = ?", new String[]{questionID});

        if (res == -1) {
            return false;
        }

        return true;
    }


    @SuppressLint("Range")
    public ArrayList<ArrayList<String>> getAllQuestions(String parentChapter) {
//        int parent = Integer.parseInt(parentSubject);
        ArrayList<ArrayList<String>> chapters = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + QUESTIONS_TABLE_NAME + " WHERE " + QUESTIONS_COL_PARENTCHAPTER + " = " + parentChapter + " ORDER BY " + QUESTIONS_COL_ID + " ASC", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            ArrayList<String> row = new ArrayList<String>();

            row.add(String.valueOf(res.getInt(res.getColumnIndex(QUESTIONS_COL_ID))));
            row.add(res.getString(res.getColumnIndex(QUESTIONS_COL_QUESTION)));
            row.add(res.getString(res.getColumnIndex(QUESTIONS_COL_ANSWER)));
            chapters.add(row);
            res.moveToNext();
        }

        return chapters;
    }


    public boolean deleteQuestion(String questionID) {
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete(QUESTIONS_TABLE_NAME,
                QUESTIONS_COL_ID + "= ? ",
                new String[]{questionID});

        if (res == -1) {
            return false;
        }
        return true;
    }

    @SuppressLint("Range")
    public ArrayList<String> getQuestion(String questionID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + QUESTIONS_TABLE_NAME + " WHERE " + QUESTIONS_COL_ID + " = " + questionID, null);
        res.moveToFirst();

        String question = res.getString(res.getColumnIndex(QUESTIONS_COL_QUESTION));
        String answer = res.getString(res.getColumnIndex(QUESTIONS_COL_ANSWER));

        ArrayList<String> data = new ArrayList<>();
        data.add(question);
        data.add(answer);

        return data;

    }

    @SuppressLint("Range")
    public String getSubject(String subjectID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + SUBJECTS_TABLE_NAME + " WHERE " + SUBJECTS_COL_ID + " = " + subjectID, null);
        res.moveToFirst();

        return res.getString(res.getColumnIndex(SUBJECTS_COL_NAME));
    }

    @SuppressLint("Range")
    public String getChapter(String chapterID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + CHAPTERS_TABLE_NAME + " WHERE " + CHAPTERS_COL_ID + " = " + chapterID, null);
        res.moveToFirst();

        return res.getString(res.getColumnIndex(CHAPTERS_COL_NAME));
    }

    @SuppressLint("Range")
    public String getChapterParent(String chapterID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + CHAPTERS_TABLE_NAME + " WHERE " + CHAPTERS_COL_ID + " = " + chapterID, null);
        res.moveToFirst();

        return res.getString(res.getColumnIndex(CHAPTERS_COL_PARENTSUBJECT));
    }

    @SuppressLint("Range")
    public ArrayList<ArrayList<String>> getQuestions(String subjectID, String chapterID) {
        ArrayList<ArrayList<String>> qaList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        if (subjectID.equals("-1") && chapterID.equals("-1"))
            res = db.rawQuery("SELECT * FROM " + QUESTIONS_TABLE_NAME, null);
        else if (chapterID.equals("-1")) {
            return getQuestionsFromSubject(subjectID);
        } else
            res = db.rawQuery("SELECT * FROM " + QUESTIONS_TABLE_NAME + " WHERE " + QUESTIONS_COL_PARENTCHAPTER + " = " + chapterID, null);

        res.moveToFirst();


        while (!res.isAfterLast()) {
            ArrayList<String> row = new ArrayList<String>();

            row.add(String.valueOf(res.getInt(res.getColumnIndex(QUESTIONS_COL_ID))));
            row.add(res.getString(res.getColumnIndex(QUESTIONS_COL_QUESTION)));
            row.add(res.getString(res.getColumnIndex(QUESTIONS_COL_ANSWER)));
            qaList.add(row);
            res.moveToNext();
        }

        return qaList;
    }

    @SuppressLint("Range")
    private ArrayList<ArrayList<String>> getQuestionsFromSubject(String subjectID) {
        ArrayList<ArrayList<String>> qaList = new ArrayList<>();
        ArrayList<ArrayList<String>> chapterList = getAllChapters(subjectID);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;

        for (int i = 0; i < chapterList.size(); i++) {
            res = db.rawQuery("SELECT * FROM " + QUESTIONS_TABLE_NAME + " WHERE " + QUESTIONS_COL_PARENTCHAPTER + " = " + chapterList.get(i).get(0), null);
            res.moveToFirst();

            while (!res.isAfterLast()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add(String.valueOf(res.getInt(res.getColumnIndex(QUESTIONS_COL_ID))));
                row.add(res.getString(res.getColumnIndex(QUESTIONS_COL_QUESTION)));
                row.add(res.getString(res.getColumnIndex(QUESTIONS_COL_ANSWER)));
                qaList.add(row);
                res.moveToNext();
            }

        }

        return qaList;
    }
}
