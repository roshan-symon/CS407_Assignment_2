package com.wisc.ganz.calendary;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateEvent extends AppCompatActivity {

    private EditText et_titleText;
    private EditText et_startDateText;
    private EditText et_endDateText;
    private EditText et_startTimeText;
    private EditText et_endTimeText;
    private EditText et_descriptionText ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeAndSetListeners();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extractDataAndAddEvent();
            }
        });
    }
    private void initializeAndSetListeners(){
        et_titleText = (EditText)findViewById(R.id.editText_title);
        et_startDateText = (EditText)findViewById(R.id.editText_start_date);
        et_endDateText = (EditText)findViewById(R.id.editText_end_date);
        et_startTimeText = (EditText)findViewById(R.id.editText_start_time);
        et_endTimeText = (EditText)findViewById(R.id.editText_end_time);
        et_descriptionText = (EditText)findViewById(R.id.editText_description);

        EditTextDatePicker startDatePicker = new EditTextDatePicker(this, R.id.editText_start_date);
        EditTextDatePicker endDatePicker = new EditTextDatePicker(this, R.id.editText_end_date);

        setOnClickListenerHelper(et_startTimeText);
        setOnClickListenerHelper(et_endTimeText);

    }

    public void setOnClickListenerHelper(final EditText et){
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                //Display a 24-hour format timepicker dialog and set the edit text on selection
                mTimePicker = new TimePickerDialog(CreateEvent.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        et.setText( String.format("%02d",selectedHour) + ":" +
                                String.format("%02d",selectedMinute));
                    }
                }, hour, minute, true);

                mTimePicker.show();
            }
        });
    }
    /***
     * Adds an event with the specified arguments to the calendar
     * @param title         Event Title
     * @param startMillis   Start time of event in milliseconds
     * @param endMillis     End time of event in milliseconds
     * @param description   Event Description
     */
    private void addEvent(String title, long startMillis, long endMillis, String description) {
        long calID = 5;

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

        String et_title = et_titleText.getText().toString().trim();
        String et_startDate = et_startDateText.getText().toString().trim();
        String et_startTime = et_startTimeText.getText().toString().trim();
        String et_endDate = et_endDateText.getText().toString().trim();
        String et_endTime = et_endTimeText.getText().toString().trim();
        String et_description = et_descriptionText.getText().toString().trim();

        if(et_title.length() == 0 || et_startDate.length() == 0 || et_startTime.length() == 0
                || et_endDate.length() == 0 || et_endTime.length() == 0){
            Toast.makeText(this, R.string.missing_date_message, Toast.LENGTH_LONG).show();
            return;
        }

        String startDateString = et_startDate + " " + et_startTime;
        String endDateString = et_endDate + " " + et_endTime;

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm"); //Custom time format

        Date startDate = null;
        Date endDate = null;
        try {
            startDate = format.parse(startDateString);
            endDate = format.parse(endDateString);
        } catch (ParseException e) {
            Log.e("Parse Exception", "Error : "+e.getMessage());
        }

        long startMillis = startDate.getTime();
        long endMillis = endDate.getTime();

        //If the event is in reverse chronological order, do not add
        if(startMillis > endMillis){
            Toast.makeText(this, R.string.reverse_date_message, Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, et_title + " " + startDateString +" " +
        et_endDate + " " + et_endTime+ " " + et_description, Toast.LENGTH_LONG).show();

        addEvent(et_title, startMillis, endMillis, et_description);
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
