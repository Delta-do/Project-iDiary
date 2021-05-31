package com.example.idiary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.idiary.R;
import com.example.idiary.model.Subject;

import java.util.ArrayList;

public class SubjectAdapter extends ArrayAdapter<Subject> {

    public SubjectAdapter(Context context, ArrayList<Subject> list) {
        super(context, R.layout.subject_item, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Subject subject = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.subject_item, null);
        }

        ((TextView) convertView.findViewById(R.id.numS)).setText(String.valueOf(subject.getId()));
        ((TextView) convertView.findViewById(R.id.nameS)).setText(subject.getName());

        return convertView;
    }
}
