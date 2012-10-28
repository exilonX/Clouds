package com.example.clouds;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NameMessage extends Activity {
	EditText name, text;
	String fisier;
	String strpoint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_name_message);
		Intent intent = getIntent();
		fisier = intent.getStringExtra("Map");
		strpoint = intent.getStringExtra("Point");
		
		TextView tv = new TextView(this);
		name = (EditText) findViewById(R.id.name);
		text = (EditText) findViewById(R.id.text);
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1,1,1,"Finish").setIcon(android.R.drawable.btn_star);
		menu.add(1,2,2,"Next").setIcon(android.R.drawable.btn_star);
		return super.onCreateOptionsMenu(menu);

	}

	private void writeFileToInternalStorage(String name, String continut) {

		String eol = System.getProperty("line.separator");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(openFileOutput(name, MODE_APPEND)));
			writer.write(continut + eol);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String eol = System.getProperty("line.separator");

		switch(item.getItemId()){
		case 1 : 
		{
			Editable e = name.getText();
			String myname = e.toString();
			e = text.getText();
			String mytext = e.toString();
			writeFileToInternalStorage(fisier, strpoint + eol + myname + eol + mytext); 	
			
			// !! de scos
			String s = readFileFromInternalStorage(fisier);
			Log.d("Din fisier",s);

			finish();
			break;
		} 
		case 2 :
		{
			Editable e = name.getText();
			String myname = e.toString();
			e = text.getText();
			String mytext = e.toString();
			
			writeFileToInternalStorage(fisier, strpoint + eol + myname + eol + mytext); 	
			Intent myIntent = new Intent(NameMessage.this, MapMarkerActivity.class);
			
			myIntent.putExtra("Map", fisier);
			startActivity(myIntent);
			
			finish();
			break;
		}
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("unused")
	public String readFileFromInternalStorage(String name) {
		File directory = Environment.getExternalStorageDirectory();
		// Assumes that a file article.rss is available on the SD card
		String path = getFilesDir().getAbsolutePath();
		File file = new File(path + "/" + name);
		if (!file.exists()) {
			throw new RuntimeException("File not found");
		}
		Log.e("Testing", "Starting to read");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			return builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}

