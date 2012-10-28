package com.example.clouds;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.


// ----------------------------------------------------------------------

public class CameraPreview extends Activity implements SensorEventListener {
	boolean nextok = false;
	private int index = 0;
    private Preview mPreview;
    Camera mCamera;
    int numberOfCameras;
    int cameraCurrentlyLocked;
    float mijloc;
 //
    private float[] valuesAccelerometer;
    private float[] valuesMagneticField;
     
    private float[] matrixR;
    private float[] matrixI;
    private float[] matrixValues; 
    String mesaj = "This is a mesagge";
    float  position;
	Marker marker ;
    MyLocation myLocation ;
    float mValues[];
	//
    SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;
	
	

	// The first rear facing camera
    int defaultCameraId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mValues = new float[3];
		myLocation = new MyLocation(this);
		

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        
        // Create a RelativeLayout container that will hold a SurfaceView,
        // and set it as the content of our activity.
        
        mPreview = new Preview(this);
        setContentView(mPreview);
        Intent intent = getIntent();
        //index = intent.getIntExtra("index",0);
        mesaj = intent.getStringExtra("mesaj");
        
       // Log.d("CameraP", Integer.toString(index));
        DrawOnTop mDraw = new DrawOnTop(this); 
        marker = new Marker(this,mesaj,0,0); 
        addContentView(mDraw, new LayoutParams (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 
        addContentView(marker, new LayoutParams (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 
        // Find the total number of cameras available
        
        numberOfCameras = Camera.getNumberOfCameras();
        valuesAccelerometer = new float[3];
        valuesMagneticField = new float[3];
        
        matrixR = new float[9];
        matrixI = new float[9];
        matrixValues = new float[3];
        // Find the ID of the default camera
        CameraInfo cameraInfo = new CameraInfo();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                    defaultCameraId = i;
                }
            }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Open the default i.e. the first rear facing camera.
        mCamera = Camera.open();
        cameraCurrentlyLocked = defaultCameraId;
        mPreview.setCamera(mCamera);
        sensorManager.registerListener(this,
        	       sensorAccelerometer,
        	       SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
        	       sensorMagneticField,
        	       SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
        sensorManager.unregisterListener(this,
        	    sensorAccelerometer);
        sensorManager.unregisterListener(this,
        	    sensorMagneticField);
    }
    
    @Override
    public void onSensorChanged(SensorEvent event) {

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
      
      //unghiul sub care se poate vedea textul ajutator pentru quest, atunci cand suntem la o distanta < 200m
      //de destinatie
      double roll = Math.toDegrees(matrixValues[2]);
      if((-130 >= roll && roll >=-180) ||( roll >= 130 && roll <= 180) ){
      	
    	   Button button = new Button(this);
	        button.setText("Next");
	        button.setLayoutParams(new LayoutParams(
	            ViewGroup.LayoutParams.WRAP_CONTENT,
	                ViewGroup.LayoutParams.WRAP_CONTENT));
	        button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//Intent i = new Intent(CameraPreview.this, Activity1.class);
					//i.putExtra("index", index+1);
					//startActivity(i);
					finish();
				}
			});
	        addContentView(button, new LayoutParams (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 
       
      }
		marker.updateMarker(roll);
		marker.invalidate();
     }
      
    }
    
    
    
    
// ----------------------------------------------------------------------

/**
 * A simple wrapper around a Camera and a SurfaceView that renders a centered preview of the Camera
 * to the surface. We need to center the SurfaceView because not all devices have cameras that
 * support preview sizes at the same aspect ratio as the device's display.
 */
    

class Preview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "Preview";

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;
    
    Preview(Context context) {
        super(context);

        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            requestLayout();
        }
    }

    public void switchCamera(Camera camera) {
       setCamera(camera);
       try {
           camera.setPreviewDisplay(mHolder);
       } catch (IOException exception) {
           Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
       }
       Camera.Parameters parameters = camera.getParameters();
       parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
       requestLayout();

       camera.setParameters(parameters);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }


    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }


    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        requestLayout();

        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

}




@Override
public void onAccuracyChanged(Sensor sensor, int accuracy) {
	
}








}