package com.example.clouds;



import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class MapMarkerActivity extends MapActivity {
	private MapView map=null;
	private MyLocationOverlay me=null;
	// initilizam locatia de agaudat in hunt cu o locatie oarecare, aceasta locatie schimbandu-se 
	// la miscarea pinului pe ecran
	GeoPoint pt =  getPoint(44.419398, 26.081992);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent in = getIntent();
		final String numefis = in.getStringExtra("Map");	
		
		setContentView(R.layout.activity_map_marker);
		// buton pt adaugare de text pentru locatia pt
		LinearLayout ll = (LinearLayout)findViewById(R.id.btn);
		Button b = new Button(this);
		b.setText("Select Location");
		ll.addView(b);

		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent myIntent = new Intent(MapMarkerActivity.this, NameMessage.class);
				
				myIntent.putExtra("Point",Double.toString(((double)pt.getLatitudeE6())/1000000) + "\n" + Double.toString(((double)pt.getLongitudeE6())/1000000));
				myIntent.putExtra("Map",numefis);
				startActivity(myIntent);
				finish();
			}

		});

		map=(MapView)findViewById(R.id.map);
		//centrare harta pe bucuresti
		map.getController().setCenter(getPoint(44.429122,
				26.098680));

		map.setBuiltInZoomControls(true);
		// un pin care se va misca pe harta setand locatia care va fi transmisa pt
		Drawable marker=getResources().getDrawable(R.drawable.pushpin);

		marker.setBounds(0, 0, marker.getIntrinsicWidth(),marker.getIntrinsicHeight());

		map.getOverlays().add(new SitesOverlay(marker));

		me=new MyLocationOverlay(this, map);
		map.getOverlays().add(me);

	}

	@Override
	public void onResume() {
		super.onResume();

		me.enableCompass();
	}  

	@Override
	public void onPause() {
		super.onPause();

		me.disableCompass();
	}  

	@Override
	protected boolean isRouteDisplayed() {
		return(false);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_S) {

			map.setSatellite(!map.isSatellite());
			return(true);
		}
		// single click afiseaza zoom in/out
		else if (keyCode == KeyEvent.KEYCODE_Z) {
			map.displayZoomControls(true);
			return(true);
		}

		return(super.onKeyDown(keyCode, event));
	}

	private GeoPoint getPoint(double lat, double lon) {
		return(new GeoPoint((int)(lat*1000000.0),
				(int)(lon*1000000.0)));
	}

	private class SitesOverlay extends ItemizedOverlay<OverlayItem> {
		private List<OverlayItem> items=new ArrayList<OverlayItem>();
		private Drawable marker=null;
		private OverlayItem inDrag=null;
		private ImageView dragImage=null;
		private int xDragImageOffset=0;
		private int yDragImageOffset=0;
		private int xDragTouchOffset=0;
		private int yDragTouchOffset=0;

		public SitesOverlay(Drawable marker) {
			super(marker);
			this.marker=marker;

			dragImage=(ImageView)findViewById(R.id.drag);
			xDragImageOffset=dragImage.getDrawable().getIntrinsicWidth()/2;
			yDragImageOffset=dragImage.getDrawable().getIntrinsicHeight();
			// pozitia initiala 
			items.add(new OverlayItem(getPoint(44.419398,
					26.081992),
					"Bucharest",
					"Crystal Palace Ballroms"));
			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return(items.get(i));
		}

		@Override
		public void draw(Canvas canvas, MapView mapView,
				boolean shadow) {
			super.draw(canvas, mapView, shadow);

			boundCenterBottom(marker);
		}

		@Override
		public int size() {
			return(items.size());
		}

		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			final int action=event.getAction();
			final int x=(int)event.getX();
			final int y=(int)event.getY();
			boolean result=false;

			if (action==MotionEvent.ACTION_DOWN) {
				for (OverlayItem item : items) {
					Point p=new Point(0,0);

					map.getProjection().toPixels(item.getPoint(), p);

					if (hitTest(item, marker, x-p.x, y-p.y)) {
						result=true;
						inDrag=item;
						items.remove(inDrag);
						populate();

						xDragTouchOffset=0;
						yDragTouchOffset=0;

						setDragImagePosition(p.x, p.y);
						dragImage.setVisibility(View.VISIBLE);

						xDragTouchOffset=x-p.x;
						yDragTouchOffset=y-p.y;

						break;
					}
				}
			}
			else if (action==MotionEvent.ACTION_MOVE && inDrag!=null) {
				setDragImagePosition(x, y);
				result=true;
			}
			else if (action==MotionEvent.ACTION_UP && inDrag!=null) {
				dragImage.setVisibility(View.GONE);

				pt=map.getProjection().fromPixels(x-xDragTouchOffset,
						y-yDragTouchOffset);
				OverlayItem toDrop=new OverlayItem(pt, inDrag.getTitle(),
						inDrag.getSnippet());
				Toast.makeText(MapMarkerActivity.this, pt.getLatitudeE6()+" "+pt.getLongitudeE6(), Toast.LENGTH_SHORT).show();
				items.add(toDrop);
				Log.d("In vector",pt.toString());
				Log.d("Size",Integer.toString(items.size()));
				populate();

				inDrag=null;
				result=true;
			}

			return(result || super.onTouchEvent(event, mapView));
		}

		private void setDragImagePosition(int x, int y) {
			RelativeLayout.LayoutParams lp=
					(RelativeLayout.LayoutParams)dragImage.getLayoutParams();

			lp.setMargins(x-xDragImageOffset-xDragTouchOffset,
					y-yDragImageOffset-yDragTouchOffset, 0, 0);
			dragImage.setLayoutParams(lp);
		}
	}
}  