package com.example.idiary.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.idiary.DataBaseHelper;
import com.example.idiary.MainActivity;
import com.example.idiary.R;
import com.example.idiary.model.Lesson;

import java.util.ArrayList;

public class LessonAdapter extends ArrayAdapter<Lesson> {

    DataBaseHelper dateBase = MainActivity.dataBase;

    int textColorDone = Color.GRAY;
    int textColorNotDone = Color.BLACK;

    public LessonAdapter(Context context, ArrayList<Lesson> list) {
        super(context, R.layout.lesson_item, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Lesson lesson = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lesson_item, null);
        }

        ((TextView) convertView.findViewById(R.id.nameL)).setText(lesson.getName());
        ((TextView) convertView.findViewById(R.id.numL)).setText(String.valueOf(lesson.getNum()));
        TextView homeWork = ((TextView) convertView.findViewById(R.id.hw));
        homeWork.setText(lesson.getHomeWork());
        boolean isDone = lesson.isDone();
        if (isDone) {
            homeWork.setTextColor(textColorDone);
        }

        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
        checkBox.setChecked(isDone);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lesson.setDone(isChecked);
                dateBase.update(lesson);
                if (isChecked) {
                    homeWork.setTextColor(textColorDone);
                } else {
                    homeWork.setTextColor(textColorNotDone);
                }
            }
        });
        return convertView;
    }
}
