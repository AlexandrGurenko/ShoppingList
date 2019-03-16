package com.alexandr.gurenko.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // находим кнопки
        Button button_new_list = findViewById(R.id.new_list_btn);
        Button button_existing_list = findViewById(R.id.existing_list_btn);

        // присваиваем слушателя
        button_new_list.setOnClickListener(this);
        button_existing_list.setOnClickListener(this);
    }

    // обработка нажатия кнопок
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.new_list_btn:
                intent = new Intent(MainActivity.this, NewListActivity.class);
                startActivity(intent);
                break;
            case R.id.existing_list_btn:
                intent = new Intent(MainActivity.this, ExistingListActivity.class);
                startActivity(intent);
                break;

        }

    }

    private static long back_pressed;

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Toast.makeText(getBaseContext(), R.string.exit_message, Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
}
