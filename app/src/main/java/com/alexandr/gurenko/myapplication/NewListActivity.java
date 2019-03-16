package com.alexandr.gurenko.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewListActivity extends Activity implements View.OnClickListener {

    List<String> shoppingList;
    EditText editText;
    ListView listView;
    Button add_item, save_list;
    ArrayAdapter<String> adapter;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);
        shoppingList = new ArrayList<>();
        editText = findViewById(R.id.new_item);
        listView = findViewById(R.id.item_list);
        add_item = findViewById(R.id.add_item);
        save_list = findViewById(R.id.save_list);
        add_item.setOnClickListener(this);
        save_list.setOnClickListener(this);
        databaseHelper = new DatabaseHelper(this);
        editText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onClick(View v) {

        db = databaseHelper.getWritableDatabase();

        switch (v.getId()){
            case R.id.add_item:
                if(editText.getText().toString().isEmpty()) {
                    Toast.makeText(this, "А что покупать-то будем?", Toast.LENGTH_LONG).show();
                    return;
                }
                shoppingList.add(editText.getText().toString());
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shoppingList);
                listView.setAdapter(adapter);
                editText.getText().clear();
                break;
            case R.id.save_list:
                if(shoppingList.isEmpty()) {
                    Toast.makeText(this, "Список пуст. Сохранять нечего.", Toast.LENGTH_LONG).show();
                    return;
                }

                final ListName tmp = new ListName();

                final EditText alertEditText = new EditText(this);
                alertEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

                new AlertDialog.Builder(this)
                        .setIconAttribute(android.R.attr.dialogIcon)
                        .setTitle("Сохранить список")
                        .setMessage("Как назовем этот список?")
                        .setView(alertEditText)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tmp.setName(alertEditText.getText().toString());
                                if(tmp.getName().isEmpty()){
                                    String currentTime = DateFormat.getDateTimeInstance().format(new Date());
                                    tmp.setName(currentTime);
                                }
                                ContentValues cv = new ContentValues();
                                cv.put(DatabaseHelper.LIST_NAME, tmp.getName());
                                db.insertOrThrow(DatabaseHelper.TABLE_LIST_NAME, null, cv);

                                try {
                                    Cursor cursor = db.query(DatabaseHelper.TABLE_LIST_NAME, null, null, null, null, null, null);
                                    if (cursor.moveToFirst()) {
                                        do {
                                            tmp.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.LIST_NAME_ID)));
                                        } while (cursor.moveToNext());
                                    }
                                    cursor.close();

                                    if (tmp.getId() == 0) {
                                        Toast.makeText(getBaseContext(), "Упс, что-то пошло не так...", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                }catch (Exception e){
                                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                                try {
                                    for (int i = 0; i < shoppingList.size(); i++) {
                                        final ContentValues contentValues = new ContentValues();
                                        contentValues.put(DatabaseHelper.ITEM_NAME, shoppingList.get(i));
                                        contentValues.put(DatabaseHelper.ITEM_LN_ID, tmp.getId());
                                        db.insertOrThrow(DatabaseHelper.TABLE_LIST_ITEM, null, contentValues);
                                    }
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Intent intent = new Intent(NewListActivity.this, ExistingListActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getBaseContext(), "Список сохранён!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Нет", null)
                        .show()
                        .getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                break;
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(NewListActivity.this)
                        .setIconAttribute(android.R.attr.alertDialogIcon)
                        .setTitle("Удаление")
                        .setMessage("Удалить эту покупку?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                shoppingList.remove(position);
                                adapter = new ArrayAdapter<>(NewListActivity.this, android.R.layout.simple_list_item_1, shoppingList);
                                listView.setAdapter(adapter);
                            }
                        })
                        .setNegativeButton("Нет", null)
                        .show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();

    }


}
