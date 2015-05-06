package com.remedeo.remedeoapp.activities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.remedeo.remedeoapp.R;
import com.remedeo.remedeoapp.entities.PatientEntity;
import com.remedeo.remedeoapp.helper.PatientHelper;
import com.remedeo.remedeoapp.utils.Utils;

public class MainActivity extends FragmentActivity {
	// REMIDO APP NAME
	final static int CAMERA_RESULT = 1;
	private static final int REQUEST_VIDEO_CAPTURE = 2;
	private static final String TAG = "MainActivity";
	private static final int GENDER_MALE = 0;
	private static final String PATIENT_OBJECT = "PATIENT_OBJECT";
	
	private static final String MP4_FORMAT = ".mp4";
	private static final String DOCTOR_NAME = "DOCTOR_NAME";
	private static final String HOSPITAL_NAME = "HOSPITAL_NAME";
	private static final String FOLDER_MAIN = "Remidio";
	// final static int Grid_Result = 1;
	int width = 0, height = 0;
	ImageView imv;
	// ,myimagetest;
	Button saveButton;
	LinearLayout view_pics_button;
	// REMIDO
	// Uri imageFileUri;
	static String FldrName;
	static int incrval = 1;
	// Button takePictureButton;
	public static String MyPath;
	
	private EditText patedtidEditText;
	private EditText patedtNameEditText;
	private EditText patedtdob;
	private EditText patedtage;
	private EditText patedtcomm;
	private EditText patedtdrname;
	private Uri imageUri;
	private EditText hospitalNameEditText;
	private Spinner genderSpinner;
	// private List<String> filePathList;
	private String patientId;
	private Button exportButton;
	private ImageView adminSectionImageView;
	
	private static final String EXPORTFILE = "export.json";
	private static final String ADDRESS = "ADDRESS";
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		
		setContentView(R.layout.activity_main);
		// myimagetest = (ImageView)findViewById(R.id.imageView1);
		saveButton = (Button) findViewById(R.id.TakePictureButton);
		view_pics_button = (LinearLayout) findViewById(R.id.view_pics_button);
		patedtidEditText = (EditText) findViewById(R.id.edtID);
		patedtNameEditText = (EditText) findViewById(R.id.edtName);
		patedtdob = (EditText) findViewById(R.id.edtDOB);
		patedtage = (EditText) findViewById(R.id.edtAge);
		patedtcomm = (EditText) findViewById(R.id.edtComm);
		patedtdrname = (EditText) findViewById(R.id.edtDrName);
		view_pics_button.setVisibility(View.GONE);
		genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
		hospitalNameEditText = (EditText) findViewById(R.id.hospital_name_edittext);
		exportButton = (Button) findViewById(R.id.exportButton);
		exportButton.setVisibility(View.GONE);
		
		adminSectionImageView = (ImageView) findViewById(R.id.main_admin);
		// doctor name and hospital name
		String doctorName = Utils.getPreference(getApplicationContext(),
				DOCTOR_NAME);
		String hospitalName = Utils.getPreference(getApplicationContext(),
				HOSPITAL_NAME);
		String address = Utils.getPreference(getApplicationContext(),
						ADDRESS);
		
		if (doctorName != null && !doctorName.isEmpty()) {
			patedtdrname.setText(doctorName);
		}
		if (hospitalName != null && !hospitalName.isEmpty()) {
			hospitalNameEditText.setText(hospitalName);
		}
		if (address != null && !address.isEmpty()) {
			patedtcomm.setText(address);
		}
		
		patedtidEditText.setText("");
		
		final DisplayMetrics metrics = new DisplayMetrics();
		Display display = getWindowManager().getDefaultDisplay();
		display.getRealMetrics(metrics);
		width = metrics.widthPixels;
		height = metrics.heightPixels;
		
		// filePathList = GetImageFiles.getFiles();
		
