package com.bilkentazure.evenuclub.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Zeyad Khaled and Rana Elbatanony on 1/5/2018.
 * Adding events to database.
 * @author Zeyad Khaled, Rana Elbatanony
 * @version 1/5/2018
 */
public class AddEventFragment extends Fragment {

	private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";
	private static final String TAG_DATETIME_FRAGMENT_2 = "TAG_DATETIME_FRAGMENT_2";
	private static final String TAG = "Sample";

	private EditText editEventName;
	private EditText editEventDate;
	private EditText editGePoints;
	private EditText editLocation;
	private EditText editDescription;
	private EditText editTags;
	private Button addButton;
	private FirebaseFirestore db;
	private String club_id;
	private String name;
	private String image;
	private String description;
	private String location;
	private Date from;
	private Date to;
	private int ge_point;
	private ArrayList<String> tags;
	private SwitchDateTimeDialogFragment fromDateTimeFragment;
	private SwitchDateTimeDialogFragment dateTimeFragment2;
	private TextView fromText;
	private TextView toText;
	private Button selectFrom;
	private Button selectTo;

	/**
	 * Implement Date/Time picker and process them into date objects
	 *
	 * Make needed checks before processing add event ex: all fields are filled and valid.
	 *
	 * Fix the layout mainly displaying the date and time pickers
	 */


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_addevent, container, false);
		db = FirebaseFirestore.getInstance();

		club_id = "from database"; //Retrieve from database somehow using current user credentials

		editEventName = view.findViewById(R.id.edt_eventName);
		editGePoints = view.findViewById(R.id.edt_gePoints);
		editLocation = view.findViewById(R.id.edt_location);
		editDescription = view.findViewById(R.id.edt_description);
		editTags = view.findViewById(R.id.edt_tags);


		fromText = view.findViewById(R.id.fromTextView);
		toText = view.findViewById(R.id.toTextView);
		selectFrom = view.findViewById(R.id.selectFrom);
		selectTo = view.findViewById(R.id.selectTo);


		// Construct SwitchDateTimePicker for the from date
		fromDateTimeFragment = (SwitchDateTimeDialogFragment) getFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
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
				from = date;
			}

			@Override
			public void onNegativeButtonClick(Date date) {
				// Do nothing
			}
		});

		View.OnClickListener from = new FromOnClickListener();
		selectFrom.setOnClickListener(from);
		fromText.setOnClickListener(from);



		// Construct SwitchDateTimePicker for the to date
		dateTimeFragment2 = (SwitchDateTimeDialogFragment) getFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT_2);
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
				to = date;
			}

			@Override
			public void onNegativeButtonClick(Date date) {
				// Do nothing
			}
		});


		View.OnClickListener to = new ToOnClickListener();
		selectTo.setOnClickListener(to);
		toText.setOnClickListener(to);


		//Initializing the properties, btw they could be empty so before submitting run your checks *IMPORTANT*
		name = editEventName.getText().toString();
		ge_point = Integer.parseInt(editEventName.getText().toString() + "1");
		location = editLocation.getText().toString();
		description = editDescription.getText().toString();
		String unsplitTags = editTags.getText().toString();
		tags = new ArrayList<>(Arrays.asList(unsplitTags.split(",")));


		//Before accepting this onClick process the fields to make sure they are filled.
		addButton = view.findViewById(R.id.addButton);
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			addEvent();
			}
		});


		return view;
	}

	public void addEvent(){

		DocumentReference ref = db.collection("_events").document();
		String eventId = ref.getId();

		Event event = new Event(eventId,club_id,name,"image_url",description,location,from,to,ge_point,tags,tags,"","","999");
		//db.collection("_events").document(eventId).set(event); //Disable for now cuz no need to add events to db now

		Toast.makeText(getContext(), "Event has been added", //add to oncomplete listener
				Toast.LENGTH_LONG).show();

		Intent intent = new Intent(getContext(), MainActivity.class);
		startActivity(intent); // After adding it should be viewed in home page

	}


	public class ToOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View view) {
			// Re-init each time
			dateTimeFragment2.startAtCalendarView();
			dateTimeFragment2.setDefaultDateTime(new GregorianCalendar(2017, Calendar.MARCH, 4, 15, 20).getTime());
			dateTimeFragment2.show(getFragmentManager(), TAG_DATETIME_FRAGMENT_2);
		}
	}

	public class FromOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View view) {
			// Re-init each time
			fromDateTimeFragment.startAtCalendarView();
			fromDateTimeFragment.setDefaultDateTime(new GregorianCalendar(2017, Calendar.MARCH, 4, 15, 20).getTime());
			fromDateTimeFragment.show(getFragmentManager(), TAG_DATETIME_FRAGMENT);
		}
	}


}
