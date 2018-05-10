package com.bilkentazure.evenuclub;

import android.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.bilkentazure.evenuclub.models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Zeyad Khaled on 1/5/2018.
 * Event view activity that is opened using an intent with an event object.
 * Event object is then used to display relevant information.
 * @author Zeyad Khaled
 * @version 1/5/2018
 */

public class EventView extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView txtTitle;
    private TextView txtInfo;
    private TextView txtDate;
    private TextView txtLocation;
    private TextView txtGE;
    private TextView txtClub;
    private Button edit;
    private Button delete;
    private Button generateList;
    private Button generateQR;
    private FirebaseFirestore db;
    String headerData;
    String rowData;
    String excelData;
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);
        event = (Event)getIntent().getSerializableExtra("event");

        db = FirebaseFirestore.getInstance();
        mToolbar = findViewById(R.id.event_view_toolbar);
        setSupportActionBar(mToolbar);
      getSupportActionBar().setTitle(event.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtTitle = findViewById(R.id.event_view_txt_title);
        txtInfo =findViewById(R.id.event_view_txt_info);
        txtDate = findViewById(R.id.event_view_txt_date);
        txtLocation = findViewById(R.id.event_view_txt_location);
        txtGE = findViewById(R.id.event_view_txt_ge);
        txtClub = findViewById(R.id.event_view_txt_club);

        txtTitle.setText(event.getName());
        txtInfo.setText(event.getDescription());
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy\n'Time: ' H:m");
        String date = dateFormat.format(event.getFrom());
        txtDate.setText(date);
        txtLocation.setText(event.getLocation());
        txtGE.setText(event.getGe_point() + " Points");
        txtClub.setText(event.getClub_id());


        edit = findViewById(R.id.edit_event);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , EditEvent.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }
        });

        delete = findViewById(R.id.delete_event);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(EventView.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(EventView.this);
                }
                builder.setTitle("Delete Event")
                        .setMessage("Are you sure you want to delete this Event?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                db.collection("_events").document(event.getId()).delete();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(EventView.this, "Event Deleted!",
                                        Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(EventView.this, "Delete revoked!",
                                        Toast.LENGTH_LONG).show();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });

        generateList = findViewById(R.id.generate_list_event);
        generateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateList("CAg0auj0noTKi2GOtKtZ");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });

        generateQR = findViewById(R.id.generate_qr_event);
        generateQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EventView.this , GenerateQR.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }
        });

    }

    public void generateList(String eventID) {

        //Initializing my excel file with necessary data
        headerData =   "\"Name\",\"School ID\",\"Email\"";
        excelData = headerData + "\n";

        //Query DB for attendees sub collection
        db.collection("_events").document(eventID).collection("attendees").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    //Get data from every document and write it to the excel file
                    for ( QueryDocumentSnapshot document : task.getResult()) {
                        //Log.e("x",document.getString("name"));
                        rowData   =   "\"" + document.getString("name") +"\",\"" + document.getString("schoolID") + "\",\"" + document.getString("email") + "\"";
                        excelData = excelData + rowData +  "\n";
                    }

                    //Check for write permission
                    if (ActivityCompat.checkSelfPermission(EventView.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(EventView.this , new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE} , 1 );

                        //If available then continue execution
                    } else if (ActivityCompat.checkSelfPermission(EventView.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());

                        //Initialize a file
                        File file   = null;
                        File root   = Environment.getExternalStorageDirectory();

                        //Create file and write to it
                        if ( root.canWrite()){
                            File dir    =   new File (root.getAbsolutePath() + "/EventAttendees");
                            dir.mkdirs();
                            file   =   new File(dir, "event name here" + ".csv");
                            FileOutputStream out   =   null;
                            try {
                                out = new FileOutputStream(file);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            try {
                                out.write(excelData.getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            try {
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        //Open an email intent and retrieve the file from storage.
                        Uri u1  =   null;
                        u1  =   Uri.fromFile(file);
                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Attendees List");
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Attendees List");
                        sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
                        sendIntent.setType("text/html");
                        startActivity(sendIntent);
                    }
                } else {
                    Toast.makeText(EventView.this, "An attendee list doesn't exist for this event",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
