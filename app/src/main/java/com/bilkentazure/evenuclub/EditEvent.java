package com.bilkentazure.evenuclub;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import com.bilkentazure.evenuclub.MainActivity;
import com.bilkentazure.evenuclub.R;
import com.bilkentazure.evenuclub.models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Zeyad Khaled on 5/5/2018.
 * Adding events to database.
 * @author Zeyad Khaled
 * @version 5/5/2018
 */
public class EditEvent extends AppCompatActivity {

    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";
    private static final String TAG_DATETIME_FRAGMENT_2 = "TAG_DATETIME_FRAGMENT_2";
    private static final String TAG = "Sample";

    private EditText editEventName;
    private EditText editEventDate;
    private EditText editGePoints;
    private EditText editLocation;
    private EditText editDescription;
    private EditText editTags;
    private Button editButton;
    private FirebaseFirestore db;
    private String club_id;
    private String name;
    private String image;
    private String description;
    private String location;
    public Date from;
    public Date to;
    private int ge_point;
    private ArrayList<String> tags;
    private SwitchDateTimeDialogFragment fromDateTimeFragment;
    private SwitchDateTimeDialogFragment dateTimeFragment2;
    private TextView fromText;
    private TextView toText;
    private Button selectFrom;
    private Button selectTo;
    private  Event event;
    private Toolbar mToolbar;



    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        event = (Event)getIntent().getSerializableExtra("event");

        mToolbar = findViewById(R.id.edit_event_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(event.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        db = FirebaseFirestore.getInstance();

        club_id = "BIH"; //Retrieve from database somehow using current user credentials
        editEventName = findViewById(R.id.edit_event_edt_eventName);
        editGePoints = findViewById(R.id.edit_event_edt_gePoints);
        editLocation = findViewById(R.id.edit_event_edt_location);
        editDescription = findViewById(R.id.edit_event_edt_description);
        editTags = findViewById(R.id.edit_event_edt_tags);
        fromText = findViewById(R.id.edit_event_fromTextView);
        toText = findViewById(R.id.edit_event_toTextView);
        selectFrom =findViewById(R.id.edit_event_selectFrom);
        selectTo = findViewById(R.id.edit_event_selectTo);

        //Giving EditTexts previously entered values
        editEventName.setText(event.getName());
        editGePoints.setText(event.getGe_point() +"");
        editLocation.setText(event.getLocation());
        editDescription.setText(event.getDescription());
        editTags.setText(event.getTarget_interest());

        //Display formatted date
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");

		String from = dateFormat.format(event.getFrom());
        fromText.setText(from);

        String to = dateFormat.format(event.getTo());
        toText.setText(to);


        // Construct SwitchDateTimePicker for the from date
        fromDateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if(fromDateTimeFragment == null) {
            fromDateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    "Select Date & Time",
                    "Ok",
                    "Cancel"
            );
        }

        // Init format
        final SimpleDateFormat myDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", java.util.Locale.getDefault());
        // Assign unmodifiable values
        fromDateTimeFragment.set24HoursMode(false);
        fromDateTimeFragment.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        fromDateTimeFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());

        // Define new day and month format
        try {
            fromDateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }

        fromDateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                fromText.setText(myDateFormat.format(date));
                //from = date;
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Do nothing
            }
        });

        View.OnClickListener fromListener = new FromOnClickListener();
        selectFrom.setOnClickListener(fromListener);
        fromText.setOnClickListener(fromListener);



        // Construct SwitchDateTimePicker for the to date
        dateTimeFragment2 = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT_2);
        if(dateTimeFragment2 == null) {
            dateTimeFragment2 = SwitchDateTimeDialogFragment.newInstance(
                    "Select Date & Time",
                    "Ok",
                    "Cancel"
            );
        }


        dateTimeFragment2.set24HoursMode(false);

        // Define new day and month format
        try {
            dateTimeFragment2.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }

        dateTimeFragment2.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                toText.setText(myDateFormat.format(date));
                //to = date;
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Do nothing
            }
        });


        View.OnClickListener toListener = new ToOnClickListener();
        selectTo.setOnClickListener(toListener);
        toText.setOnClickListener(toListener);


        //Before accepting this onClick process the fields to make sure they are filled.
        editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEvent();
            }
        });


    }

    /**
     * Retrieve data from event object and allow user to edit them
     */
    public void editEvent(){

        if(TextUtils.isEmpty(editEventName.getText().toString()) ){
            Toast.makeText(EditEvent.this, "name!", Toast.LENGTH_SHORT).show();
        }
//        else if (from == null){
//            Toast.makeText(EditEvent.this, "From!", Toast.LENGTH_SHORT).show();
//
//        }
//        else if (to == null){
//            Toast.makeText(EditEvent.this, "To!", Toast.LENGTH_SHORT).show();
//
//        }
        else if (TextUtils.isEmpty( editGePoints.getText().toString()) ){
            Toast.makeText(EditEvent.this, "GE!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty( editLocation.getText().toString() )){
            Toast.makeText(EditEvent.this, "Location!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(editDescription.getText().toString())){
            Toast.makeText(EditEvent.this, "Description!", Toast.LENGTH_SHORT).show();
        } else {

            name = editEventName.getText().toString();
            ge_point = Integer.parseInt(editGePoints.getText().toString());
            location = editLocation.getText().toString();
            description = editDescription.getText().toString();
//            String unsplitTags = editTags.getText().toString();
//            tags = new ArrayList<>(Arrays.asList(unsplitTags.split(",")));

            Event updateEvent = new Event(event.getId(), event.getClub_id(), name, image, description, location,  event.getFrom(), event.getTo(),  ge_point,  "", "", "1", event.getClub_id(), event.getTarget_department(), event.getTarget_interest());
            db.collection("_events").document(event.getId()).set(updateEvent);

            Toast.makeText(getApplicationContext(), "Event has been edited successfully!",
                    Toast.LENGTH_LONG).show();

            onPause();
        }
    }

    /**
     * Return the event object to EventView Activity by calling an intent
     */
    public void onPause() {
        super.onPause();
        Intent intent = new Intent(EditEvent.this , EventView.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }


    public class ToOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            // Re-init each time
            dateTimeFragment2.startAtCalendarView();
            dateTimeFragment2.setDefaultDateTime(new GregorianCalendar(2017, Calendar.MARCH, 4, 15, 20).getTime());
            dateTimeFragment2.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT_2);
        }
    }

    public class FromOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            // Re-init each time
            fromDateTimeFragment.startAtCalendarView();
            fromDateTimeFragment.setDefaultDateTime(new GregorianCalendar(2017, Calendar.MARCH, 4, 15, 20).getTime());
            fromDateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
        }
    }


}
