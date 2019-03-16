package com.alexandr.gurenko.myapplication;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MyAdapter extends ArrayAdapter<String> {

    List<String> stringList;

    public MyAdapter(Context context, List<String> items) {
        super(context, android.R.layout.simple_list_item_1, items);
        stringList = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
        }
        if(stringList.isEmpty()) {
            Toast.makeText(getContext(), "return", Toast.LENGTH_SHORT).show();
            return null;
        }
        ((TextView)convertView.findViewById(R.id.test)).setText(stringList.get(position));
        ((TextView)convertView).setPaintFlags(((TextView)convertView).getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

        return convertView;
    }
}
