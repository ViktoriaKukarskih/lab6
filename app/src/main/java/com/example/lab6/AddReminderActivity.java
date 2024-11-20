package com.example.lab6;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddReminderActivity extends AppCompatActivity {
    private EditText titleEditText, messageEditText;
    private Button dateButton, timeButton, saveButton;
    private Calendar selectedDateTime;
    private ReminderDatabaseHelper dbHelper;
    private ArrayAdapter<Reminder> adapter;
    private ArrayList<Reminder> reminderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        dbHelper = new ReminderDatabaseHelper(this);
        titleEditText = findViewById(R.id.titleEditText);
        messageEditText = findViewById(R.id.messageEditText);
        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);
        saveButton = findViewById(R.id.saveButton);
        selectedDateTime = Calendar.getInstance();

        dateButton.setOnClickListener(v -> new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDateTime.set(year, month, dayOfMonth);
        }, selectedDateTime.get(Calendar.YEAR), selectedDateTime.get(Calendar.MONTH), selectedDateTime.get(Calendar.DAY_OF_MONTH)).show());

        timeButton.setOnClickListener(v -> new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDateTime.set(Calendar.MINUTE, minute);
        }, selectedDateTime.get(Calendar.HOUR_OF_DAY), selectedDateTime.get(Calendar.MINUTE), true).show());

        saveButton.setOnClickListener(v -> saveReminder());
    }

    private void saveReminder() {
        String title = titleEditText.getText().toString();
        String message = messageEditText.getText().toString();
        long dateTimeInMillis = selectedDateTime.getTimeInMillis();

        if (title.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполни все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("message", message);
        values.put("date_time", dateTimeInMillis);
        db.insert("reminders", null, values);

        // Запланировать уведомление
        scheduleReminder(title, message, dateTimeInMillis);
        reminderList.clear(); // Очищаем существующий список
        reminderList.addAll(dbHelper.getAllReminders()); // Загружаем новые данные
        adapter.notifyDataSetChanged();
        finish();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleReminder(String title, String message, long dateTimeInMillis) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Log.d("AddReminderActivity", "Setting alarm for: " + dateTimeInMillis);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, dateTimeInMillis, pendingIntent);
        } else {
            Log.e("AddReminderActivity", "AlarmManager is null");
        }
    }
}
