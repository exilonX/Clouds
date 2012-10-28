package com.example.clouds;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Activity1 extends Activity implements LocationListener {
	int ok = 0;
	boolean flag = true;
	double distance;
	private ArrayList<Location> locatii;
	private ArrayList<String> messages;
	private ArrayList<String> names;
	private int i =0;
	private LocationManager locationManager;
	private String provider;
	private String locatie;
	private float[] valuesAccelerometer;
	private float[] valuesMagneticField;
	private Toast toast;
	private long lastBackPressTime = 0;

	private float[] matrixR;
	private float[] matrixI;
	private float[] matrixValues;

	private double myLat, myLng;

	Button button;
	private static final String TAG = "Compass";

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private SampleView mView;
	private float[] mValues;
	private int index = 0;
	private final SensorEventListener mListener = new SensorEventListener() {
		
		public void onSensorChanged(SensorEvent event) {
			if (flag){
				mValues = event.values;
				Location msj = new Location(SENSOR_SERVICE);
				Location mel = new Location(SENSOR_SERVICE);
				msj.setLatitude(locatii.get(index).getLatitude());
				msj.setLongitude(locatii.get(index).getLongitude());
				mel.setLatitude(myLat);
				mel.setLongitude(myLng);
				float bearing = mel.bearingTo(msj);
				mValues[0] -=  bearing;
				if (mView != null) {
					mView.invalidate();
				}
	
	
	
				switch(event.sensor.getType()){
				case Sensor.TYPE_ACCELEROMETER:
					for(int i =0; i < 3; i++){
						valuesAccelerometer[i] = event.values[i];
					}
					break;
				case Sensor.TYPE_MAGNETIC_FIELD:
					for(int i =0; i < 3; i++){
						valuesMagneticField[i] = event.values[i];
					}
					break;
				}
	
				boolean success = SensorManager.getRotationMatrix(
						matrixR,
						matrixI,
						valuesAccelerometer,
						valuesMagneticField);
	
				if(success){
					SensorManager.getOrientation(matrixR, matrixValues);
	
					//double azimuth = Math.toDegrees(matrixValues[0]);
					//double pitch = Math.toDegrees(matrixValues[1]);
					//double roll = Math.toDegrees(matrixValues[2]);
	
					System.out.println(matrixValues[0]);
				}
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//se incarca, coordonatele pentru care este relizat questul
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_clouds);
		
		//ce a facut andreea
		Intent intent = getIntent();
		String dd = intent.getStringExtra("vector");
		
		locatii = convert_locations(dd);

		//chestii cu locatia
		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the location provider -> use
		// default
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);

		// Initialize the location fields
		if (location != null) {
			System.out.println("Provider " + provider + " has been selected.");
			onLocationChanged(location);
		} else {
			locatie = "Location not available";
		}
		
		//gata cu locatia
		valuesAccelerometer = new float[3];
		valuesMagneticField = new float[3];

		matrixR = new float[9];
		matrixI = new float[9];
		matrixValues = new float[3];

		distance = distance (myLat, myLng, locatii.get(index).getLatitude(),locatii.get(index).getLongitude());
		Log.d("MyLocation",Double.toString(myLat) + Double.toString(myLng));
		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		locatie = Integer.toString((int)distance) + " m " + Double.toString(myLat) + " " + Double.toString(myLng);
		mView = new SampleView(this,names.get(index));


		//aici verificam daca era distanta mai mica decat 200
		LinearLayout mylin = (LinearLayout) findViewById(R.id.linear);
		mylin.addView(mView); 
	}

	public ArrayList<Location> convert_locations(String d){
		
			messages = new ArrayList<String>();
			names = new ArrayList<String>();
			
			String[] str = d.split("\n");
			
			ArrayList<Double> dd = new ArrayList<Double>() ;
			
			for (int i = 0; i < str.length - 3; i += 4){
				double x = Double.parseDouble(str[i]);
				if (x!=0)
					dd.add(x);
				
				x = Double.parseDouble(str[i+1]);
				if (x!=0)
					dd.add(x);
				
				names.add(str[i+2]);
				messages.add(str[i+3]);
					
			}
		   
		   ArrayList<Location> locations = new ArrayList<Location>();
		   
		   for (int i = 0; i < dd.size() - 1; i += 2){
			   Location l = new Location(ALARM_SERVICE);
			   l.setLatitude(dd.get(i));
			   l.setLongitude(dd.get(i+1));
			   locations.add(l);
		   }
		   
		   return locations;
		   }

	@Override
	protected void onResume()
	{
		if (Config.LOGD) Log.d(TAG, "onResume");
		super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
		mSensorManager.registerListener(mListener, mSensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onStop()
	{
		if (Config.LOGD) Log.d(TAG, "onStop");
		mSensorManager.unregisterListener(mListener);
		super.onStop();
	}



	public static double distance(double lat_a, double lng_a, double lat_b, double lng_b) {
		double pk = (double) (180/3.14169);

		double a1 = lat_a / pk;
		double a2 = lng_a / pk;
		double b1 = lat_b / pk;
		double b2 = lng_b / pk;

		double t1 = Math.cos(a1)*Math.cos(a2)*Math.cos(b1)*Math.cos(b2);
		double t2 = Math.cos(a1)*Math.sin(a2)*Math.cos(b1)*Math.sin(b2);
		double t3 = Math.sin(a1)*Math.sin(b1);
		double tt = Math.acos(t1 + t2 + t3);

		return 6366000*tt;
	}


	private class SampleView extends View {
		private Paint   mPaint = new Paint();
		private Path    mPath = new Path();
		private boolean mAnimate;
		String distance ;
		public SampleView(Context context,String distance) {
			super(context);

			// Construct a wedge-shaped path
			mPath.moveTo(0, -50);
			mPath.lineTo(-20, 60);
			mPath.lineTo(0, 50);
			mPath.lineTo(20, 60);
			mPath.close();
			this.distance = distance;
		}

		@Override 
		protected void onDraw(Canvas canvas) {
			Paint paint = mPaint;

			canvas.drawColor(Color.WHITE);

			paint.setAntiAlias(true);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.FILL);

			int w = canvas.getWidth();
			int h = canvas.getHeight();
			int cx = w / 2;
			int cy = h / 2;
			canvas.save();

			canvas.translate(cx, cy);
			if (mValues != null) {            
				canvas.rotate(-mValues[0]);
			}

			canvas.drawPath(mPath, mPaint);
			canvas.restore();
			Paint paint1 = new Paint(); 

			paint1.setColor(Color.BLACK); 
			paint1.setTextSize(25); 
			canvas.drawText(distance, 10, 25, paint1); 

		}

		@Override
		protected void onAttachedToWindow() {
			mAnimate = true;
			super.onAttachedToWindow();
		}

		@Override
		protected void onDetachedFromWindow() {
			mAnimate = false;
			super.onDetachedFromWindow();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		myLat = location.getLatitude();
		myLng = location.getLongitude();
		locatie = Double.toString(myLat) + " " + Double.toString(myLng);
		distance = distance(myLat, myLng, locatii.get(index).getLatitude(),locatii.get(index).getLongitude());
		//daca se ajunge la distanta < 200
		if (distance <= 200 && ok == 0) {
			ok = 1;
			LinearLayout ll = (LinearLayout)findViewById(R.id.btn);
			Button b = new Button(this);
			b.setText("Open camera");
			ll.addView(b);

			b.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent myIntent = new Intent(Activity1.this, CameraPreview.class);
					Log.d("MSG",messages.get(index));
					myIntent.putExtra("mesaj",messages.get(index));		
					flag = false;
					startActivity(myIntent);
					index++;
					if (names.size() <= index)
						finish();
					else
						flag = true;
				}
			});
		}
		Toast.makeText(this, "Distanta este " + distance  , Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onBackPressed() {
		if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
			toast = Toast.makeText(this, "Press back again to close this app", 4000);
			toast.show();
			this.lastBackPressTime = System.currentTimeMillis();
		} else {
			if (toast != null) {
				toast.cancel();
			}
			super.onBackPressed();
		}
	}
}
