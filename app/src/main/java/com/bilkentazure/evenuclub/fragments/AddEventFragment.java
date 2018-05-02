package com.bilkentazure.evenuclub.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Zeyad Khaled and Rana Elbatanony on 1/5/2018.
 * Adding events to database.
 * @author Zeyad Khaled, Rana Elbatanony
 * @version 1/5/2018
 */
public class AddEventFragment extends Fragment {


	private EditText editEventName;
	private EditText editEventDate;
	private EditText editGePoints;
	private EditText editLocation;
	private EditText editDescription;
	private EditText editTags;
	private DatePicker dateFrom;
	private DatePicker dateTo;
	private TimePicker fromPick;
	private TimePicker toPick;
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

		Date date = new Date(); //until you get a from and to dates

		DocumentReference ref = db.collection("_events").document();
		String eventId = ref.getId();

		Event event = new Event(eventId,club_id,name,"image_url",description,location,date,date,ge_point,tags,tags,"","","999");
		//db.collection("_events").document(eventId).set(event); //Disable for now cuz no need to add events to db now

		Toast.makeText(getContext(), "Event has been added",
				Toast.LENGTH_LONG).show();

		Intent intent = new Intent(getContext(), MainActivity.class);
		startActivity(intent); // After adding it should be viewed in home page

	}


}
