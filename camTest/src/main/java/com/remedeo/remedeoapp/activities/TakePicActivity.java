package com.remedeo.remedeoapp.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.remedeo.remedeoapp.R;
import com.remedeo.remedeoapp.adpater.ListAdapter;
import com.remedeo.remedeoapp.entities.PatientEntity;
import com.remedeo.remedeoapp.helper.PatientHelper;
import com.remedeo.remedeoapp.utils.Utils;

public class TakePicActivity extends FragmentActivity {
	
	private List<Camera.Size> supportedPreviewSizes;
	private Camera.Size mPreviewSize;
	
	private static final int MEDIA_TYPE_VIDEO = 1;
	private static final int MEDIA_TYPE_IMAGE = 2;
	Button captureButton;
	private Button btnEffect;
	private Button btnExposure;
	String name;
	private Camera camera;
	
	private FrameLayout previewLayout;
	
	private CameraPreview mPreview;
	
	private MediaRecorder mMediaRecorder;
	
	private boolean isRecording = false;
	
	private SeekBar zoomSeekBar;
	private SeekBar exposureSeekBar;
	
	private int maxZoom = 0;
	private int currentZoomLevel = 0;
	
	private int seconds = 0;
	
	private Handler handler;
	private TextView recordingSecondsTextView;
	
	// TONE
	private boolean isFlashOn = false;
	private MediaPlayer mediaPlayer;
	
	private final float duration = 2f; // seconds
	private final int sampleRate = 8000;
	private final int numSamples = (int) (duration * sampleRate);
	private final double sample[] = new double[numSamples];
	private byte generatedSnd[] = new byte[2 * numSamples];
	
	private byte[] cameraFrame;
	private byte[] buffer;
	
	private ListView effectListView;
	
	private List<Camera.Size> sizes;
	
	private List<String> colorEffects;
	
	private Button btnSwitchCamera;
	private Button btnFlash;
	private Button btnIso;
	private ImageView focusImageView;
	private ImageView changeFocusImageView;
	// private RelativeLayout pictureTakenWhiteLayout;
	private LinearLayout isoRadioLayout;
	private RadioGroup isoRadioGroup;
	
	private boolean isFocusAllowed = true;
	
	private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
	
	private MediaPlayer captureSound = null;
	
	private File imageFile;
	
	private static final String PATIENT_OBJECT = "PATIENT_OBJECT";
	private static String direcotryPath;
	private PatientEntity patientObject;
	private static final String FOLDER_MAIN = "Remidio";
	
	private File patientDirectory;
	
	private String isoLevel = "auto";
	
	private boolean isBurstMode = false;
	private int burstCount = 0;
	private static final int BURST_LIMIT = 4;
	
	private String flashMode;
	
	private ListView isoListView;
	
