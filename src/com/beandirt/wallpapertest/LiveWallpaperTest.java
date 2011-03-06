package com.beandirt.wallpapertest;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public class LiveWallpaperTest extends WallpaperService {

	private int counter = 0;
	private String location = "sf";
	private int[] resource_ids = {R.drawable.sf_1,R.drawable.sf_2,R.drawable.sf_3,R.drawable.sf_4};
	private static String TAG = "LiveWallpaperApp";
	
	@Override 
	public void onCreate(){
		super.onCreate();
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
		
		TestEngine(){
			
		}
		
		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			setTouchEventsEnabled(true);
			Log.d(TAG,"On Create");
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset) {
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
					xPixelOffset, yPixelOffset);
			Log.d(TAG,"Offsets Changed");
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
		        drawBitmap();
		    }
		    return null;
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			if(visible) Log.d(TAG,"The wallpaper has been made visible");
			else drawBitmap();
		}
		
		private void drawBitmap(){
			if(counter == resource_ids.length - 1) counter = 0;
			else ++counter;
			final SurfaceHolder holder = getSurfaceHolder();
			Bitmap bMap = BitmapFactory.decodeResource(getResources(), resource_ids[counter]);
			Canvas c = new Canvas();
			c = holder.lockCanvas();
			c.save();
			c.drawBitmap(bMap,0,0,null);
			c.restore();
			holder.unlockCanvasAndPost(c);			
		}
	}
}
