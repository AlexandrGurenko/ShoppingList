package com.alexandr.gurenko.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ExistingListActivity extends AppCompatActivity implements View.OnClickListener {

    Button back;
    ListView listNameView, listItemView;
    Cursor cursor;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    TextView nameList;
    boolean inList;
    static String string;
    static int currentListNameId;
    static int callbackAddItem;

    List<ListName> listNames;
    List<ListItem> listItems;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_list);
        listNameView = findViewById(R.id.existing_list_name);
        listItemView = findViewById(R.id.existing_list_item);
        listItemView.setVisibility(View.GONE);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        nameList = findViewById(R.id.name_list);
        nameList.setVisibility(View.GONE);
        databaseHelper = new DatabaseHelper(getBaseContext());
        listNames = new ArrayList<>();
        listItems = new ArrayList<>();
        inList = false;

        Intent intent = getIntent();
        callbackAddItem = intent.getIntExtra("currentListNameId", 0);
        string = intent.getStringExtra("listName");
        if(callbackAddItem != 0){
            openList(callbackAddItem);
        }else {
            Load();
            if (listNames.isEmpty()) {
                Toast.makeText(ExistingListActivity.this, R.string.empty_list_name, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
    }

    private void openList(int callbackAddItem) {
        listItems.clear();

        db = databaseHelper.getWritableDatabase();
        cursor = db.query(DatabaseHelper.TABLE_LIST_ITEM, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                if(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ITEM_LN_ID)) == callbackAddItem) {
                    ListItem tmp = new ListItem();
                    tmp.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ITEM_ID)));
                    tmp.setItems(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ITEM_NAME)));
                    tmp.setListNameId(callbackAddItem);
                    listItems.add(tmp);
                }
            }while (cursor.moveToNext());
        }
        if(listItems.isEmpty()){
            Toast.makeText(ExistingListActivity.this, R.string.empty_list, Toast.LENGTH_SHORT).show();
        }else {

            currentListNameId = callbackAddItem;
            listItemAdapter(listItems);
        }
        listItemView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(ExistingListActivity.this)
                        .setIconAttribute(android.R.attr.dialogIcon)
                        .setTitle("Покупка")
                        .setMessage(listItems.get(position).getItem() + " купили?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                db.delete(DatabaseHelper.TABLE_LIST_ITEM, DatabaseHelper.ITEM_ID + "=" + listItems.get(position).getId(), null);
                                listItems.remove(position);
                                listItemAdapter(listItems);
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        });
    }


    private void Load(){
        listNames.clear();

        db = databaseHelper.getWritableDatabase();

        cursor = db.query(DatabaseHelper.TABLE_LIST_NAME, null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                ListName tmp = new ListName();
                tmp.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.LIST_NAME_ID)));
                tmp.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LIST_NAME)));
                listNames.add(tmp);
            }while (cursor.moveToNext());
            if(listNames.isEmpty()) {
                Toast.makeText(ExistingListActivity.this, R.string.empty_list_name, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

            listNameAdapter(listNames);

            listNameView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    listItems.clear();
                    cursor = db.query(DatabaseHelper.TABLE_LIST_ITEM, null, null, null, null, null, null);
                    if(cursor.moveToFirst()){
                        do{
                            if(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ITEM_LN_ID)) == listNames.get(position).getId()) {
                                ListItem tmp = new ListItem();
                                tmp.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ITEM_ID)));
                                tmp.setItems(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ITEM_NAME)));
                                tmp.setListNameId(listNames.get(position).getId());
                                listItems.add(tmp);
                            }
                        }while (cursor.moveToNext());
                    }
                    if(listItems.isEmpty()){
                        Toast.makeText(ExistingListActivity.this, R.string.empty_list, Toast.LENGTH_SHORT).show();
                    }else {
                        string = listNames.get(position).getName();
                        currentListNameId = listNames.get(position).getId();
                        listItemAdapter(listItems);
                    }
                }
            });

            listNameView.setLongClickable(true);
            listNameView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                    new AlertDialog.Builder(ExistingListActivity.this)
                            .setIconAttribute(android.R.attr.alertDialogIcon)
                            .setTitle("Удаление списка")
                            .setMessage("Вы уверены, что хотите навсегда удалить этот список?")
                            .setPositiveButton("Да", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    db.delete(DatabaseHelper.TABLE_LIST_NAME, DatabaseHelper.LIST_NAME_ID + "=" + listNames.get(position).getId(), null);
                                    db.delete(DatabaseHelper.TABLE_LIST_ITEM, DatabaseHelper.ITEM_LN_ID + "=" + listNames.get(position).getId(), null);
                                    listNames.remove(position);
                                    listNameAdapter(listNames);
                                }
                            })
                            .setNegativeButton("Нет", null)
                            .show();
                    return true;
                }
            });


            listItemView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    new AlertDialog.Builder(ExistingListActivity.this)
                            .setIconAttribute(android.R.attr.dialogIcon)
                            .setTitle("Покупка")
                            .setMessage(listItems.get(position).getItem() + " купили?")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    db.delete(DatabaseHelper.TABLE_LIST_ITEM, DatabaseHelper.ITEM_ID + "=" + listItems.get(position).getId(), null);
                                    listItems.remove(position);
                                    listItemAdapter(listItems);
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                }
            });
    }


    private void listItemAdapter(List<ListItem> listItems){
        invalidateOptionsMenu();
        inList = true;
        nameList.setVisibility(View.VISIBLE);
        listNameView.setVisibility(View.GONE);
        listItemView.setVisibility(View.VISIBLE);
        nameList.setText(string);

        MyAdapter maa = null;
        List<String> tmp = new ArrayList<>();
        if(listItems != null){
            for (int i = 0; i < listItems.size(); i++){
                tmp.add(listItems.get(i).getItem());
            }
            adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, tmp);
            maa = new MyAdapter(this, tmp);
        }
        listItemView.setAdapter(maa);
    }

    private void listNameAdapter(List<ListName> listNames){
        invalidateOptionsMenu();
        inList = false;
        listNameView.setVisibility(View.VISIBLE);
        listItemView.setVisibility(View.GONE);
        nameList.setVisibility(View.GONE);

        List<String> tmp = new ArrayList<>();
        if(listNames != null) {
            for (int i = 0; i < listNames.size(); i++) {
                tmp.add(listNames.get(i).getName());
            }
            adapter = new ArrayAdapter<>(this, R.layout.style, tmp);
        }
        listNameView.setAdapter(adapter);
    }

    @Override
     public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                if(inList){
                    inList = false;
                    Load();
                } else {
                    finish();
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(inList){
            menu.add(R.string.add_items_in_list);
            menu.add(R.string.del_list);
        } else {
            menu.add(R.string.create_new_list);
            menu.add(R.string.del_all_lists);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // R.string.add_items_in_list
        if(item.getTitle() == getResources().getString(R.string.add_items_in_list)) {
            Intent intent = new Intent(ExistingListActivity.this, AddItemActivity.class);
            intent.putExtra("currentListNameId", currentListNameId);
            intent.putExtra("listName", string);
            startActivity(intent);
            finish();
        }

        // R.string.del_list
        if(item.getTitle() == getResources().getString(R.string.del_list)){
            new AlertDialog.Builder(ExistingListActivity.this)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle("Удаление списка")
                    .setMessage("Вы уверены, что хотите навсегда удалить этот список?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            db.delete(DatabaseHelper.TABLE_LIST_NAME, DatabaseHelper.LIST_NAME_ID + "=" + currentListNameId, null);
                            db.delete(DatabaseHelper.TABLE_LIST_ITEM, DatabaseHelper.ITEM_LN_ID + "=" + currentListNameId, null);
                            Load();
                            if(listNames.isEmpty())
                                finish();

                        }
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        }

        // R.string.create_new_list
        if(item.getTitle() == getResources().getString(R.string.create_new_list)){
            startActivity(new Intent(ExistingListActivity.this, NewListActivity.class));
            finish();
        }

        // menu.add(R.string.del_all_lists);
        if(item.getTitle() == getResources().getString(R.string.del_all_lists)){
            new AlertDialog.Builder(this)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle("Удаление списка")
                    .setMessage("Вы уверены, что хотите навсегда удалить все списки?")
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.delete(DatabaseHelper.TABLE_LIST_NAME,null,null);
                            db.delete(DatabaseHelper.TABLE_LIST_ITEM, null, null);
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (inList) {
            inList = false;
            listNameAdapter(listNames);
        }
        else
            finish();

    }

}

