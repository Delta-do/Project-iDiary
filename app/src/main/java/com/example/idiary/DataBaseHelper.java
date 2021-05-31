package com.example.idiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.idiary.model.Lesson;
import com.example.idiary.model.Subject;

import java.time.LocalDate;
import java.util.ArrayList;

public class DataBaseHelper {

    private static final String DATABASE_NAME = "iDiaryDataBase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME_LESSONS = "lessons";
    private static final String COLUMN_LESSONS_ID = "_id";
    private static final String COLUMN_LESSONS_DATE = "date";
    private static final String COLUMN_LESSONS_NAME = "name_id";
    private static final String COLUMN_LESSONS_NUM = "num";
    private static final String COLUMN_LESSONS_HOMEWORK = "homework";
    private static final String COLUMN_LESSONS_DONE = "is_done";

    private static final int NUM_COLUMN_LESSONS_ID = 0;
    private static final int NUM_COLUMN_LESSONS_DATE = 1;
    private static final int NUM_COLUMN_LESSONS_NAME = 2;
    private static final int NUM_COLUMN_LESSONS_NUM = 3;
    private static final int NUM_COLUMN_LESSONS_HOMEWORK = 4;
    private static final int NUM_COLUMN_LESSONS_DONE = 5;

    private static final String TABLE_NAME_SUBJECTS = "subjects";
    private static final String COLUMN_SUBJECTS_ID = "_id";
    private static final String COLUMN_SUBJECTS_NAME = "name";

    private static final int NUM_COLUMN_SUBJECTS_ID = 0;
    private static final int NUM_COLUMN_SUBJECTS_NAME = 1;

    private SQLiteDatabase mDataBase;
    private OpenHelper mOpenHelper;

    public DataBaseHelper(Context context) {
        mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }

