package com.example.clouds;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;


public class MapList extends ListActivity {

	private List<String> fileList = new ArrayList<String>();

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		//get selected items
		String selectedValue = (String) getListAdapter().getItem(position);

		Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();

		String dd = readFileFromInternalStorage(selectedValue);

		Intent newIntent = new Intent(MapList.this, Activity1.class);
		Log.d("String", dd);
		newIntent.putExtra("vector", dd);

		startActivity(newIntent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		File root = new File(getFilesDir().getAbsolutePath());
		ListDir(root);
	}

	void ListDir(File f){

		File[] files = f.listFiles();
		fileList.clear();
		for (File file : files){
			String fname = file.getName();
			if (fname.contains("harta"))
				fileList.add(fname);  
		}

		ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, fileList);
		setListAdapter(directoryList); 
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
				builder.append(line + "\n");
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
