package com.alexandr.gurenko.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddItemActivity extends Activity implements View.OnClickListener {

    List<String> shoppingList;
    EditText editText;
    ListView listView;
    Button add_item, save_list;
    ArrayAdapter<String> adapter;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    static int currentListNameId;
    static String string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        shoppingList = new ArrayList<>();
        editText = findViewById(R.id.add_new_item);
        listView = findViewById(R.id.add_item_list);
        add_item = findViewById(R.id.add_add_item);
        save_list = findViewById(R.id.add_save_list);
        add_item.setOnClickListener(this);
        save_list.setOnClickListener(this);
        databaseHelper = new DatabaseHelper(this);
        editText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        Intent intent = getIntent();
        currentListNameId = intent.getIntExtra("currentListNameId", 0);
        string = intent.getStringExtra("listName");
    }

    @Override
    public void onClick(View v) {
        db = databaseHelper.getWritableDatabase();

        switch (v.getId()){
            case R.id.add_add_item:
                if(editText.getText().toString().isEmpty()) {
                    Toast.makeText(this, "А что добавлять-то будем?", Toast.LENGTH_LONG).show();
                    return;
                }
                shoppingList.add(editText.getText().toString());
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shoppingList);
                listView.setAdapter(adapter);
                editText.getText().clear();
                break;
            case R.id.add_save_list:
                if(shoppingList.isEmpty()) {
                    Toast.makeText(this, "Список пуст. Добавлять нечего.", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    for (int i = 0; i < shoppingList.size(); i++) {
                        final ContentValues contentValues = new ContentValues();
                        contentValues.put(DatabaseHelper.ITEM_NAME, shoppingList.get(i));
                        contentValues.put(DatabaseHelper.ITEM_LN_ID, currentListNameId);
                        db.insertOrThrow(DatabaseHelper.TABLE_LIST_ITEM, null, contentValues);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(AddItemActivity.this, ExistingListActivity.class);
                intent.putExtra("currentListNameId", currentListNameId);
                intent.putExtra("listName", string);
                startActivity(intent);
                finish();
                Toast.makeText(getBaseContext(), "Добавили!", Toast.LENGTH_SHORT).show();
                break;
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(AddItemActivity.this)
                        .setIconAttribute(android.R.attr.alertDialogIcon)
                        .setTitle("Удаление")
                        .setMessage("Удалить эту покупку?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                shoppingList.remove(position);
                                adapter = new ArrayAdapter<>(AddItemActivity.this, android.R.layout.simple_list_item_1, shoppingList);
                                listView.setAdapter(adapter);
                            }
                        })
                        .setNegativeButton("Нет", null)
                        .show();
            }
        });

    }
}
