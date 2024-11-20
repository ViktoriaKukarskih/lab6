package com.example.lab6;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ReminderDatabaseHelper dbHelper;
    private ArrayAdapter<Reminder> adapter;
    private ArrayList<Reminder> reminderList;
    private ArrayList<Integer> reminderIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new ReminderDatabaseHelper(this);
        reminderList = dbHelper.getAllReminders();
        reminderIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reminderList);
        ListView listView = findViewById(R.id.reminderListView);
        listView.setAdapter(adapter);
        try {
            loadReminders();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        findViewById(R.id.addReminderButton).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddReminderActivity.class));
        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Reminder selectedReminder = reminderList.get(position);
            Intent intent = new Intent(MainActivity.this, ReminderDetailActivity.class);
            intent.putExtra("title", selectedReminder.getTitle());
            startActivity(intent);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            // Получаем напоминание для удаления
            Reminder reminderToDelete = reminderList.get(position);

            // Удаляем из базы данных
            dbHelper.deleteReminder(reminderToDelete.getId());

            // Удаляем из списка и обновляем адаптер
            runOnUiThread(() -> {
                reminderList.remove(position);
                adapter.notifyDataSetChanged();// Оповещаем адаптер об изменениях
                try {
                    loadReminders();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });

            // Уведомление об удалении
            Toast.makeText(MainActivity.this, "Напоминание удалено", Toast.LENGTH_SHORT).show();
            return true; // Указываем, что событие обработано
        });


    }

    private void loadReminders() throws ParseException {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reminderList);
        ListView listView = findViewById(R.id.reminderListView);
        listView.setAdapter(adapter);
    }
}
