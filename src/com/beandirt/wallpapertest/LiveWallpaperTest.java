package com.beandirt.wallpapertest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class LiveWallpaperTest extends WallpaperService {

	private int counter = 0;
	private String location = "sf";
	private int[] resource_ids = {R.drawable.sf_2,R.drawable.sf_3,R.drawable.sf_4};
	private List<Bitmap> bitmaps;
	private static String TAG = "LiveWallpaperApp";
	private ExecutorService executor;
	
	private static int SCREEN_WIDTH;
	private static int SCREEN_HEIGHT;
	
	@Override 
	public void onCreate(){
		super.onCreate();
		bitmaps = new ArrayList<Bitmap>();
		for (int i = 0; i < resource_ids.length; ++i){
			bitmaps.add(BitmapFactory.decodeResource(getResources(), resource_ids[i]));
		}
		executor = Executors.newSingleThreadExecutor();
		
		Log.d(TAG, "width: "+String.valueOf(getWallpaperDesiredMinimumWidth()));
		Log.d(TAG, "height: "+String.valueOf(getWallpaperDesiredMinimumHeight()));
		
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		SCREEN_WIDTH = display.getWidth();
		SCREEN_HEIGHT = display.getHeight();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	@Override
	public Engine onCreateEngine() {
		return new TestEngine();
	}
	
	class TestEngine extends Engine{
		
		private float xOffset;
		
		TestEngine(){
			
		}
		
		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			setTouchEventsEnabled(true);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
		}

		@Override
		public void onOffsetsChanged(final float xOffset, final float yOffset,
				final float xOffsetStep, final float yOffsetStep, final int xPixelOffset,
				final int yPixelOffset) {
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
					xPixelOffset, yPixelOffset);
			Runnable offsetsChangedCommand = new Runnable() {
				public void run() {
					if (xOffsetStep != 0f) {
						setParallax(xOffset);
					}
				};
			};
			executor.execute(offsetsChangedCommand);
			drawBitmap();
		}

		private void setParallax(float xOffset) {
			this.xOffset = -(xOffset * (bitmaps.get(counter).getWidth() - SCREEN_WIDTH));
		}
		
		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
			Log.d(TAG,"Surface Created");
			drawBitmap();
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder){
			super.onSurfaceDestroyed(holder);
			Log.d(TAG,"Surface Destroyed");
		}
		
		
		@Override
		public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
		    if (action.equals(WallpaperManager.COMMAND_TAP)) {
		    	if(counter == bitmaps.size() - 1) counter = 0;
				else ++counter;
		    	drawBitmap();
		    }
		    return null;
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			if(visible) Log.d(TAG,"The wallpaper has been made visible");
			else {
				if(counter == bitmaps.size() - 1) counter = 0;
				else ++counter;
				drawBitmap();
			}
		}
		
		private void drawBitmap(){
			final SurfaceHolder holder = getSurfaceHolder();
			Canvas c = new Canvas();
			c = holder.lockCanvas();
			c.save();
			Log.d(TAG, String.valueOf(this.xOffset));
			c.drawBitmap(bitmaps.get(counter),this.xOffset,0,null);
			c.restore();
			holder.unlockCanvasAndPost(c);
		}
	}
}
