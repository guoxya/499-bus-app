package com.example.reminder;

import com.example.reminder.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
 
/**
 * --FOR TESTING PURPOSE--
 * This is the first view when the test app is started.
 */
public class MainView extends Activity {
 
    Button btnScheduelReminder;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
 
        btnScheduelReminder = (Button) findViewById(R.id.btnScheduleReminder); 
        btnScheduelReminder.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {
            	startActivity(new Intent(MainView.this, ReminderSettingsView.class));
            }
        });
    }
 
}