package com.example.clouds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.example.clouds.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class StartMenu extends Activity{

	@Override
    protected void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   this.setContentView(R.layout.start_menu);
	   LinearLayout ll0 = (LinearLayout)findViewById(R.id.newTHbtn1);
	   Button create = new Button(this);
	   create.setText("Create Quest");
	   ll0.addView(create);
	   create.setOnClickListener(new OnClickListener() {

    	   @Override
    	   public void onClick(View v) {
    		   Intent harta = new Intent(StartMenu.this,MapMarkerActivity.class);
    		   String filename = getNewFilename();
    		   harta.putExtra("Map", filename);
    		   startActivity(harta);
    		   
    	   }
	   });
	   LinearLayout ll1 = (LinearLayout)findViewById(R.id.playTHbtn);
	   Button play = new Button(this);
	   play.setText("Play Quest");
	   ll1.addView(play);
	   play.setOnClickListener(new OnClickListener() {

    	   @Override
    	   public void onClick(View v) {	   
    		   Intent myIntent = new Intent(StartMenu.this, MapList.class);
	    	   Log.d("MSG", "Deschide nou activity");
    		   startActivity(myIntent);
    	   }
	   });
	}	
	
	String getNewFilename(){
		
		ArrayList<String> fileList = new ArrayList<String>();
		
		File f = new File(getFilesDir().getAbsolutePath());
		
		File[] files = f.listFiles();
		fileList.clear();
		for (File file : files){
			
			String fname = file.getName();
			if (fname.contains("harta_"))
				fileList.add(fname);   
		}
		
		if (fileList.size()!=0){
			
			String filename = fileList.get(0);
			String[] aux = filename.split("_");
			char[] c = aux[1].toCharArray();
			String aux2 = "";
			
			for (int i = 0; i < c.length; i++)
				if (c[i] != '.')
					aux2 += c[i];
				else
					break;
			
			Log.d("aux", aux2);
			int no = Integer.parseInt(aux2);
			
			Log.d("nume", "harta_"+(no++)+".txt");
			
			return "harta_"+(no++)+".txt";
		}
		
		else return "harta_1.txt";
		
	}	

}
