package com.example.idiary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idiary.adapter.SubjectAdapter;
import com.example.idiary.model.Subject;

import java.util.ArrayList;

public class AddSubjectsActivity extends AppCompatActivity {

    private TextView hintText;
    private EditText etSubject;
    private Button btOk, btSave;

    private ArrayList<Subject> listSubject;
    private SubjectAdapter adapter;

    DataBaseHelper dataBase = MainActivity.dataBase;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subjects);
        setTitle(R.string.add_subject_title);

        intent = getIntent();

        hintText = (TextView) findViewById(R.id.hintAddSubject);
        etSubject = (EditText) findViewById(R.id.editTextSubject);
        btOk = (Button) findViewById(R.id.buttonOk);
        btSave = (Button) findViewById(R.id.buttonSave);

        if (getIntent().hasExtra("update")) {
            listSubject = dataBase.selectAllSubjects();
        } else {
            listSubject = new ArrayList<>();
        }

        if (listSubject.isEmpty()) {
            hintText.setText(R.string.hint_list_subjects);
        }

        adapter = new SubjectAdapter(AddSubjectsActivity.this, listSubject);
        ListView listView = (ListView) findViewById(R.id.listViewSubjects);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Subject subject = (Subject) parent.getItemAtPosition(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(AddSubjectsActivity.this);

                final EditText editText = new EditText(AddSubjectsActivity.this);
                editText.setText(subject.getName());

                builder.setTitle(R.string.edit_subject)
                        .setView(editText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                subject.setName(editText.getText().toString());
                                dataBase.update(subject);
                                adapter.notifyDataSetInvalidated();
                                intent.putExtra("change", 0);
                                setResult(RESULT_OK, intent);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null);
                builder.show();
                return false;
            }
        });

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etSubject.getText().toString();
                long id = dataBase.insertSubject(name);

                listSubject.add(new Subject(id, name));
                adapter.notifyDataSetInvalidated();

                etSubject.setText("");
                hintText.setText("");
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteAll:
                dataBase.deleteAll();
                listSubject.clear();
                adapter.notifyDataSetInvalidated();
                hintText.setText(R.string.hint_list_subjects);
                intent.putExtra("delete", 0);
                setResult(RESULT_OK, intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
