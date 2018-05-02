package com.bilkentazure.evenuclub;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegisterActivity extends AppCompatActivity {


	private static WebView browser;
	private WebSettings browserSettings;
    private FirebaseFirestore db;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

//		db = FirebaseFirestore.getInstance();

		browser = (WebView) findViewById(R.id.web_view);
		browserSettings = browser.getSettings();
	    browserSettings.setJavaScriptEnabled(true);
		browser.loadUrl("https://docs.google.com/forms/d/e/1FAIpQLSd7xIsO5LesQaetXySa0mdwjtaatRMrjPE4wEQPpngafo9Hjw/viewform?usp=sf_link");
	}

}
