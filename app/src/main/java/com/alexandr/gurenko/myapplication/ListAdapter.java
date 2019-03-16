package com.alexandr.gurenko.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<ListItem> objects;

    ListAdapter(Context context, ArrayList<ListItem> products){
        this.context = context;
        objects = products;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // количество элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункты списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null)
            view = layoutInflater.inflate(R.layout.item, parent, false);

        ListItem listItem = getProduct(position);

        TextView tmp =  view.findViewById(R.id.tvItemName);
        tmp.setText(listItem.getItem());
        CheckBox checkBox = view.findViewById(R.id.checkBoxItem);
        checkBox.setOnCheckedChangeListener(myCheckChangeList);
        checkBox.setTag(position);

        if(checkBox.isChecked())
            tmp.setPaintFlags(tmp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        return view;
    }

    ListItem getProduct(int position){
        return (ListItem) getItem(position);
    }

    OnCheckedChangeListener myCheckChangeList = new OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        }
    };
}
