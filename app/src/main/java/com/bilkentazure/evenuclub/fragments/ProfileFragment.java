package com.bilkentazure.evenuclub.fragments;


import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bilkentazure.evenuclub.MainActivity;
import com.bilkentazure.evenuclub.ProfileImage;
import com.bilkentazure.evenuclub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
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
	private StorageReference mStorageRef;

	private Bitmap imageBmap;
	private Uri imageUrl;
	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_profile, container, false);

		profileImage = view.findViewById(R.id.profile_image);

		mStorageRef = FirebaseStorage.getInstance().getReference("images").child("clubs").child("BIH");

		final long ONE_MEGABYTE = 1024 * 1024;
		mStorageRef.getBytes(ONE_MEGABYTE)
				.addOnSuccessListener(new OnSuccessListener<byte[]>() {
					@Override
					public void onSuccess(byte[] bytes) {
						Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
						DisplayMetrics dm = new DisplayMetrics();
						getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

						profileImage.setMinimumHeight(dm.heightPixels);
						profileImage.setMinimumWidth(dm.widthPixels);
						profileImage.setImageBitmap(bm);
					}
				});

		profileImage.buildDrawingCache();
		final Bitmap bmap = profileImage.getDrawingCache();

//		have to get image from database and set it in view
//		FirebaseFirestore.getInstance().collection("clubs").whereEqualTo("id","BIH").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//			@Override
//			public void onComplete(@NonNull Task<QuerySnapshot> task) {
//				if (task.isSuccessful()) {
//					for (QueryDocumentSnapshot document : task.getResult()) {
////						String img = document.get("image").toString();
////						System.out.print(img);
////						profileImage.setImageBitmap( StringToBitMap(img) );
////					}
////				} else {
////					task.getException();
////				}
////			}
////		});


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

				Intent i = new Intent(getActivity() , MainActivity.class);
//				i.putExtra("fragment_index_key", 0);
				startActivity(i);


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
					imageUrl = imageReturnedIntent.getData();
					InputStream imageStream = null;
					try {
						imageStream = getActivity().getContentResolver().openInputStream(imageUrl);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					final Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
					profileImage.setImageBitmap(yourSelectedImage);

					//here i have to save image to database;
					uploadFile();

//					FirebaseFirestore.getInstance().collection("clubs").whereEqualTo("id","BIH").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//						@Override
//						public void onComplete(@NonNull Task<QuerySnapshot> task) {
//							if (task.isSuccessful()) {
//								for (QueryDocumentSnapshot document : task.getResult()) {
//									document.getReference().update("image", BitMapToString(yourSelectedImage));
//								}
//							} else {
//								task.getException();
//							}
//						}
//					});

			}
		}
	}


	public void uploadFile(){
		if(imageUrl != null){
			StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUrl));

			fileReference.putFile(imageUrl)
//					.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//						@Override
//						public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//						}
//					})
			.addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {
					Toast.makeText(getActivity(), e.getMessage() , Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	public String BitMapToString(Bitmap bitmap){
		ByteArrayOutputStream baos=new  ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
		byte [] b=baos.toByteArray();
		String temp=Base64.encodeToString(b, Base64.DEFAULT);
		return temp;
	}

	public Bitmap StringToBitMap(String image){
		try{
			byte [] encodeByte=Base64.decode(image,Base64.DEFAULT);

			InputStream inputStream  = new ByteArrayInputStream(encodeByte);
			Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
			return bitmap;
		}catch(Exception e){
			e.getMessage();
			return null;
		}
	}

	public String getFileExtension( Uri uri ){
		ContentResolver cR = getActivity().getApplicationContext().getContentResolver();
		MimeTypeMap mime = MimeTypeMap.getSingleton();
		return mime.getExtensionFromMimeType(cR.getType(uri));
	}


}
