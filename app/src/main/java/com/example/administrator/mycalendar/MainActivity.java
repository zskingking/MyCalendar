package com.example.administrator.mycalendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    MyCalendarView calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendar = (MyCalendarView) findViewById(R.id.calendarView);
        calendar.setDateCallBack(new MyCalendarView.DateCallBack() {
            @Override
            public void onClick(String date) {
                Toast.makeText(MainActivity.this,"当前日期为："+ date,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
