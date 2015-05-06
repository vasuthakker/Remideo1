package com.remedeo.remedeoapp.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.remedeo.remedeoapp.R;
import com.remedeo.remedeoapp.entities.PatientEntity;

public class CameraActivity extends Activity {
	private static final int MEDIA_TYPE_VIDEO = 1;
	private static final int MEDIA_TYPE_IMAGE = 2;
	Button captureButton;
	private Button flashButton;
	private Button stopPreview;
	String name;
	private Camera camera;
	
	private FrameLayout previewLayout;
	
	private CameraPreview mPreview;
	
	private MediaRecorder mMediaRecorder;
	
	private boolean isRecording = false;
	
	private SeekBar zoomSeekBar;
	
	private int maxZoom = 0;
	private int currentZoomLevel = 0;
	
	private int seconds = 0;
	
	private Handler handler;
	private TextView recordingSecondsTextView;
	
	private PatientEntity patientObject;
	
	private static final String PATIENT_OBJECT = "PATIENT_OBJECT";
	private String folderName;
	
	private Drawable flashOn;
	private Drawable flashOff;
	private Drawable flashAuto;
	private Drawable recordOn;
	private Drawable recordOff;
	
	private static final String FOLDER_MAIN = "Remidio";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_camera);
		
		captureButton = (Button) findViewById(R.id.captureButton);
		flashButton = (Button) findViewById(R.id.startbutton);
		stopPreview = (Button) findViewById(R.id.stopbutton);
		previewLayout = (FrameLayout) findViewById(R.id.frameLayout);
		zoomSeekBar = (SeekBar) findViewById(R.id.zoom_seekbar);
		recordingSecondsTextView = (TextView) findViewById(R.id.camera_recording_minutes);
		
		folderName = getIntent().getStringExtra("directoryPath");
		
		handler = new Handler();
		
		flashOn = getResources().getDrawable(R.drawable.flash_on);
		flashOff = getResources().getDrawable(R.drawable.flash_off);
		flashAuto = getResources().getDrawable(R.drawable.flash_auto);
		recordOff = getResources().getDrawable(R.drawable.cam_video);
		recordOn = getResources().getDrawable(R.drawable.cam_video_on);
		
		patientObject = (PatientEntity) getIntent().getSerializableExtra(
				PATIENT_OBJECT);
		
		if (this.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			
			camera = Camera.open();
			
			camera.setDisplayOrientation(90);
			
			mPreview = new CameraPreview(this, camera);
			
			previewLayout.addView(mPreview);
			
			camera.startPreview();
			
			Parameters param = camera.getParameters();
			
			param.setFlashMode(Parameters.FLASH_MODE_AUTO);
			param.set("iso", String.valueOf(200));
			
			camera.setParameters(param);
			flashButton.setBackground(flashAuto);
			
			flashButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// camera.startPreview();
					Parameters param = camera.getParameters();
					if (param.getFlashMode().equalsIgnoreCase(
							Parameters.FLASH_MODE_OFF)) {
						param.setFlashMode(Parameters.FLASH_MODE_ON);
						flashButton.setBackground(flashOn);
					}
					else if (param.getFlashMode().equalsIgnoreCase(
							Parameters.FLASH_MODE_ON)) {
						param.setFlashMode(Parameters.FLASH_MODE_AUTO);
						flashButton.setBackground(flashAuto);
					}
					else {
						param.setFlashMode(Parameters.FLASH_MODE_OFF);
						flashButton.setBackground(flashOff);
					}
					camera.setParameters(param);
				}
			});
			
			stopPreview.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (zoomSeekBar.getVisibility() == View.VISIBLE) {
						zoomSeekBar.setVisibility(View.GONE);
					}
					else {
						zoomSeekBar.setVisibility(View.VISIBLE);
					}
				}
			});
			
			captureButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					camera.takePicture(null, null, mPicture);
					
				}
			});
			
			// For Video
			// captureButton.setOnLongClickListener(new OnLongClickListener() {
			//
			// @Override
			// public boolean onLongClick(View v) {
			// if (isRecording) {
			// // stop recording and release camera
			// mMediaRecorder.stop(); // stop the recording
			// releaseMediaRecorder(); // release the MediaRecorder
			// // object
			// camera.lock(); // take camera access back from
			// // MediaRecorder
			//
			// // inform the user that recording has stopped
			// captureButton.setBackground(recordOff);
			// isRecording = false;
			//
			// recordingSecondsTextView.setVisibility(View.GONE);
			// handler.removeCallbacks(timerRunnable);
			//
			// }
			// else {
			// // initialize video camera
			// if (prepareVideoRecorder()) {
			// seconds = 0;
			// // Camera is available and unlocked, MediaRecorder
			// // is prepared,
			// // now you can start recording
			// mMediaRecorder.start();
			//
			// // inform the user that recording has started
			// captureButton.setBackground(recordOn);
			// isRecording = true;
			// recordingSecondsTextView
			// .setVisibility(View.VISIBLE);
			// handler.postDelayed(timerRunnable, 1000);
			//
			// }
			// else {
			// // prepare didn't work, release the camera
			// releaseMediaRecorder();
			// // inform user
			// }
			// }
			//
			// return true;
			// }
			// });
			
			captureButton.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					
					return true;
				}
			});
			
		}
		
	}
	
	@Override
	public void onBackPressed() {
		if (camera != null) {
			camera.release();
			camera = null;
		}
		Intent in = new Intent(getApplicationContext(), GridViewActivity.class);
		in.putExtra(PATIENT_OBJECT, patientObject);
		in.putExtra("directoryPath", folderName);
		in.putExtra("showAll", false);
		startActivity(in);
		finish();
	}
	
	private Runnable timerRunnable = new Runnable() {
		
		@Override
		public void run() {
			seconds++;
			recordingSecondsTextView.setText(String.valueOf(seconds));
			if (isRecording) {
				handler.postDelayed(this, 1000);
			}
			else {
				handler.removeCallbacks(this);
			}
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		if (camera == null) {
			camera = Camera.open();
			camera.setDisplayOrientation(90);
			camera.startPreview();
		}
	}
	
	@Override
	protected void onPause() {
		if (camera != null) {
			camera.release();
			camera = null;
		}
		super.onPause();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 2) {
				Bundle bundle = data.getExtras();
				if (name == null) {
					name = bundle.getString("returnKey");
					
				}
				
				if (name != null && !name.isEmpty()) {
					captureButton.setText(bundle.getString("returnKey"));
				}
			}
		}
	}
	
	private PictureCallback mPicture = new PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			
			captureButton.setEnabled(false);
			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
			if (bmp != null) {
				bmp = addText(bmp);
				if (bmp != null) {
					try {
						FileOutputStream fos = new FileOutputStream(
								getOutputMediaFile(MEDIA_TYPE_IMAGE));
						bmp.compress(CompressFormat.JPEG, 100, fos);
						fos.write(data);
						fos.close();
						MediaPlayer m = new MediaPlayer();
						
						AssetFileDescriptor afd = CameraActivity.this
								.getAssets().openFd("sounds/capture.mp3");
						
						m.setDataSource(afd.getFileDescriptor(),
								afd.getStartOffset(), afd.getLength());
						
						m.prepare();
						
						m.setVolume(1f, 1f);
						
						m.start();
						
						captureButton.setEnabled(true);
					}
					catch (Exception error) {
						Log.e("eror", "exception", error);
					}
				}
			}
		}
		
	};
	
	private Bitmap addText(Bitmap bmp) {
		
		EditText tempEditText = new EditText(getApplicationContext());
		
		tempEditText.setText("Id: "
				+ patientObject.getId()
				+ " Name: "
				+ patientObject.getName()
				+ " DOB: "
				+ patientObject.getDOB()
				+ "\nDate:"
				+ new SimpleDateFormat("d/MM/yyyy H:m:s").format(System
						.currentTimeMillis()));
		
		tempEditText.setTextColor(Color.WHITE);
		tempEditText.setBackgroundColor(Color.BLACK);
		tempEditText.setDrawingCacheEnabled(true);
		tempEditText.buildDrawingCache();
		tempEditText.setTextSize(26f);
		tempEditText.setWidth(800);
		tempEditText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		tempEditText.layout(0, 0, 1600, 250);
		tempEditText.setPadding(30, 30, 0, 0);
		Bitmap bm1 = Bitmap.createBitmap(tempEditText.getDrawingCache());
		
		tempEditText.setDrawingCacheEnabled(false);
		
		return combineImages(bmp, bm1);
		
	}
	
	@SuppressLint({ "NewApi", "SdCardPath" })
	public Bitmap combineImages(Bitmap background, Bitmap foreground) {
		int width = 0, height = 0;
		Bitmap cs;
		
		final DisplayMetrics metrics = new DisplayMetrics();
		Display display = getWindowManager().getDefaultDisplay();
		display.getRealMetrics(metrics);
		width = metrics.widthPixels;
		height = metrics.heightPixels;
		
		boolean isSupportHD = false;
		try {
			cs = Bitmap.createBitmap(background.getWidth(),
					background.getHeight(), Bitmap.Config.ARGB_8888);
			
			isSupportHD = true;
			
		}
		catch (Throwable e) {
			cs = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888);
			
		}
		Canvas canvas = new Canvas(cs);
		if (isSupportHD) {
			background = Bitmap.createScaledBitmap(background,
					background.getWidth(), background.getHeight(), true);
		}
		else {
			background = Bitmap.createScaledBitmap(background, height, width,
					true);
		}
		canvas.drawBitmap(background, 0, 0, null);
		canvas.drawBitmap(foreground, 10, 0, null);
		
		return cs;
	}
	
	private class CameraPreview extends SurfaceView implements
			SurfaceHolder.Callback {
		private static final String TAG = "CameraPreview";
		private SurfaceHolder mHolder;
		private Camera mCamera;
		
		public CameraPreview(Context context, Camera camera) {
			super(context);
			mCamera = camera;
			
			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = getHolder();
			mHolder.addCallback(this);
			// deprecated setting, but required on Android versions prior to 3.0
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			
			setFocusable(true);
			setFocusableInTouchMode(true);
		}
		
		public void surfaceCreated(SurfaceHolder holder) {
			// The Surface has been created, now tell the camera where to draw
			// the preview.
			try {
				mCamera.setPreviewDisplay(holder);
				Camera.Parameters p = mCamera.getParameters();
				p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				
				mCamera.setParameters(p);
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
				mCamera.autoFocus(null);
				// mCamera.startPreview();
			}
			catch (IOException e) {
				Log.d(TAG, "Error setting camera preview: " + e.getMessage());
			}
		}
		
		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				Log.d("down", "focusing now");
				
				mCamera.autoFocus(null);
			}
			
			return true;
		}
		
		public void surfaceDestroyed(SurfaceHolder holder) {
			// empty. Take care of releasing the Camera preview in your
			// activity.
		}
		
		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			// If your preview can change or rotate, take care of those events
			// here.
			// Make sure to stop the preview before resizing or reformatting it.
			
			if (mHolder.getSurface() == null) {
				// preview surface does not exist
				return;
			}
			
			// stop preview before making changes
			try {
				
				mCamera.stopPreview();
			}
			catch (Exception e) {
				// ignore: tried to stop a non-existent preview
			}
			
			// set preview size and make any resize, rotate or
			// reformatting changes here
			
			// start preview with new settings
			try {
				final Camera.Parameters p = mCamera.getParameters();
				p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				
				if (p.isZoomSupported()) {
					// most phones
					maxZoom = p.getMaxZoom();
					
					zoomSeekBar.setMax(maxZoom);
					
					zoomSeekBar
							.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
								
								@Override
								public void onStopTrackingTouch(SeekBar seekBar) {
									
								}
								
								@Override
								public void onStartTrackingTouch(SeekBar seekBar) {
									
								}
								
								@Override
								public void onProgressChanged(SeekBar seekBar,
										int progress, boolean fromUser) {
									if (p.isSmoothZoomSupported()) {
										camera.startSmoothZoom(progress);
									}
									else if (!p.isSmoothZoomSupported()) {
										p.setZoom(progress);
										camera.setParameters(p);
									}
								}
							});
				}
				else {
					// no zoom on phone
					zoomSeekBar.setVisibility(View.GONE);
				}
				
				camera.setParameters(p);
				
				try {
					camera.setPreviewDisplay(holder);
				} // end try
				catch (IOException e) {
					Log.v(TAG, e.toString());
				}
				
				mCamera.setParameters(p);
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();
				mCamera.autoFocus(null);
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();
				
			}
			catch (Exception e) {
				Log.d(TAG, "Error starting camera preview: " + e.getMessage());
			}
		}
	}
	
	/** Create a File for saving an image or video */
	private File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		
		File mediaStorageDir = new File(Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + FOLDER_MAIN + File.separator + folderName);
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.
		
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}
		
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".JPEG");
		}
		else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		}
		else {
			return null;
		}
		
		return mediaFile;
	}
	
	private boolean prepareVideoRecorder() {
		
		if (camera == null) {
			camera = Camera.open();
			
		}
		camera.setDisplayOrientation(90);
		mMediaRecorder = new MediaRecorder();
		
		// Step 1: Unlock and set camera to MediaRecorder
		camera.unlock();
		mMediaRecorder.setCamera(camera);
		
		// Step 2: Set sources
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		
		// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
		mMediaRecorder.setProfile(CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH));
		
		mMediaRecorder.setOrientationHint(90);
		
		// Step 4: Set output file
		mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO)
				.toString());
		
		// Step 5: Set the preview output
		mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
		
		// Step 6: Prepare configured MediaRecorder
		try {
			mMediaRecorder.prepare();
		}
		catch (IllegalStateException e) {
			
			releaseMediaRecorder();
			return false;
		}
		catch (IOException e) {
			
			releaseMediaRecorder();
			return false;
		}
		return true;
	}
	
	private void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			mMediaRecorder.reset(); // clear recorder configuration
			mMediaRecorder.release(); // release the recorder object
			mMediaRecorder = null;
			camera.lock(); // lock camera for later use
		}
	}
	
	private void releaseCamera() {
		if (camera != null) {
			camera.release(); // release the camera for other applications
			camera = null;
		}
	}
	
}
