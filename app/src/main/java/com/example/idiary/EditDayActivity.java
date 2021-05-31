package com.example.idiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idiary.adapter.SelectionAdapter;
import com.example.idiary.model.Lesson;
import com.example.idiary.model.Subject;

import java.time.LocalDate;
import java.util.ArrayList;

public class EditDayActivity extends AppCompatActivity {

    private TextView textDate;
    private Spinner[] spinners;
    private Button btSave;

    LocalDate currentDate;
    ArrayList<Subject> listSubject;
    ArrayList<Lesson> listLesson = new ArrayList<>();
    ArrayList<Lesson> listNewLesson = new ArrayList<>();

    SelectionAdapter adapter;
    boolean[] arrWas = new boolean[8];

    DataBaseHelper dataBase = MainActivity.dataBase;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_day);
        setTitle(R.string.edit_table);

        textDate = (TextView)findViewById(R.id.textDate);
        btSave = (Button)findViewById(R.id.btSave);

        currentDate = LocalDate.ofEpochDay(getIntent().getLongExtra("date",-1));

        String currentDateString = getIntent().getStringExtra("dateString");
        textDate.setText(currentDateString);

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                spinnersSave();
                intent.putExtra("lessons", listNewLesson);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        listSubject = dataBase.selectAllSubjects();

        ArrayList<String> listNames = new ArrayList<>();
        for (int i = 0; i < listSubject.size(); i++) {
            listNames.add(listSubject.get(i).getName());
        }
        listNames.add(getString(R.string.chose_subject));

        adapter = new SelectionAdapter(this, R.layout.spinner_item, listNames);

        listLesson = dataBase.selectLessonsByDate(currentDate);

        spinners = new Spinner[8];
        int[] ids = new int[]{R.id.spinner1, R.id.spinner2, R.id.spinner3, R.id.spinner4, R.id.spinner5,
                R.id.spinner6, R.id.spinner7, R.id.spinner8};
        Subject subject;

        for (int i = 0; i < spinners.length; i++) {
            spinners[i] = findViewById(ids[i]);
            spinners[i].setAdapter(adapter);

            if (listLesson.size() > i) {
                subject = dataBase.selectSubjectByName(listLesson.get(i).getName());
                spinners[i].setSelection((int)subject.getId() - 1); 
                arrWas[i] = true;
            } else {
                spinners[i].setSelection(adapter.getCount());

            }
        }
    }

    private void spinnersSave() {
        String nameSubject;
        long id;
        Lesson lesson;

        for (int i = 0; i < spinners.length; i++) {
            nameSubject = (String) spinners[i].getSelectedItem();

            if (!nameSubject.equals(getString(R.string.chose_subject))) {

                if (arrWas[i]) {
                    lesson = listLesson.get(i);
                    lesson.setName(nameSubject);
                    dataBase.update(lesson);
                } else {
                    id = dataBase.insertLesson(currentDate, nameSubject, i + 1, null, false);
                    lesson = new Lesson(id, currentDate, nameSubject, i + 1, null, false);
                }

                listNewLesson.add(lesson);
            }
        }
    }
}
