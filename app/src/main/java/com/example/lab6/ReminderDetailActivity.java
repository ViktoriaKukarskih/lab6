package com.example.lab6;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReminderDetailActivity extends AppCompatActivity {
    private ReminderDatabaseHelper dbHelper; // Объявите объект для работы с БД

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_detail);

        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView messageTextView = findViewById(R.id.messageTextView);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");

        // Инициализируем DatabaseHelper
        dbHelper = new ReminderDatabaseHelper(this);
        String message = dbHelper.getMessageByTitle(title); // Получаем сообщение по заголовку

        titleTextView.setText(title);
        messageTextView.setText(message != null ? message : ""); // Отображаем сообщение
    }
}
