package com.bilkentazure.evenuclub.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bilkentazure.evenuclub.EventView;
import com.bilkentazure.evenuclub.GenerateQR;
import com.bilkentazure.evenuclub.R;
import com.bilkentazure.evenuclub.models.Event;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


	//Firebase
	private FirebaseFirestore db;
//	private FirebaseAuth mAuth;
//	private FirebaseUser mUser;
	private FirestoreRecyclerAdapter mFirestoreRecyclerAdapter;
	private static final int REQUEST_CALENDAR = 0;
	private RecyclerView mRecyclerEvent;



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_home, container, false);

		db = FirebaseFirestore.getInstance();
//		mAuth = FirebaseAuth.getInstance();
//		mUser = mAuth.getCurrentUser();

		mRecyclerEvent = view.findViewById(R.id.fragment_home_recycler);

		mRecyclerEvent.setHasFixedSize(true);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(container.getContext());
		mRecyclerEvent.setLayoutManager(layoutManager);
		mRecyclerEvent.setItemAnimator(new DefaultItemAnimator());
		mRecyclerEvent.addItemDecoration(new DividerItemDecoration(container.getContext(), LinearLayoutManager.VERTICAL));



		Query query = db.collection("_events")
				.orderBy("from", Query.Direction.ASCENDING);


		FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
				.setQuery(query, Event.class)
				.build();

		mFirestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Event, EventHolder>(options) {
			@Override
			public void onBindViewHolder(final EventHolder holder, int position, final Event event) {


				final String id = event.getId();
				String club_id = event.getClub_id();
				String name = event.getName();
				String image = event.getImage();
				String description = event.getDescription();
				String location = event.getLocation();
				Date from = event.getFrom();
				Date to = event.getTo();
				int ge_point = event.getGe_point();
				ArrayList<String> tags = event.getTags();
				ArrayList<String> keywords = event.getKeywords();
				String qr_id = event.getQr_id();
				String spreadsheet = event.getSpreadsheet();
				String security_check = event.getSecurity_check();

				holder.setTitle(name);
				holder.setInfo(description);
				holder.setImage(image);
				holder.setLocation(location);
				holder.setDate(from);



                //Test Viewing events
                holder.btnView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getContext() , EventView.class);
                        startActivity(intent);
                    }
                });


                if( !( event.getClub_id().equals("BIH") )){
					holder.hide();
				}


				//Test Viewing events
				holder.btnView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						Intent intent = new Intent(getContext() , EventView.class);
						intent.putExtra("event", event);
						startActivity(intent);
					}
				});

			}

			@Override
			public EventHolder onCreateViewHolder(ViewGroup group, int i) {
				// Create a new instance of the ViewHolder, in this case we are using a custom
				// layout called R.layout.message for each item
				View view = LayoutInflater.from(group.getContext())
						.inflate(R.layout.event_list_item, group, false);



				return new EventHolder(view);
			}

		};

		mRecyclerEvent.setAdapter(mFirestoreRecyclerAdapter);

		return view;


	}


	public class EventHolder extends RecyclerView.ViewHolder {

		private View mView;
		private TextView txtTitle;
		private TextView txtInfo;
		private TextView txtDate;
		private TextView txtLocation;
		public RelativeLayout mainRlt;
		private ImageView imgEvent;
		public FloatingActionButton btnView;


		public EventHolder (View itemView) {
			super(itemView);
			mView = itemView;

			txtTitle = mView.findViewById(R.id.event_txt_title);
			txtInfo = mView.findViewById(R.id.event_txt_info);
			txtDate = mView.findViewById(R.id.event_txt_date);
			txtLocation = mView.findViewById(R.id.event_txt_location);
			mainRlt = mView.findViewById(R.id.event_list_rlt);
			imgEvent = mView.findViewById(R.id.event_img_event);
			btnView = mView.findViewById(R.id.click_event);
		}

		public void setTitle(String title) {
			txtTitle.setText(title);
		}

		public void setInfo(String info) {
			txtInfo.setText(info);
		}

		public void setDate(Date date) {
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy\nH:m");
			txtDate.setText(dateFormat.format(date));
		}

		public void setLocation(String location) {
			txtLocation.setText(location);
		}

		public void setImage(String image_url){
			//TODO set image to imageview
		}


		public void hide(){
			mainRlt.setVisibility(View.GONE);
			ViewGroup.LayoutParams layoutParams = mainRlt.getLayoutParams();
			layoutParams.height = 0;
			layoutParams.width = 0;
			mainRlt.setLayoutParams(layoutParams);
		}


	}

	@Override
	public void onStart() {
		super.onStart();
		mFirestoreRecyclerAdapter.startListening();
	}

	@Override
	public void onStop() {
		super.onStop();
		mFirestoreRecyclerAdapter.stopListening();
	}

}
