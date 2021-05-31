
package com.example.idiary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.idiary.model.Lesson;
import com.example.idiary.adapter.LessonAdapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public static DataBaseHelper dataBase;
    private ArrayList<Lesson> listLesson = new ArrayList<>();
    private LessonAdapter adapter;
    private LocalDate currentDate;
    private String currentDateString;

    private TextView hintText;
    private ImageButton btPreviousDate;
    private Button btCurrentDate;
    private ImageButton btNextDate;
    private Calendar calendar;

    private static final int EDIT_DAY = 1;
    private static final int ADD_SUBJECT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataBase = new DataBaseHelper(this);

        if (dataBase.selectAllSubjects().isEmpty()) {
            Intent i = new Intent(MainActivity.this, AddSubjectsActivity.class);
            i.putExtra("create", "create");
            startActivity(i);
        }

        hintText = (TextView) findViewById(R.id.hintAddSubject);

        btCurrentDate = (Button) findViewById(R.id.btSetDate);
        btPreviousDate = (ImageButton) findViewById(R.id.btPreviousDate);
        btNextDate = (ImageButton) findViewById(R.id.btNextDate);
        calendar = Calendar.getInstance();

        adapter = new LessonAdapter(this, listLesson);
        ListView listView = (ListView) findViewById(R.id.listViewLessons);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Lesson lesson = (Lesson) parent.getItemAtPosition(position);
                String oldHomework = lesson.getHomeWork();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final EditText editText = new EditText(MainActivity.this);
                editText.setText(oldHomework);

                builder.setTitle(R.string.enter_homework)
                        .setView(editText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                lesson.setHomeWork(editText.getText().toString());
                                dataBase.update(lesson);
                                updateList();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null);
                builder.show();
            }
        });

        currentDate = LocalDate.now();
        setDate(currentDate);

        btCurrentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, d,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        btPreviousDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate = currentDate.minusDays(1);
                setDate(currentDate);
            }
        });

        btNextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate = currentDate.plusDays(1);
                setDate(currentDate);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editDay:
                Intent i1 = new Intent(MainActivity.this, EditDayActivity.class);
                i1.putExtra("date", currentDate.toEpochDay());
                i1.putExtra("dateString", currentDateString);
                startActivityForResult(i1, EDIT_DAY);
                return true;
            case R.id.addSubject:
                Intent i2 = new Intent(MainActivity.this, AddSubjectsActivity.class);
                i2.putExtra("update", "update");
                startActivityForResult(i2, ADD_SUBJECT);
                return true;
            case R.id.deleteDay:
                dataBase.deleteLessonsByDate(currentDate);
                listLesson.clear();
                updateList();
                return true;
            case R.id.exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            currentDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
            setDate(currentDate);
        }
    };

    private void setDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String strDate = date.format(formatter);
        String dayOfWeek = date.getDayOfWeek().toString();
        String dayOfWeekRus = DayOfWeekRus.valueOf(dayOfWeek).shortRus;
        currentDateString = strDate + " " + dayOfWeekRus;
        btCurrentDate.setText(currentDateString);
        listLesson = dataBase.selectLessonsByDate(date);

        if (listLesson.isEmpty()) {

            LocalDate datePreWeek = date.minusDays(7);
            ArrayList<Lesson> list = dataBase.selectLessonsByDate(datePreWeek);

            if (!list.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle(R.string.question)
                        .setMessage(R.string.use_previous_week)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Lesson lesson;

                                for (int i = 0; i < list.size(); i++) {
                                    lesson = list.get(i);
                                    lesson.setHomeWork(null);
                                    lesson.setDone(false);
                                    listLesson.add(lesson);
                                    dataBase.insertLesson(date, lesson.getName(), lesson.getNum(), null, false);
                                }
                                updateList();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null);
                builder.show();
            }
        }
        updateList();
    }

    private void updateList() {
        adapter.clear();
        if (listLesson.isEmpty()) {
            hintText.setText(R.string.hint_add_table);
        } else {
            hintText.setText("");
            adapter.addAll(listLesson);
        }
        adapter.notifyDataSetInvalidated();
    }

     
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == EDIT_DAY) {
                listLesson = (ArrayList<Lesson>) data.getExtras().getSerializable("lessons");
                updateList();
            }
            else {
                if (data.hasExtra("delete")) {
                    listLesson.clear();
                    updateList();
                }
                if (data.hasExtra("change")) {
                    listLesson = dataBase.selectLessonsByDate(currentDate);
                    updateList();
                }
            }
        }
    }
}

