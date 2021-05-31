package com.example.idiary.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.idiary.R;

import java.util.ArrayList;

public class SelectionAdapter extends ArrayAdapter<String> {

    private Context context;
    private int textViewResourceId;
    private ArrayList<String> objects;

    public SelectionAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = View.inflate(context, textViewResourceId, null);

        TextView tv = (TextView) convertView;
        String text = objects.get(position);
        tv.setText(text);

        if (text.equals(context.getString(R.string.chose_subject))) {
            tv.setTextColor(Color.GRAY);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        // don't display last item. It is used as hint.
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }
}
