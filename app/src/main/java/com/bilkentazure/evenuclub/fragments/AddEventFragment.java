package com.bilkentazure.evenuclub.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bilkentazure.evenuclub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddEventFragment extends Fragment implements View.OnClickListener{


	private EditText editEventName;
	private EditText editEventDate;
	private EditText editGePoints;
	private EditText editLocation;
	private EditText editDescription;
	private EditText editTags;
	private Button addButton;
	private FirebaseFirestore db;



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_addevent, container, false);

		db = FirebaseFirestore.getInstance();

		editEventName = view.findViewById(R.id.edt_eventName);
		editEventDate = view.findViewById(R.id.edt_eventDate);
		editGePoints = view.findViewById(R.id.edt_gePoints);
		editLocation = view.findViewById(R.id.edt_location);
		editDescription = view.findViewById(R.id.edt_description);
		editTags = view.findViewById(R.id.edt_tags);

		addButton = view.findViewById(R.id.addButton);

		addButton.setOnClickListener(this);
		return view;
	}

	public void addEvent(){



	}

	@Override
	public void onClick(View v) {
		if( v == addButton){
			addEvent();
		}
	}
}
