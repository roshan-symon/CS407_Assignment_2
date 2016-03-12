package com.wisc.ganz.calendary;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.provider.CalendarContract.Events;

import java.util.Calendar;

public class CreateEvent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extractDataAndAddEvent();
            }
        });
    }

    /***
     * Adds an event with the specified arguments to the calendar
     * @param title
     * @param startMillis
     * @param endMillis
     * @param description
     */
    private void addEvent(String title, long startMillis, long endMillis, String description) {
        long calID = 5;

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2016, 2, 14, 7, 40);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2016, 2, 14, 8, 40);
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startMillis);
        values.put(Events.DTEND, endMillis);
        values.put(Events.TITLE, title);
        values.put(Events.DESCRIPTION, description);
        values.put(Events.CALENDAR_ID, calID);
        values.put(Events.EVENT_TIMEZONE, "America/Chicago");

        checkForAndRequestPermission();

        Uri uri = cr.insert(Events.CONTENT_URI, values);

        long eventID = Long.parseLong(uri.getLastPathSegment());
        Toast.makeText(this, "Event ID is" + eventID, Toast.LENGTH_LONG).show();
    }

    private void extractDataAndAddEvent(){
        EditText et_titleText = (EditText)findViewById(R.id.editText_title);
        EditText et_startDateText = (EditText)findViewById(R.id.editText_start_date);
        EditText et_endDateText = (EditText)findViewById(R.id.editText_end_date);
        EditText et_startTimeText = (EditText)findViewById(R.id.editText_start_time);
        EditText et_endTimeText = (EditText)findViewById(R.id.editText_end_time);
        EditText et_descriptionText = (EditText)findViewById(R.id.editText_description);

        String et_title = et_titleText.getText().toString();
        String et_startDate = et_startDateText.getText().toString();
        String et_startTime = et_startTimeText.getText().toString();
        String et_endDate = et_endDateText.getText().toString();
        String et_endTime = et_endTimeText.getText().toString();
        String et_description = et_descriptionText.getText().toString();

        Toast.makeText(this, et_title + " " + et_startDate + " " +et_startTime+" "+
        et_endDate + " " + et_endTime+ " " + et_description, Toast.LENGTH_LONG).show();

    }

    /***
     * Checks if Calendar Read/Write permission has been granted. If not,
     * Requests for it.
     */
    private void checkForAndRequestPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission_group.CALENDAR) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission_group.CALENDAR},
                    CalendarMainActivity.MY_PERMISSIONS_REQUEST_RW_CALENDAR);
            return;
        }
    }

}
