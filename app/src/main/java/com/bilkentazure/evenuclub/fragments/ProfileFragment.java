package com.bilkentazure.evenuclub.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bilkentazure.evenuclub.ProfileImage;
import com.bilkentazure.evenuclub.R;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


	private static final int SELECT_PHOTO = 100;
	private ImageView profileImage;


	public ProfileFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_profile, container, false);

		profileImage = view.findViewById(R.id.profile_image);

//		profileImage.buildDrawingCache();
//		final Bitmap bmap = profileImage.getDrawingCache();






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



		return view;
	}

//
//	public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
//
//		ByteArrayOutputStream stream = new ByteArrayOutputStream();
//		bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
//		byte[] byteFormat = stream.toByteArray();
//		// get the base 64 string
//		String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
//		return imgString;
//	}

}