    public long insertLesson(LocalDate date, String name, int num, String homework, boolean isDone) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LESSONS_DATE, date.toEpochDay());
        Subject subject = selectSubjectByName(name);
        cv.put(COLUMN_LESSONS_NAME, subject.getId());
        cv.put(COLUMN_LESSONS_NUM, num);
        cv.put(COLUMN_LESSONS_HOMEWORK, homework);
        int is_done = 0;
        if (isDone) {
            is_done = 1;
        }
        cv.put(COLUMN_LESSONS_DONE, is_done);
        return mDataBase.insert(TABLE_NAME_LESSONS, null, cv);
    }

    public long insertSubject(String name) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SUBJECTS_NAME, name);
        return mDataBase.insert(TABLE_NAME_SUBJECTS, null, cv);
    }

    public Lesson selectLesson(long id) {
        Cursor cursorLessons = mDataBase.query(TABLE_NAME_LESSONS, null, COLUMN_LESSONS_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        cursorLessons.moveToFirst();

        long dateLong = cursorLessons.getLong(NUM_COLUMN_LESSONS_DATE);
        LocalDate date = LocalDate.ofEpochDay(dateLong); //день с 1970

        int name_id = cursorLessons.getInt(NUM_COLUMN_LESSONS_NAME);
        Cursor cursorSubjects = mDataBase.query(TABLE_NAME_SUBJECTS, null, COLUMN_SUBJECTS_ID + " = ?",
                new String[]{String.valueOf(name_id)}, null, null, null);
        cursorSubjects.moveToFirst();
        String name = cursorSubjects.getString(NUM_COLUMN_SUBJECTS_NAME);

        int num = cursorLessons.getInt(NUM_COLUMN_LESSONS_NUM);

        String homework = cursorLessons.getString(NUM_COLUMN_LESSONS_HOMEWORK);

        int isDoneInt = cursorLessons.getInt(NUM_COLUMN_LESSONS_DONE);
        boolean isDone = false;
        if (isDoneInt == 1)
            isDone = true;

        return new Lesson(id, date, name, num, homework, isDone);
    }

    public ArrayList<Lesson> selectLessonsByDate(LocalDate date) {
        Cursor cursor = mDataBase.query(TABLE_NAME_LESSONS, null, COLUMN_LESSONS_DATE + " = ?",
                new String[]{String.valueOf(date.toEpochDay())}, null, null, COLUMN_LESSONS_NUM);

        ArrayList<Lesson> arr = new ArrayList<>();
        cursor.moveToFirst();

        if (!cursor.isAfterLast()) {
            do {
                long id = cursor.getLong(NUM_COLUMN_LESSONS_ID);
                arr.add(selectLesson(id));
            } while (cursor.moveToNext());
        }
        return arr;
    }

    public ArrayList<Lesson> selectAllLessons() {
        Cursor cursor = mDataBase.query(TABLE_NAME_LESSONS, null, null, null, null, null, null);

        ArrayList<Lesson> arr = new ArrayList<>();
        cursor.moveToFirst();

        if (!cursor.isAfterLast()) {
            do {
                long id = cursor.getLong(NUM_COLUMN_LESSONS_ID);
                arr.add(selectLesson(id));
            } while (cursor.moveToNext());
        }
        return arr;
    }

    public Subject selectSubject(long id) {
        Cursor cursor = mDataBase.query(TABLE_NAME_SUBJECTS, null, COLUMN_SUBJECTS_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();

        String name = cursor.getString(NUM_COLUMN_SUBJECTS_NAME);
        return new Subject(id, name);
    }

    public Subject selectSubjectByName(String name) {
        Cursor cursor = mDataBase.query(TABLE_NAME_SUBJECTS, null, COLUMN_SUBJECTS_NAME + " = ?",
                new String[]{String.valueOf(name)}, null, null, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) return null;

        long id = cursor.getLong(NUM_COLUMN_SUBJECTS_ID);
        return new Subject(id, name);
    }

    public ArrayList<Subject> selectAllSubjects() {
        Cursor cursor = mDataBase.query(TABLE_NAME_SUBJECTS, null, null, null, null, null, null);

        ArrayList<Subject> arr = new ArrayList<>();
        cursor.moveToFirst();

        if (!cursor.isAfterLast()) {
            do {
                long id = cursor.getLong(NUM_COLUMN_SUBJECTS_ID);
                String name = cursor.getString(NUM_COLUMN_SUBJECTS_NAME);
                arr.add(new Subject(id, name));
            } while (cursor.moveToNext());
        }
        return arr;
    }

    public int update(Lesson lesson) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LESSONS_DATE, lesson.getDate().toEpochDay());
        cv.put(COLUMN_LESSONS_NAME, selectSubjectByName(lesson.getName()).getId());
        cv.put(COLUMN_LESSONS_NUM, lesson.getNum());
        cv.put(COLUMN_LESSONS_HOMEWORK, lesson.getHomeWork());
        int isDoneInt = 0;
        if (lesson.isDone()) isDoneInt = 1;
        cv.put(COLUMN_LESSONS_DONE, isDoneInt);
        return mDataBase.update(TABLE_NAME_LESSONS, cv, COLUMN_LESSONS_ID + " = ?",
                new String[] { String.valueOf(lesson.getId())});
    }

    public int update(Subject subject) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SUBJECTS_NAME, subject.getName());
        return mDataBase.update(TABLE_NAME_SUBJECTS, cv, COLUMN_SUBJECTS_ID + " = ?",
                new String[] { String.valueOf(subject.getId())});
    }

    public void deleteLesson(long id) {
        mDataBase.delete(TABLE_NAME_LESSONS, COLUMN_LESSONS_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public void deleteLessonsByDate(LocalDate date) {
        mDataBase.delete(TABLE_NAME_LESSONS, COLUMN_LESSONS_DATE + " = ?", new String[] { String.valueOf(date.toEpochDay()) });
    }

    public void deleteAllLessons() {
        mDataBase.delete(TABLE_NAME_LESSONS, null, null);
    }

    public void deleteSubject(long id) {
        mDataBase.delete(TABLE_NAME_SUBJECTS, COLUMN_SUBJECTS_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public void deleteAllSubjects() {
        mDataBase.delete(TABLE_NAME_SUBJECTS, null, null);
    }

    public void deleteAll() {
        mOpenHelper.onUpgrade(mDataBase, 1, 2);
    }

    private class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE " + TABLE_NAME_LESSONS + " (" +
                    COLUMN_LESSONS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_LESSONS_DATE + " INTEGER, " +
                    COLUMN_LESSONS_NAME + " INTEGER, " +
                    COLUMN_LESSONS_NUM + " INTEGER, "+
                    COLUMN_LESSONS_HOMEWORK +" TEXT, " +
                    COLUMN_LESSONS_DONE + " INTEGER, " +
                    "FOREIGN KEY (" + COLUMN_LESSONS_NAME + ") REFERENCES " + TABLE_NAME_SUBJECTS + " (" + COLUMN_SUBJECTS_ID + "))";
            db.execSQL(query);

            query = "CREATE TABLE " + TABLE_NAME_SUBJECTS + " (" +
                    COLUMN_SUBJECTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SUBJECTS_NAME + " TEXT);";
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LESSONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SUBJECTS);
            onCreate(db);
        }
    }
}

