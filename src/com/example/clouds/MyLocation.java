package com.example.clouds;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MyLocation  {
	Context mContext;
	LocationManager mlocManager;
	MyLocationListener mlocListener;
	double lat=0,lng=0;

	public MyLocation(Context mContext) {
		super();
		
		this.mContext = mContext;
		mlocManager = (LocationManager)mContext.getSystemService(mContext.LOCATION_SERVICE);
		mlocListener = new MyLocationListener();
		lat = mlocListener.getLat();
		Log.d("Locatie", Double.toString(mlocListener.getLat()));
		lng = mlocListener.getLng();
		mlocManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
	}

	/* Class My Location Listener */
	public class MyLocationListener implements LocationListener {
		private double lat = 0, lng = 0;
		@Override
		public void onLocationChanged(Location loc)	{
			loc.getLatitude();
			loc.getLongitude();

			String Text = "My current location is: " +"Latitud = " + loc.getLatitude() +"Longitud = " + loc.getLongitude();
			Toast.makeText( mContext,Text,Toast.LENGTH_SHORT).show();
			this.lat = loc.getLatitude();
			Log.d("Aici", Double.toString(this.lat));
			this.lng = loc.getLongitude();
		}
		
		@Override
		public void onProviderDisabled(String provider){
			Toast.makeText(mContext,"Gps Disabled",Toast.LENGTH_SHORT ).show();
		}

		@Override
		public void onProviderEnabled(String provider){
			Toast.makeText( mContext," Gps Enabled" ,Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}

		public double getLat() {
			Log.d("in Clasa", Double.toString(this.lat));
			return this.lat;
		}

		public double getLng(){
			return this.lng;
		}
	}
}