		// m = new MyFileContentProvider();
		view_pics_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// Intent in = new Intent(getApplicationContext(),
				// GridViewActivity.class);
				Intent in = new Intent(getApplicationContext(),
						DisplayDirectoriesActivity.class);
				// in.putExtra("showAll", true);
				startActivity(in);
				// finish();
			}
		});
		adminSectionImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						AdminActivity.class);
				startActivity(intent);
				
			}
		});
		
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// save inforamtion to a text file
				if (saveInfoToFile()) {
					DialogFragment dialog = new ActionChooserDialog();
					dialog.show(getSupportFragmentManager(), "ActionChooser");
					// finishCapturing();
				}
			}
		});
		
		patedtdob.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					DialogFragment dialog = new DatePickerDialogue();
					dialog.show(getSupportFragmentManager(), "DatePickerDOB");
				}
			}
		});
		
		patedtdob.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().length() > 0) {
					patedtdob.setError(null);
				}
			}
		});
		
		// // Age validity
		// patedtage.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before,
		// int count) {
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		// }
		//
		// @Override
		// public void afterTextChanged(Editable s) {
		// String value = s.toString();
		// if (value != null && value.trim().length() > 0) {
		// int age = Integer.parseInt(value);
		// if (age > 100 || age < 1) {
		// patedtage.setError("Age is not valid");
		// }
		// }
		// }
		// });
		
		/*
		 * exportButton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { //new
		 * ExportFileAsync().execute(); } });
		 */
		
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + FOLDER_MAIN + File.separator + EXPORTFILE);
		if (file.exists()) {
			new ImportFileAsync().execute();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		List<PatientEntity> list = PatientHelper.fetchPatients(this, null,
				null, null, null, null);
		if (list != null && !list.isEmpty()) {
			view_pics_button.setVisibility(View.VISIBLE);
			//exportButton.setVisibility(View.VISIBLE);
		}
		else {
			view_pics_button.setVisibility(View.GONE);
			//exportButton.setVisibility(View.GONE);
		}
	}
	
	private class ImportFileAsync extends AsyncTask<Void, Void, Boolean> {
		
		ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(MainActivity.this);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setMessage("Importing FIle");
			dialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			boolean isSuccess = false;
			File file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + FOLDER_MAIN + File.separator
					+ EXPORTFILE);
			try {
				if (file.exists()) {
					BufferedReader reader = new BufferedReader(new FileReader(
							file));
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
					Type listType = new TypeToken<ArrayList<PatientEntity>>() {
					}.getType();
					
					List<PatientEntity> list = new Gson().fromJson(
							sb.toString(), listType);
					
					List<PatientEntity> dbRecords = PatientHelper
							.fetchPatients(getApplicationContext(), null, null,
									null, null, null);
					if (dbRecords != null && !dbRecords.isEmpty()) {
						Iterator<PatientEntity> iterator = list.iterator();
						while (iterator.hasNext()) {
							PatientEntity patient = iterator.next();
							for (PatientEntity dbPatient : dbRecords) {
								if (patient.getPatientId().equals(
										dbPatient.getPatientId())) {
									iterator.remove();
									break;
								}
							}
						}
					}
					if (list != null && !list.isEmpty()) {
						PatientHelper.insertPatient(getApplicationContext(),
								list);
					}
					file.delete();
					isSuccess = true;
					
				}
			}
			catch (IOException e) {
				Log.e("IO", "IOException", e);
			}
			return isSuccess;
		}
		
		@Override
		protected void onPostExecute(Boolean param) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			if (param) {
				view_pics_button.setVisibility(View.VISIBLE);
			}
			
		}
		
	}
	
	// private class UpdateAppAsyncTask extends AsyncTask<Void, Void, Void> {
	//
	// @Override
	// protected Void doInBackground(Void... params) {
	// PackageInfo pInfo;
	// try {
	// pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
	//
	// int versionCode = pInfo.versionCode;
	//
	// Log.i(TAG, "Version code " + versionCode);
	// if (versionCode == 2) {
	// List<PatientEntity> newPatients = PatientHelper
	// .fetchPatients(getApplicationContext(), null, null,
	// null, null, null);
	// if (newPatients == null || newPatients.isEmpty()) {
	// List<PatientEntity> oldPatients = PatientHelper
	// .fetchOldPatients(getApplicationContext(),
	// null, null, null, null, null);
	// for (PatientEntity patient : oldPatients) {
	// PatientHelper.insertPatient(
	// getApplicationContext(), patient);
	// }
	// }
	// }
	// }
	// catch (NameNotFoundException e) {
	// Log.e(TAG, "NameNotFoundException", e);
	// }
	// return null;
	// }
	//
	// }
	
	private boolean saveInfoToFile() {
		boolean isAllValid = true;
		String patientId = patedtidEditText.getText().toString();
		if (patientId == null || patientId.trim().length() == 0) {
			patedtidEditText.setError(Html
					.fromHtml("<font color=white>Id is Missing</font>"));
			isAllValid = false;
		}
		else {
			
			List<PatientEntity> patientList = PatientHelper.fetchPatients(
					getApplicationContext(), PatientHelper.KEY_PATIENT_ID
							+ "=?", new String[] { patientId }, null, null,
					null);
			if (patientList != null && !patientList.isEmpty()) {
				isAllValid = false;
				patedtidEditText
						.setError(Html
								.fromHtml("<font color=white>Duplicate Id detected</font>"));
				Toast.makeText(getApplicationContext(),
						"Duplicate Id detected", Toast.LENGTH_SHORT).show();
				
			}
		}
		String patientName = patedtNameEditText.getText().toString();
		if (patientName == null || patientName.isEmpty()) {
			patedtNameEditText.setError(Html
					.fromHtml("<font color=white>Name is Missing</font>"));
			isAllValid = false;
		}
		
		String patientAge = patedtage.getText().toString();
		if (patientAge == null || patientAge.isEmpty()) {
			patedtage.setError(Html
					.fromHtml("<font color=white>Age is Missing</font>"));
			isAllValid = false;
		}
		
		String patientDOB = patedtdob.getText().toString();
		// if (patientDOB == null || patientDOB.isEmpty()) {
		// patedtdob.setError(Html
		// .fromHtml("<font color=white>DOB is Missing</font>"));
		// isAllValid = false;
		// }
		String patientDrName = patedtdrname.getText().toString();
		// if (patientDrName == null || patientDrName.isEmpty()) {
		// patedtdrname
		// .setError(Html
		// .fromHtml("<font color=white>Doctor Name is Missing</font>"));
		// isAllValid = false;
		//
		// }
		String address=patedtcomm.getText().toString();
		String patientHosiptalName = hospitalNameEditText.getText().toString();
		if (patientHosiptalName == null || patientHosiptalName.isEmpty()) {
			hospitalNameEditText
					.setError(Html
							.fromHtml("<font color=white>Hospital Name is Missing</font>"));
			isAllValid = false;
		}
		if (isAllValid) {
			StringBuilder folderName = new StringBuilder();
			
			folderName.append(patedtidEditText.getText().toString());
			
			folderName.append("_" + patedtNameEditText.getText().toString());
			
			folderName.append("_" + patedtdob.getText().toString());
			
			FldrName = folderName.toString();
			saveTxt();
			
			Utils.setPreference(getApplicationContext(), DOCTOR_NAME,
					patientDrName);
			Utils.setPreference(getApplicationContext(), HOSPITAL_NAME,
					patientHosiptalName);
			Utils.setPreference(getApplicationContext(), ADDRESS,
					address);
			
			return isAllValid;
		}
		return isAllValid;
	}
	
	// Method to capture photo
	private void capturePhoto() {
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, "New Picture");
		values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
		imageUri = getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, CAMERA_RESULT);
	}
	
	// Method to capture video
	private void captureVideo() {
		File newVideo = new File(getSavePath() + File.separator
				+ System.currentTimeMillis() + MP4_FORMAT);
		
		Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
			
			// create a file to save the video
			Uri fileUri = Uri.fromFile(newVideo);
			
			// set the image file name
			takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
			
			// set the video image quality to high
			takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == CAMERA_RESULT) {
			if (resultCode == RESULT_OK) {
				incrval++;
				Bitmap bmp = null;
				try {
					bmp = MediaStore.Images.Media.getBitmap(
							getContentResolver(), imageUri);
				}
				catch (FileNotFoundException e) {
					Log.e(TAG, "FileNotFoundException", e);
				}
				catch (IOException e) {
					Log.e(TAG, "IOException", e);
				}
				catch (Throwable e) {
					Log.e(TAG, "OutOfMemoryError", e);
				}
				
				if (bmp != null) {
					Matrix mat = new Matrix();
					mat.postRotate(0);
					bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
							bmp.getHeight(), mat, true);
					
					Log.i(TAG, "height of bitmap is" + bmp.getHeight());
					Log.i(TAG, "width of bitmap is" + bmp.getWidth());
					
					EditText tempEditText = new EditText(
							getApplicationContext());
					patientId = patedtidEditText.getText().toString();
					
					tempEditText.setText("Id: "
							+ patientId
							+ " Name: "
							+ patedtNameEditText.getText().toString()
							+ " DOB: "
							+ patedtdob.getText().toString()
							+ "\nDate:"
							+ new SimpleDateFormat("d/MM/yyyy H:m:s")
									.format(System.currentTimeMillis())
							+ "\nHospital Name:"
							+ hospitalNameEditText.getText());
					
					tempEditText.setTextColor(Color.WHITE);
					tempEditText.setBackgroundColor(Color.BLACK);
					tempEditText.setDrawingCacheEnabled(true);
					tempEditText.buildDrawingCache();
					tempEditText.setTextSize(26f);
					tempEditText.setWidth(800);
					tempEditText.setGravity(Gravity.LEFT
							| Gravity.CENTER_VERTICAL);
					tempEditText.layout(0, 0, 1600, 425);
					tempEditText.setPadding(30, 30, 0, 0);
					Bitmap bm1 = Bitmap.createBitmap(tempEditText
							.getDrawingCache());
					Bitmap combined = combineImages(bmp, bm1);
					saveToCacheFile(combined);
					
					tempEditText.setDrawingCacheEnabled(false);
					
					// deleting a photo from gallary
					getContentResolver().delete(imageUri, null, null);
				}
				else {
					Toast.makeText(getApplicationContext(),
							"Please take this photo again", Toast.LENGTH_SHORT)
							.show();
				}
				
				// Intent i = new Intent(
				// android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				// i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				// startActivityForResult(i, CAMERA_RESULT);
				
				capturePhoto();
			}
			else {
				finishCapturing();
			}
			
		}
		else if (requestCode == REQUEST_VIDEO_CAPTURE) {
			if (resultCode == RESULT_OK) {
				Uri videoUri = intent.getData();
				if (videoUri != null) {
					String savedVideoPath = videoUri.getPath();
					
					Log.i(TAG, "Saved path is " + savedVideoPath);
				}
				captureVideo();
			}
			else {
				finishCapturing();
			}
		}
		
	}
	
	// called when user presses back button from taking photo/video
	private void finishCapturing() {
		PatientEntity patient = new PatientEntity();
		patient.setAge(Integer.parseInt(patedtage.getText().toString()));
		patient.setName(patedtNameEditText.getText().toString());
		if (genderSpinner.getSelectedItemPosition() == GENDER_MALE) {
			patient.setGender("M");
		}
		else {
			patient.setGender("F");
		}
		patient.setPatientId(patedtidEditText.getText().toString());
		patient.setHospitalName(hospitalNameEditText.getText().toString());
		patient.setDOB(patedtdob.getText().toString());
		patient.setDoctorName(patedtdrname.getText().toString());
		patient.setAddress(patedtcomm.getText().toString());
		PatientHelper.insertPatient(null, getApplicationContext(), patient);
		
		Intent in = new Intent(getApplicationContext(), TakePicActivity.class);
		// Intent in = new Intent(getApplicationContext(),
		// GridViewActivity.class);
		in.putExtra(PATIENT_OBJECT, patient);
		in.putExtra("directoryPath", FldrName);
		
		// clear all the field
		patedtidEditText.setText("");
		patedtNameEditText.setText("");
		patedtdob.setText("");
		patedtage.setText("");
		//patedtcomm.setText("");
		
		startActivity(in);
		
		// finish();
	}
	
	private String getLastImagePathByCamera() {
		String imagePath = null;
		final String[] imageColumns = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DATA };
		final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
		Cursor imageCursor = managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns,
				null, null, imageOrderBy);
		if (imageCursor.moveToFirst()) {
			imagePath = imageCursor.getString(imageCursor
					.getColumnIndex(MediaStore.Images.Media.DATA));
			imageCursor.close();
		}
		return imagePath;
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
	
	public File getSavePath() {
		File folder;
		if (hasSDCard()) {
			folder = new File(getSDCardPath() + "/Remidio/" + FolderName());
			if (!folder.exists()) {
				folder.mkdirs();
				Log.i("foldercreated", " by return statement");
			}
		}
		else {
			folder = Environment.getDataDirectory();
		}
		return folder;
	}
	
	public String getCacheFilename() {
		File f = getSavePath();
		return f.getAbsolutePath() + "/" + FolderName() + "--" + incrval
				+ ".JPEG";
	}
	
	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	public Bitmap loadFromFile(String filename) {
		try {
			File f = new File(filename);
			if (!f.exists()) {
				return null;
			}
			Bitmap tmp = BitmapFactory.decodeFile(filename);
			return tmp;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public Bitmap loadFromCacheFile() {
		return loadFromFile(getCacheFilename());
	}
	
	public void saveToCacheFile(Bitmap bmp) {
		saveToFile(getCacheFilename(), bmp);
	}
	
	public void saveToFile(String filename, Bitmap bmp) {
		try {
			FileOutputStream out = new FileOutputStream(filename);
			bmp.compress(CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		}
		catch (Exception e) {
		}
	}
	
	public boolean hasSDCard() {
		// String status = Environment.getExternalStorageState();
		// Log.e("hasSdCard", "stutus is" + status);
		// return status.equals(Environment.MEDIA_MOUNTED);
		return true;
	}
	
	public String getSDCardPath() {
		File path = Environment.getExternalStorageDirectory();
		return path.getAbsolutePath();
	}
	
	public File getOutputMediaFile() {
		File file = getSavePath();
		File mediaFile;
		mediaFile = new File(file, FolderName() + ".txt");
		return mediaFile;
	}
	
	public String FolderName() {
		MyPath = "/" + FOLDER_MAIN + "/" + FldrName;
		return FldrName;
	}
	
	public void saveTxt() {
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(
					getOutputMediaFile()));
			
			output.write("\n Paitient Id : "
					+ patedtidEditText.getText().toString());
			output.newLine();
			output.write("\n Paitient Name : "
					+ patedtNameEditText.getText().toString());
			output.newLine();
			String gender;
			if (genderSpinner.getSelectedItemPosition() == GENDER_MALE) {
				gender = "Male";
			}
			else {
				gender = "Female";
			}
			output.write("\n Paitient Gender : " + gender);
			output.newLine();
			output.write("\n Paitient DOB : " + patedtdob.getText().toString());
			output.newLine();
			output.write("\n Paitient age : " + patedtage.getText().toString());
			output.newLine();
			output.write("\n Comments : " + patedtcomm.getText().toString());
			output.newLine();
			output.write("\n Dr. Name : " + patedtdrname.getText().toString());
			
			output.flush();
			output.close();
		}
		catch (FileNotFoundException e) {
			Log.e(TAG, "FileNotFoundException", e);
		}
		catch (IOException e) {
			Log.e(TAG, "IOException", e);
		}
		
	}
	
	public Bitmap drawTextToBitmap(Bitmap bitmap, String mText) {
		try {
			int scale = 30;
			android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
			// set default bitmap config if none
			if (bitmapConfig == null) {
				bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
			}
			// resource bitmaps are imutable,
			// so we need to convert it to mutable one
			bitmap = bitmap.copy(bitmapConfig, true);
			
			Canvas canvas = new Canvas(bitmap);
			// new antialised Paint
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			// text color - #3D3D3D
			paint.setColor(Color.rgb(110, 110, 110));
			// text size in pixels
			paint.setTextSize((int) (12 * scale));
			// paint.
			// text shadow
			paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);
			
			// draw text to the Canvas center
			Rect bounds = new Rect();
			paint.getTextBounds(mText, 0, mText.length(), bounds);
			int x = (bitmap.getWidth() - bounds.width()) / 6;
			int y = (bitmap.getHeight() + bounds.height()) / 7;
			
			canvas.drawText(mText, x * scale, y * scale, paint);
			
			return bitmap;
		}
		catch (Exception e) {
			return null;
		}
		
	}
	
	@SuppressLint("ValidFragment")
	public class DatePickerDialogue extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		// private static final String TAG = "datepicker";
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		@Override
		public void onDateSet(android.widget.DatePicker view, int year,
				int monthOfYear, int dayOfMonth) {
			Calendar calendar = new GregorianCalendar(year, monthOfYear,
					dayOfMonth);
			patedtdob.setText(DateFormat.format("dd-MMM-yyyy",
					calendar.getTimeInMillis()));
			
		}
		
	}
	
	@SuppressLint("ValidFragment")
	public class ActionChooserDialog extends DialogFragment {
		// private static final String TAG = "datepicker";
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Dialog dialog = super.onCreateDialog(savedInstanceState);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
			dialog.setContentView(R.layout.dialog_choose_video_image);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			RelativeLayout photoLayout = (RelativeLayout) dialog
					.findViewById(R.id.camera_layout);
			RelativeLayout videoLayout = (RelativeLayout) dialog
					.findViewById(R.id.video_layout);
			
			photoLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					// capture a photo
					// capturePhoto();
					finishCapturing();
					
					dialog.dismiss();
				}
			});
			
			videoLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					// capture video
					captureVideo();
					
					dialog.dismiss();
				}
			});
			return dialog;
		}
		
	}
	
}
