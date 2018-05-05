package com.bilkentazure.evenuclub.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bilkentazure.evenuclub.ProfileImage;
import com.bilkentazure.evenuclub.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


	private static final int SELECT_PHOTO = 100;
	private ImageView profileImage;
	private TextView myEvents;

	private Bitmap imageBmap;


	public ProfileFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_profile, container, false);

		profileImage = view.findViewById(R.id.profile_image);

		profileImage.buildDrawingCache();
		final Bitmap bmap = profileImage.getDrawingCache();

		//have to get image from database and set it in view


		profileImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

//				Intent intent = new Intent(getActivity(), ProfileImage.class);
//				intent.getStringExtra("image", getEncoded64ImageStringFromBitmap( bmap ));
//				startActivity(intent);

				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO);

			}
		});


		myEvents = view.findViewById(R.id.event_txt_myevents);

		myEvents.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				HomeFragment nextFrag= new HomeFragment();
				getActivity().getSupportFragmentManager().beginTransaction()
						.replace(R.id.profileLayout, nextFrag,"findThisFragment")
						.addToBackStack(null)
						.commit();

			}
		});



		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch(requestCode) {
			case SELECT_PHOTO:
				if(resultCode == getActivity().RESULT_OK) {
					Uri selectedImage = imageReturnedIntent.getData();
					InputStream imageStream = null;
					try {
						imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
					profileImage.setImageBitmap(yourSelectedImage);

					//here i have to save image to database;


				}
			}
		}


	public String BitMapToString(Bitmap bitmap){
		ByteArrayOutputStream baos=new  ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
		byte [] b=baos.toByteArray();
		String temp=Base64.encodeToString(b, Base64.DEFAULT);
		return temp;
	}

}