	List<String> supportedIsoValues = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.camera_layout);
		
		captureButton = (Button) findViewById(R.id.captureButton);
		btnEffect = (Button) findViewById(R.id.startbutton);
		btnExposure = (Button) findViewById(R.id.stopbutton);
		previewLayout = (FrameLayout) findViewById(R.id.frameLayout);
		zoomSeekBar = (SeekBar) findViewById(R.id.zoom_seekbar);
		recordingSecondsTextView = (TextView) findViewById(R.id.camera_recording_minutes);
		effectListView = (ListView) findViewById(R.id.effectListView);
		btnSwitchCamera = (Button) findViewById(R.id.switchCamera);
		btnFlash = (Button) findViewById(R.id.flashbutton);
		btnIso = (Button) findViewById(R.id.isoButton);
		exposureSeekBar = (SeekBar) findViewById(R.id.exposureSeekbar);
		focusImageView = (ImageView) findViewById(R.id.focusImageView);
		isoRadioLayout = (LinearLayout) findViewById(R.id.isoLayout);
		isoRadioGroup = (RadioGroup) findViewById(R.id.isoRadioGroup);
		
		// iso1600= (RadioButton) findViewById(R.id.iso1600);
		
		isoListView = (ListView) findViewById(R.id.isoListView);
		
		// pictureTakenWhiteLayout = (RelativeLayout)
		// findViewById(R.id.takepictureeffect);
		changeFocusImageView = (ImageView) findViewById(R.id.changeFocusImageView);
		
		btnFlash.setVisibility(View.INVISIBLE);
		
		direcotryPath = getIntent().getStringExtra("directoryPath");
		
		patientDirectory = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator
				+ FOLDER_MAIN
				+ File.separator + direcotryPath);
		
		patientObject = (PatientEntity) getIntent().getSerializableExtra(
				PATIENT_OBJECT);
		
		handler = new Handler();
		
		captureSound = MediaPlayer.create(TakePicActivity.this, R.raw.capture);
		
		if (this.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			
			// btnZoom.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// if (zoomSeekBar.getVisibility() == View.VISIBLE) {
			// zoomSeekBar.setVisibility(View.GONE);
			// } else {
			// zoomSeekBar.setVisibility(View.VISIBLE);
			// }
			// }
			// });
			
			captureButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// playTone(900);
					if (camera != null) {
						
						/*
						 * captureSound =
						 * MediaPlayer.create(TakePicActivity.this,
						 * R.raw.capture);
						 * 
						 * captureSound.start();
						 */
						// pictureTakenWhiteLayout.setVisibility(View.VISIBLE);
						/*
						 * if (isFocusAllowed) { camera.autoFocus(new
						 * AutoFocusCallback() {
						 * 
						 * @Override public void onAutoFocus(boolean success,
						 * Camera camera) {
						 * captureButton.setVisibility(View.GONE);
						 * camera.takePicture(null, null, mPicture);
						 * 
						 * } }); } else {
						 */
						captureButton.setVisibility(View.GONE);
						camera.takePicture(null, null, mPicture);
						// }
					}
				}
			});
			
			changeFocusImageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (isFocusAllowed) {
						isFocusAllowed = false;
						changeFocusImageView
								.setImageResource(R.drawable.focus_off);
					}
					else {
						isFocusAllowed = true;
						changeFocusImageView
								.setImageResource(R.drawable.focus_on);
					}
					
				}
			});
			
			// Multiple photos
			/*
			 * captureButton.setOnLongClickListener(new OnLongClickListener() {
			 * 
			 * @Override public boolean onLongClick(View v) { isBurstMode =
			 * true; captureSound.start(); camera.takePicture(null, null,
			 * mPicture); return true; } });
			 */
			
			//
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
			// captureButton.setText("capture");
			// isRecording = false;
			//
			// recordingSecondsTextView.setVisibility(View.GONE);
			// handler.removeCallbacks(timerRunnable);
			//
			// } else {
			// // initialize video camera
			// if (prepareVideoRecorder()) {
			// seconds = 0;
			// // Camera is available and unlocked, MediaRecorder
			// // is prepared,
			// // now you can start recording
			// mMediaRecorder.start();
			//
			// // inform the user that recording has started
			// captureButton.setText("stop");
			// isRecording = true;
			// recordingSecondsTextView
			// .setVisibility(View.VISIBLE);
			// handler.postDelayed(timerRunnable, 1000);
			//
			// } else {
			// // prepare didn't work, release the camera
			// releaseMediaRecorder();
			// // inform user
			// }
			// }`
			//
			// return true;
			// }
			// });
			
			effectListView.setOnItemClickListener(new OnItemClickListener() {
				
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if (camera != null && colorEffects != null) {
						Parameters params = camera.getParameters();
						params.setColorEffect(colorEffects.get(position));
						camera.setParameters(params);
						camera.startPreview();
						effectListView.setVisibility(View.GONE);
					}
				}
			});
			
			btnEffect.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (effectListView.getVisibility() == View.GONE) {
						effectListView.setVisibility(View.VISIBLE);
					}
					else {
						effectListView.setVisibility(View.GONE);
					}
				}
			});
			
			// Switching camera
			btnSwitchCamera.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					stopCamera();
					zoomSeekBar.setVisibility(View.VISIBLE);
					isoRadioLayout.setVisibility(View.GONE);
					if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
						cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
						btnIso.setVisibility(View.INVISIBLE);
						exposureSeekBar.setVisibility(View.GONE);
						btnEffect.setVisibility(View.VISIBLE);
						captureButton.setVisibility(View.VISIBLE);
						btnFlash.setVisibility(View.INVISIBLE);
						btnIso.setVisibility(View.INVISIBLE);
						
					}
					else {
						cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
						btnIso.setVisibility(View.VISIBLE);
						btnEffect.setVisibility(View.VISIBLE);
						captureButton.setVisibility(View.VISIBLE);
						btnFlash.setVisibility(View.VISIBLE);
						btnExposure.setVisibility(View.VISIBLE);
					}
					camera = Camera.open(cameraId);
					camera.startPreview();
					previewLayout.removeView(mPreview);
					mPreview = new CameraPreview(TakePicActivity.this, camera);
					previewLayout.addView(mPreview);
					
				}
			});
			
			// Flash settings
			btnFlash.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (camera != null) {
						if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
							// camera.stopPreview();
							Parameters param = camera.getParameters();
							flashMode = param.getFlashMode();
							if (flashMode.equals(Parameters.FLASH_MODE_OFF)) {
								param.setFlashMode(Parameters.FLASH_MODE_ON);
								btnFlash.setBackgroundResource(R.drawable.flash_on);
								flashMode = Parameters.FLASH_MODE_ON;
							}
							else if (flashMode.equals(Parameters.FLASH_MODE_ON)) {
								param.setFlashMode(Parameters.FLASH_MODE_AUTO);
								btnFlash.setBackgroundResource(R.drawable.flash_auto);
								flashMode = Parameters.FLASH_MODE_AUTO;
							}
							else {
								param.setFlashMode(Parameters.FLASH_MODE_OFF);
								btnFlash.setBackgroundResource(R.drawable.flash_off);
								flashMode = Parameters.FLASH_MODE_OFF;
							}
							camera.setParameters(param);
							// camera.startPreview();
						}
					}
					
				}
			});
		}
		if (!this.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA_FLASH)) {
			btnFlash.setVisibility(View.GONE);
		}
		
		// ISO settings
		btnIso.setOnClickListener(new OnClickListener() {
			String isoKeyName = null;;
			
			@Override
			public void onClick(View v) {
				final Camera.Parameters camParams = camera.getParameters();
				String flat = camParams.flatten();
				
				// Most used
				if (flat.contains("iso-values")) {
					isoKeyName = "iso";
					// Galaxy nexus
				}
				else if (flat.contains("iso-mode-values")) {
					isoKeyName = "iso";
					// Micromax
				}
				else if (flat.contains("iso-speed-values")) {
					isoKeyName = "iso-speed";
					// LG
				}
				else if (flat.contains("nv-picture-iso-values")) {
					isoKeyName = "nv-picture-iso";
				}
				else if (flat.contains("iso")) {
					isoKeyName = "iso";
				}
				if (isoKeyName != null) {
					if (isoListView.getVisibility() == View.GONE) {
						isoListView.setVisibility(View.VISIBLE);
						
						isoListView
								.setOnItemClickListener(new OnItemClickListener() {
									
									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {
										
										camParams.set(isoKeyName,
												supportedIsoValues
														.get(position));
										camera.setParameters(camParams);
										camera.startPreview();
										Utils.setPreference(
												getApplicationContext(),
												"isoLevel", supportedIsoValues
														.get(position));
										
										isoRadioLayout
												.setVisibility(View.VISIBLE);
										captureButton
												.setVisibility(View.VISIBLE);
										// btnFlash.setVisibility(View.VISIBLE);
										btnEffect.setVisibility(View.VISIBLE);
										btnExposure.setVisibility(View.VISIBLE);
										isoRadioLayout.setVisibility(View.GONE);
										isoListView.setVisibility(View.GONE);
										
										isoListView.setAdapter(new ListAdapter(
												TakePicActivity.this,
												supportedIsoValues, position));
									}
								});
					}
					else {
						isoListView.setVisibility(View.GONE);
					}
					
				}
			}
		});
		
		exposureSeekBar.setVisibility(View.GONE);
		
		// Exposure settings
		btnExposure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (camera != null) {
					
					if (exposureSeekBar.getVisibility() == View.GONE) {
						exposureSeekBar.setVisibility(View.VISIBLE);
						btnEffect.setVisibility(View.GONE);
						captureButton.setVisibility(View.GONE);
						// btnFlash.setVisibility(View.GONE);
						btnIso.setVisibility(View.GONE);
						final Parameters param = camera.getParameters();
						
						exposureSeekBar
								.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
									@Override
									public void onStopTrackingTouch(
											SeekBar seekBar) {
									}
									
									@Override
									public void onStartTrackingTouch(
											SeekBar seekBar) {
									}
									
									@Override
									public void onProgressChanged(
											SeekBar seekBar, int progress,
											boolean fromUser) {
										param.setExposureCompensation(param
												.getMinExposureCompensation()
												+ progress);
										camera.setParameters(param);
										camera.startPreview();
										Utils.setPreference(
												getApplicationContext(),
												"exposure",
												String.valueOf(param
														.getMinExposureCompensation()
														+ progress));
									}
								});
					}
					else {
						btnIso.setVisibility(View.VISIBLE);
						exposureSeekBar.setVisibility(View.GONE);
						btnEffect.setVisibility(View.VISIBLE);
						captureButton.setVisibility(View.VISIBLE);
						// btnFlash.setVisibility(View.VISIBLE);
					}
				}
			}
		});
		
	}
	
	private Bitmap addText(Bitmap bmp) {
		
		EditText tempEditText = new EditText(TakePicActivity.this);
		
		tempEditText.setText("Id: "
				+ patientObject.getPatientId()
				+ " Name: "
				+ patientObject.getName()
				+ " DOB: "
				+ patientObject.getDOB()
				+ "\nDate:"
				+ new SimpleDateFormat("d/MM/yyyy H:m:s").format(System
						.currentTimeMillis()) + "\nHospital Name: "
				+ patientObject.getHospitalName());
		
		tempEditText.setTextColor(Color.WHITE);
		tempEditText.setBackgroundColor(Color.BLACK);
		tempEditText.setDrawingCacheEnabled(true);
		tempEditText.buildDrawingCache();
		tempEditText.setTextSize(26f);
		tempEditText.setWidth(800);
		tempEditText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		tempEditText.layout(0, 0, 1600, 320);
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
		//
		// Matrix mat = new Matrix();
		// mat.postRotate(-90);
		//
		// Bitmap bmpForeground=Bitmap.createBitmap(
		// foreground, 0, 0, foreground.getWidth(),
		// foreground.getHeight(), mat, true);
		// canvas.drawBitmap(bmpForeground, 10,
		// background.getHeight()-bmpForeground.getHeight()-10, null);
		canvas.drawBitmap(foreground, 0, 0, null);
		
		return cs;
	}
	
	// Method to play tone of given frequnecy hertz
	private void playTone(final int hertz) {
		final Thread thread = new Thread(new Runnable() {
			public void run() {
				genTone(hertz);
				handler.post(new Runnable() {
					public void run() {
						playSound();
					}
				});
			}
		});
		thread.start();
	}
	
	// private Runnable timerRunnable = new Runnable() {
	//
	// @Override
	// public void run() {
	// seconds++;
	// recordingSecondsTextView.setText(String.valueOf(seconds));
	// if (isRecording) {
	// handler.postDelayed(this, 1000);
	//
	// } else {
	// handler.removeCallbacks(this);
	// }
	// }
	// };
	
	// Open camera start previewint it and add to the layout
	@Override
	protected void onResume() {
		super.onResume();
		if (this.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			
			isoLevel = Utils.getPreference(getApplicationContext(), "isoLevel");
			if (isoLevel == null || isoLevel.isEmpty()) {
				Utils.setPreference(getApplicationContext(), "isoLevel", "auto");
				isoLevel = "auto";
			}
			
			camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
			
			Camera.Parameters camParams = camera.getParameters();
			String flat = camParams.flatten();
			
			// Supported preivew sizes
			// Supported preview sizes
			supportedPreviewSizes = camParams.getSupportedPreviewSizes();
			int h, w;
			Display display = getWindowManager().getDefaultDisplay();
			if (display != null) {
				Point point = new Point();
				display.getSize(point);
				h = point.y;
				w = point.x;
				if (mPreviewSize == null)
					mPreviewSize = getOptimalPreviewCamera(
							supportedPreviewSizes, w, h);
				camParams.setPreviewSize(mPreviewSize.width,
						mPreviewSize.height);
			}
			
			Log.d("camparam", flat);
			
			String isoKeyName = null;
			
			// Most used
			if (flat.contains("iso-values")) {
				isoKeyName = "iso";
				// Galaxy nexus
			}
			else if (flat.contains("iso-mode-values")) {
				isoKeyName = "iso";
				// Micromax
			}
			else if (flat.contains("iso-speed-values")) {
				isoKeyName = "iso-speed";
				// LG
			}
			else if (flat.contains("nv-picture-iso-values")) {
				isoKeyName = "nv-picture-iso";
			}
			else if (flat.contains("iso")) {
				isoKeyName = "iso";
			}
			if (isoKeyName != null) {
				camParams.set(isoKeyName, isoLevel);
				camera.setParameters(camParams);
				
			}
			String exposure = Utils.getPreference(getApplicationContext(),
					"exposure");
			int diff = Math.abs(camParams.getMaxExposureCompensation()
					- camParams.getMinExposureCompensation());
			exposureSeekBar.setMax(diff);
			if (exposure != null) {
				int exp = Integer.parseInt(exposure);
				exposureSeekBar.setProgress(exp
						- camParams.getMinExposureCompensation());
				camParams.setExposureCompensation(Integer.parseInt(exposure));
			}
			else {
				exposureSeekBar.setProgress(Math.abs(camParams
						.getExposureCompensation()
						- camParams.getMinExposureCompensation()));
				
			}
			
			int rotation = setRotation();
			camParams.setRotation(rotation);
			
			camera.setParameters(camParams);
			
			camera.startPreview();
			previewLayout.removeView(mPreview);
			mPreview = new CameraPreview(TakePicActivity.this, camera);
			previewLayout.addView(mPreview);
			
			colorEffects = camera.getParameters().getSupportedColorEffects();
			effectListView.setAdapter(new ArrayAdapter<String>(
					TakePicActivity.this, R.layout.effect_item, colorEffects));
			
			if (supportedIsoValues.size() <= 0) {
				supportedIsoValues.add("auto");
				supportedIsoValues.add("100");
				supportedIsoValues.add("200");
				supportedIsoValues.add("400");
				supportedIsoValues.add("800");
				supportedIsoValues.add("1600");
			}
			
			int position = 0;
			for (int i = 0; i < supportedIsoValues.size(); i++) {
				String value = supportedIsoValues.get(i);
				if (value.equals(isoLevel)) {
					position = i;
					break;
				}
			}
			
			isoListView.setSelection(position);
			
			isoListView.setAdapter(new ListAdapter(TakePicActivity.this,
					supportedIsoValues, position));
			
		}
	}
	
	private int setRotation() {
		int degrees = 0;
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
		int rotation = TakePicActivity.this.getWindowManager()
				.getDefaultDisplay().getRotation();
		switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break; // Natural orientation
			case Surface.ROTATION_90:
				degrees = 90;
				break; // Landscape left
			case Surface.ROTATION_180:
				degrees = 180;
				break;// Upside down
			case Surface.ROTATION_270:
				degrees = 270;
				break;// Landscape right
		}
		
		return (info.orientation - degrees + 360) % 360;
		
	}
	
	// Release and stop the camera
	private void stopCamera() {
		System.out.println("stopCamera method");
		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}
	}
	
	@Override
	public void onPause() {
		if (camera != null) {
			stopCamera();
		}
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = new MediaPlayer();
		}
		super.onPause();
	}
	
	// Release camera and media player on pause
	@Override
	public void onBackPressed() {
		if (camera != null) {
			stopCamera();
		}
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = new MediaPlayer();
		}
		
		boolean isDeleteRequired = true;
		if (patientDirectory != null && patientDirectory.exists()) {
			File[] files = patientDirectory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.getName().endsWith(".JPEG")
							|| file.getName().endsWith(".jpeg")) {
						isDeleteRequired = false;
						break;
					}
				}
			}
		}
		
		if (isDeleteRequired) {
			if (patientDirectory != null && patientDirectory.exists()) {
				File[] files = patientDirectory.listFiles();
				if (files != null) {
					for (File file : files) {
						file.delete();
					}
				}
				patientDirectory.delete();
			}
			
			PatientHelper.deletePatient(getApplicationContext(),
					String.valueOf(patientObject.getPatientId()));
			
			showWarningDialog();
		}
		else {
			openDirectoryActivity();
		}
		
		// super.onBackPressed();
	}
	
	private void openDirectoryActivity() {
		Intent in = new Intent(getApplicationContext(), GridViewActivity.class);
		in.putExtra(PATIENT_OBJECT, patientObject);
		in.putExtra("directoryPath", direcotryPath);
		in.putExtra("showAll", false);
		startActivity(in);
		finish();
	}
	
	private void showWarningDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage(getString(R.string.error_not_taken_any_pic));
		
		alertDialogBuilder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						openDirectoryActivity();
					}
					
				});
		alertDialogBuilder.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						/*
						 * camera = Camera
						 * .open(Camera.CameraInfo.CAMERA_FACING_BACK);
						 * camera.startPreview();
						 * previewLayout.removeView(mPreview); mPreview = new
						 * CameraPreview(TakePicActivity.this, camera);
						 * previewLayout.addView(mPreview);
						 */
						if (camera != null)
							stopCamera();
						
						onResume();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.setCancelable(false);
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();
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
	
	// Called when picture is taken
	private PictureCallback mPicture = new PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			
			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
			if (bmp != null) {
				
				// if (bmp.getWidth() > bmp.getHeight()) {
				// Matrix mat = new Matrix();
				// mat.postRotate(90);
				//
				// bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
				// bmp.getHeight(), mat, true);
				// }
				//
				// bmp = addText(bmp);
				
				new SavePic().execute(bmp);
				
				// try {
				//
				// FileOutputStream fos = new FileOutputStream(
				// getOutputMediaFile(MEDIA_TYPE_IMAGE));
				// bmp.compress(CompressFormat.JPEG, 100, fos);
				// fos.flush();
				// fos.close();
				if (!isBurstMode || burstCount == BURST_LIMIT) {
					captureButton.setVisibility(View.VISIBLE);
					
					// pictureTakenWhiteLayout.setVisibility(View.GONE);
					// captureSound.release();
					
					// DialogFragment imageDialog = new
					// ViewImageDialog(imageFile);
					// imageDialog.show(
					// TakePicActivity.this.getSupportFragmentManager(),
					// "imageDialog");
					stopCamera();
					
					// camera.startPreview();
					
					onResume();
					
					// captureSound.release();
					isBurstMode = false;
					burstCount = 0;
				}
				else {
					// captureSound.start();
					camera.takePicture(null, null, mPicture);
					burstCount++;
				}
				// }
				// catch (Exception error) {
				// Log.e("eror", "exception", error);
				// }
			}
		}
		
	};
	
	private class SavePic extends AsyncTask<Bitmap, Void, Void> {
		
		@Override
		protected Void doInBackground(Bitmap... params) {
			try {
				Looper.prepare();
				
			}
			catch (Throwable e) {
				
			}
			Bitmap bmp = params[0];
			if (bmp != null) {
				
				if (bmp.getWidth() > bmp.getHeight()) {
					Matrix mat = new Matrix();
					mat.postRotate(90);
					
					bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
							bmp.getHeight(), mat, true);
				}
				if (!isBurstMode)
					bmp = addText(bmp);
				
				try {
					
					FileOutputStream fos = new FileOutputStream(
							getOutputMediaFile(MEDIA_TYPE_IMAGE));
					bmp.compress(CompressFormat.JPEG, 100, fos);
					fos.flush();
					fos.close();
					
				}
				catch (IOException e) {
					Log.e("eror", "exception", e);
				}
			}
			return null;
		}
		
	}
	
	void genTone(int freqHz) {
		// fill out the array
		generatedSnd = new byte[2 * numSamples];
		
		for (int i = 0; i < numSamples; ++i) {
			sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqHz));
		}
		// convert to 16 bit pcm sound array
		// assumes the sample buffer is normalised.
		int idx = 0;
		for (final double dVal : sample) {
			// scale to maximum amplitude
			final short val = (short) ((dVal * 32767));
			// in 16 bit wav PCM, first byte is the low order byte
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
			
		}
	}
	
	void playSound() {
		final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				sampleRate, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
				AudioTrack.MODE_STATIC);
		audioTrack.write(generatedSnd, 0, generatedSnd.length);
		audioTrack.play();
	}
	
	private File getDir() {
		File sdDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		return new File(sdDir, "CameraAPIDemo");
	}
	
	private class CameraPreview extends SurfaceView implements
			SurfaceHolder.Callback {
		
		private static final String TAG = "CameraPreview";
		private SurfaceHolder mHolder;
		private Camera mCamera;
		private byte[] rgbbuffer = new byte[256 * 256];
		private int[] rgbints = new int[256 * 256];
		private boolean meteringAreaSupported = false;
		
		@SuppressWarnings("deprecation")
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
		
		@Override
		protected void onDraw(Canvas canvas) {
			
		}
		
		public void surfaceCreated(SurfaceHolder holder) {
			// The Surface has been created, now tell the camera where to draw
			// the preview.
			synchronized (this) {
				this.setWillNotDraw(false);
				try {
					mCamera.setPreviewDisplay(holder);
					
					if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
						Camera.Parameters p = mCamera.getParameters();
						p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
						// mCamera.setParameters(p);
					}
					mCamera.setDisplayOrientation(90);
					mCamera.setPreviewDisplay(holder);
					mCamera.startPreview();
					mCamera.autoFocus(null);
					// mCamera.startPreview();
					
				}
				catch (Throwable e) {
					Log.e(TAG, "IOException", e);
				}
			}
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			// We purposely disregard child measurements because act as a
			// wrapper to a SurfaceView that centers the camera preview instead
			// of stretching it.
			final int width = resolveSize(getSuggestedMinimumWidth(),
					widthMeasureSpec);
			final int height = resolveSize(getSuggestedMinimumHeight(),
					heightMeasureSpec);
			setMeasuredDimension(width, height);
			
			if (supportedPreviewSizes != null) {
				mPreviewSize = getOptimalPreviewCamera(supportedPreviewSizes,
						width, height);
			}
		}
		
		// Focusing on touch
		
		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			try {
				if (isFocusAllowed) {
					if (event.getAction() == MotionEvent.ACTION_DOWN
							&& cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
						
						final MediaPlayer focusSound = MediaPlayer.create(
								TakePicActivity.this, R.raw.focus_sound);
						focusSound.start();
						focusImageView.setVisibility(View.VISIBLE);
						focusImageView.setImageResource(R.drawable.focused);
						
						focusImageView.setX(event.getX()
								- focusImageView.getWidth() / 2);
						focusImageView.setY(event.getY()
								- focusImageView.getHeight() / 2);
						
						mCamera.autoFocus(new AutoFocusCallback() {
							
							@Override
							public void onAutoFocus(boolean success,
									Camera camera) {
								if (!success) {
									focusImageView
											.setImageResource(R.drawable.unfocused);
								}
								else {
									focusImageView
											.setImageResource(R.drawable.focused);
								}
								Handler handler = new Handler();
								handler.postDelayed(new Runnable() {
									
									@Override
									public void run() {
										focusImageView.setVisibility(View.GONE);
										focusSound.release();
									}
								}, 800);
							}
						});
					}
				}
			}
			catch (Throwable e) {
			}
			return true;
		}
		
		public void surfaceDestroyed(SurfaceHolder holder) {
			synchronized (this) {
				try {
					if (mCamera != null) {
						mCamera.stopPreview();
						mCamera.release();
					}
				}
				catch (Exception e) {
					Log.e("Camera", e.getMessage());
				}
			}
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
				
				if (p.getMaxNumMeteringAreas() > 0) {
					this.meteringAreaSupported = true;
				}
				
				if (p.isZoomSupported()
						&& cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
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
				
				try {
					camera.setPreviewDisplay(holder);
				} // end try
				catch (IOException e) {
					Log.e(TAG, "IOException", e);
				}
				
				// mCamera.setParameters(p);
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();
				// mCamera.autoFocus(null);
				// mCamera.setPreviewDisplay(mHolder);
				// mCamera.startPreview();
				
			}
			catch (Throwable e) {
				Log.e(TAG, "Error starting camera preview:", e);
			}
		}
		
	}
	
	private Camera.Size getOptimalPreviewCamera(List<Camera.Size> sizes, int w,
			int h) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;
		
		Camera.Size optimalCameraSize = null;
		double minDiff = Double.MAX_VALUE;
		
		int targetHeight = h;
		
		// Try to find an Camera.Size match aspect ratio and Camera.Size
		for (Camera.Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalCameraSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}
		
		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalCameraSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Camera.Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalCameraSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalCameraSize;
	}
	
	public void decodeYUV(int[] rgbbuffer, byte[] fg, int width, int height)
			throws NullPointerException, IllegalArgumentException {
		int sz = width * height;
		if (rgbbuffer == null)
			throw new NullPointerException("buffer out is null");
		if (rgbbuffer.length < sz)
			throw new IllegalArgumentException("buffer out size "
					+ rgbbuffer.length + " < minimum " + sz);
		if (fg == null)
			throw new NullPointerException("buffer 'fg' is null");
		if (fg.length < sz)
			throw new IllegalArgumentException("buffer fg size " + fg.length
					+ " < minimum " + sz * 3 / 2);
		int i, j;
		int Y, Cr = 0, Cb = 0;
		for (j = 0; j < height; j++) {
			int pixPtr = j * width;
			final int jDiv2 = j >> 1;
			for (i = 0; i < width; i++) {
				Y = fg[pixPtr];
				if (Y < 0)
					Y += 255;
				if ((i & 0x1) != 1) {
					final int cOff = sz + jDiv2 * width + (i >> 1) * 2;
					Cb = fg[cOff];
					if (Cb < 0)
						Cb += 127;
					else
						Cb -= 128;
					Cr = fg[cOff + 1];
					if (Cr < 0)
						Cr += 127;
					else
						Cr -= 128;
				}
				int R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
				if (R < 0)
					R = 0;
				else if (R > 255)
					R = 255;
				int G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1)
						+ (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
				if (G < 0)
					G = 0;
				else if (G > 255)
					G = 255;
				int B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
				if (B < 0)
					B = 0;
				else if (B > 255)
					B = 255;
				rgbbuffer[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R;
			}
		}
		
	}
	
	/** Create a File for saving an image or video */
	private File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.
		
		// Create the storage directory if it does not exist
		if (!patientDirectory.exists()) {
			if (!patientDirectory.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}
		
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile = null;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(patientDirectory.getPath() + File.separator
					+ "IMG_" + timeStamp + ".JPEG");
		}
		else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(patientDirectory.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
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
	
	private class ViewImageDialog extends DialogFragment {
		File fileToDisplay;
		
		ViewImageDialog(File fileToDisplay) {
			this.fileToDisplay = fileToDisplay;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle bundle) {
			Dialog dialog = super.onCreateDialog(bundle);
			dialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setContentView(R.layout.view_pic_dialog);
			
			Button cancelButton = (Button) dialog
					.findViewById(R.id.view_pic_cancel);
			Button saveButton = (Button) dialog
					.findViewById(R.id.view_pic_save);
			// ImageView picImageView = (ImageView) dialog
			// .findViewById(R.id.previewImageView);
			//
			// if (fileToDisplay.exists()) {
			// picImageView.setImageURI(Uri.fromFile(fileToDisplay));
			// }
			
			cancelButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (fileToDisplay.exists()) {
						fileToDisplay.delete();
					}
					dismiss();
				}
			});
			
			saveButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
			return dialog;
		}
	}
	
	private void releaseCamera() {
		if (camera != null) {
			camera.release(); // release the camera for other applications
			camera = null;
		}
	}
	
}
