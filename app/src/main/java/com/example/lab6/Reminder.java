package com.example.lab6;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Reminder {
    private int id;
    private String title;
    private String message;
    private long date;

    // Конструктор
    public Reminder(int id, String title, String message, long date) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.date = date;
    }

    // Геттеры
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public long getDate() {
        return date;
    }

    public String getFormattedDate() {
        Date dateObject = new Date(date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM HH:mm", Locale.getDefault());
        return sdf.format(dateObject);
    }

    @Override
    public String toString() {
        return title + " - " + getFormattedDate(); 
    }
}
