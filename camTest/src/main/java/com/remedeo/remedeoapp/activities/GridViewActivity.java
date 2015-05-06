package com.remedeo.remedeoapp.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aviary.android.feather.library.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.remedeo.remedeoapp.R;
import com.remedeo.remedeoapp.adpater.ImageAdapter;
import com.remedeo.remedeoapp.adpater.RearrangeAdapter;
import com.remedeo.remedeoapp.entities.PatientEntity;
import com.remedeo.remedeoapp.helper.PatientHelper;
import com.remedeo.remedeoapp.utils.GetImageFiles;
import com.remedeo.remedeoapp.utils.PDFGenerator;
import com.remedeo.remedeoapp.utils.Utils;

public class GridViewActivity extends FragmentActivity {
	private static final String TAG = "GridViewActivity";
	String imgPath;
	ImageAdapter myImageAdapter;
	private GridView gridview;
	private List<String> filePathList;
	private List<Integer> delteFileList = new ArrayList<Integer>();
	private static ImageView sendEmailButton;
	private static LinearLayout buttonLayout;
	private String directoryPath;
	private boolean showAll = false;
	private static PatientEntity patientObject;
	private static final String PATIENT_OBJECT = "PATIENT_OBJECT";
	
	private static Button exportButton;
	private ImageLoader imageLoader;
	
	private final static int CAMERA_RESULT = 1;
	private static final int REQUEST_VIDEO_CAPTURE = 2;
	private Uri imageUri;
	
	private static final String MP4_FORMAT = ".mp4";
	private static final int PIC_CROP = 3;
	
	private Button takeMorePhotoButton;
	private static Button deleteButton;
	
	private int cropCount = 0;
	
	private List<String> exportFileList;
	
	private boolean isCropRequested = false;
	
	private static final String VIDEO_PATH = "VIDEO_PATH";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gridview_activity);
		gridview = (GridView) findViewById(R.id.mygridview);
		
		deleteButton = (Button) findViewById(R.id.selectBtn);
		
		sendEmailButton = (ImageView) findViewById(R.id.share_imageview);
		
		buttonLayout = (LinearLayout) findViewById(R.id.lin);
		
		exportButton = (Button) findViewById(R.id.export_button);
		
		directoryPath = getIntent().getStringExtra("directoryPath");
		
		takeMorePhotoButton = (Button) findViewById(R.id.take_more_photo);
		
		patientObject = (PatientEntity) getIntent().getSerializableExtra(
				PATIENT_OBJECT);
		
		if (patientObject == null) {
			patientObject = new PatientEntity();
		}
		showAll = getIntent().getBooleanExtra("showAll", false);
		
		if (showAll) {
			takeMorePhotoButton.setVisibility(View.GONE);
		}
		else {
			takeMorePhotoButton.setVisibility(View.VISIBLE);
		}
		
		deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (delteFileList.size() > 0) {
					showAlertForDelete();
				}
				else {
					Toast.makeText(getApplicationContext(),
							"Please Select Atleast One Image",
							Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
		sendEmailButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (delteFileList.size() > 0) {
					
					Intent emailIntent = new Intent(
							android.content.Intent.ACTION_SEND_MULTIPLE);
					
					// emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
					// new String[] { "vasu0123@gmail.com" });
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
							"Remidio Pics");
					// emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					// "PFA the images");
					
					// emailIntent.setType("message/rfc822");
					
					emailIntent.setType("image/jpeg");
					
					ArrayList<Uri> uris = new ArrayList<Uri>();
					
					Iterator<String> iterator = filePathList.iterator();
					int counter = 0;
					while (iterator.hasNext()) {
						String pp = iterator.next();
						if (delteFileList.contains((Integer) counter)) {
							File f = new File(pp);
							if (f.exists()) {
								uris.add(Uri.parse("file:///" + pp));
							}
						}
						counter++;
					}
					
					emailIntent.putExtra(Intent.EXTRA_STREAM, uris);
					
					startActivity(Intent.createChooser(emailIntent,
							"Send Images..."));
					
				}
				else {
					Toast.makeText(getApplicationContext(),
							"Please Select Atleast One Image",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		exportButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				DialogFragment dialog = new ArrangePicDialog();
				dialog.show(getSupportFragmentManager(), "Rearrange");
				// showCropDialog();
			}
		});
		
		takeMorePhotoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment dialog = new ActionChooserDialog();
				dialog.show(getSupportFragmentManager(), "TakeMore");
				//
				// Intent in = new Intent(getApplicationContext(),
				// CameraActivity.class);
				// in.putExtra(PATIENT_OBJECT, patientObject);
				// in.putExtra("directoryPath", directoryPath);
				// startActivity(in);
				// finish();
			}
		});
		
		// gridview.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// String fileName = patientObject.getPhotoFilePathList().get(
		// position);
		// if (fileName.endsWith(".mp4")) {
		// Intent intent = new Intent(GridViewActivity.this,
		// FullVideoActivity.class);
		// intent.putExtra(VIDEO_PATH, fileName);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
		// | Intent.FLAG_ACTIVITY_NEW_TASK);
		// startActivity(intent);
		// }
		// else {
		// Intent intent = new Intent(GridViewActivity.this,
		// FullImageActivity.class);
		// intent.putExtra("imagepath", fileName);
		// intent.putStringArrayListExtra(
		// "fileList",
		// new ArrayList<String>(patientObject
		// .getPhotoFilePathList()));
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
		// | Intent.FLAG_ACTIVITY_NEW_TASK);
		// startActivity(intent);
		// }
		// }
		// });
		
	}
	
	private void showCropDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("Crop Image?");
		
		alertDialogBuilder.setPositiveButton("Crop",
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						
						try {
							cropCount = 0;
							
							File file = new File(Environment
									.getExternalStorageDirectory()
									+ File.separator + "croppedPic");
							if (!file.exists()) {
								file.mkdirs();
							}
							File croppedFile = new File(file.getAbsolutePath()
									+ File.separator
									+ System.currentTimeMillis() + ".jpeg");
							croppedFile.createNewFile();
							cropPic(exportFileList.get(0), croppedFile);
							isCropRequested = true;
						}
						catch (IOException e) {
							Log.e("ERROR", "IOException", e);
						}
						
					}
					
				});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						isCropRequested = false;
						new ExportPdfAsyncTask().execute(patientObject);
						
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.setCancelable(false);
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();
	}
	
	private void showAlertForDelete() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("Delete this images?");
		
		alertDialogBuilder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Iterator<String> iterator = filePathList.iterator();
						int counter = 0;
						while (iterator.hasNext()) {
							String pp = iterator.next();
							if (delteFileList.contains((Integer) counter)) {
								File f = new File(pp);
								if (f.exists()) {
									iterator.remove();
									f.delete();
								}
							}
							counter++;
						}
						
						if (delteFileList.size() == 0) {
							buttonLayout.setVisibility(View.GONE);
							sendEmailButton.setVisibility(View.GONE);
						}
						
						myImageAdapter.notifyDataSetChanged();
					}
					
				});
		alertDialogBuilder.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.setCancelable(false);
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();
		
	}
	
	private void cropPic(String path, File croppedFile) {
		
		Intent newIntent = new Intent(this,
				com.aviary.android.feather.sdk.FeatherActivity.class);
		newIntent.setData(Uri.parse(path));
		newIntent.putExtra(Constants.EXTRA_OUTPUT_QUALITY, 100);
		newIntent.putExtra(Constants.EXTRA_OUTPUT, Uri.fromFile(croppedFile));
		newIntent.putExtra(Constants.EXTRA_IN_API_KEY_SECRET,
				Utils.AVIARY_SECRET);
		newIntent.putExtra(Constants.EXTRA_TOOLS_LIST, new String[] { "CROP",
				"FOCUS" });
		
		startActivityForResult(newIntent, PIC_CROP);
	}
	
	public void copyFile(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);
		
		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}
	
	// Async task for exporting the PDF file
	private class ExportPdfAsyncTask extends
			AsyncTask<PatientEntity, Void, Void> {
		ProgressDialog dialog;
		private File pdfFile = null;
		
		@Override
		public void onPreExecute() {
			dialog = new ProgressDialog(GridViewActivity.this);
			dialog.setMessage("Exporting PDF...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}
		
		@Override
		protected Void doInBackground(PatientEntity... params) {
			pdfFile = PDFGenerator.generatePDF(params[0], directoryPath,
					getApplicationContext(), isCropRequested);
			return null;
		}
		
		@Override
		public void onPostExecute(Void param) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			if (pdfFile != null && pdfFile.exists()) {
				
				PackageManager packageManager = getPackageManager();
				Intent intent = new Intent(Intent.ACTION_VIEW)
						.setType("application/pdf");
				List<ResolveInfo> list = packageManager.queryIntentActivities(
						intent, PackageManager.MATCH_DEFAULT_ONLY);
				if (list.size() > 0) {
					Uri path = Uri.fromFile(pdfFile);
					intent.setDataAndType(path, "application/pdf");
					startActivity(intent);
				}
				else {
					// No PDF reader, ask the user to download one first
					// or just open it in their browser like this
					intent = new Intent(Intent.ACTION_VIEW)
							.setData(Uri
									.parse("https://play.google.com/store/apps/details?id=com.adobe.reader"));
					startActivity(intent);
				}
			}
		}
	}
	
	private void test() {
		PrintManager printManager = (PrintManager) this
				.getSystemService(Context.PRINT_SERVICE);
		
		String jobName = this.getString(R.string.app_name) + " Document";
		
		printManager.print(jobName, new MyPrintDocumentAdapter(this), null);
		
	}
	
	public class MyPrintDocumentAdapter extends PrintDocumentAdapter {
		Context context;
		private int pageHeight;
		private int pageWidth;
		public PdfDocument myPdfDocument;
		public int totalpages = 1;
		
		public MyPrintDocumentAdapter(Context context) {
			this.context = context;
		}
		
		@Override
		public void onLayout(PrintAttributes oldAttributes,
				PrintAttributes newAttributes,
				CancellationSignal cancellationSignal,
				LayoutResultCallback callback, Bundle metadata) {
			myPdfDocument = new PrintedPdfDocument(context, newAttributes);
			
			pageHeight = newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
			pageWidth = newAttributes.getMediaSize().getWidthMils() / 1000 * 72;
			
			if (cancellationSignal.isCanceled()) {
				callback.onLayoutCancelled();
				return;
			}
			
			if (totalpages > 0) {
				PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder(
						"Remedio.pdf").setContentType(
						PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).setPageCount(
						totalpages);
				
				PrintDocumentInfo info = builder.build();
				callback.onLayoutFinished(info, true);
			}
			else {
				callback.onLayoutFailed("Page count is zero.");
			}
			
		}
		
		@Override
		public void onWrite(final PageRange[] pageRanges,
				final ParcelFileDescriptor destination,
				final CancellationSignal cancellationSignal,
				final WriteResultCallback callback) {
			
			for (int i = 0; i < totalpages; i++) {
				if (pageInRange(pageRanges, i)) {
					PageInfo newPage = new PageInfo.Builder(pageWidth,
							pageHeight, i).create();
					
					PdfDocument.Page page = myPdfDocument.startPage(newPage);
					
					if (cancellationSignal.isCanceled()) {
						callback.onWriteCancelled();
						myPdfDocument.close();
						myPdfDocument = null;
						return;
					}
					drawPage(page, i);
					myPdfDocument.finishPage(page);
				}
			}
			
			try {
				myPdfDocument.writeTo(new FileOutputStream(destination
						.getFileDescriptor()));
			}
			catch (IOException e) {
				callback.onWriteFailed(e.toString());
				return;
			}
			finally {
				myPdfDocument.close();
				myPdfDocument = null;
			}
			
			callback.onWriteFinished(pageRanges);
		}
		
	}
	
	private void drawPage(PdfDocument.Page page, int pagenumber) {
		Canvas canvas = page.getCanvas();
		
		pagenumber++; // Make sure page numbers start at 1
		
		int titleBaseLine = 72;
		int leftMargin = 54;
		
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(40);
		canvas.drawText("Test Print Document Page " + pagenumber, leftMargin,
				titleBaseLine, paint);
		
		paint.setTextSize(14);
		canvas.drawText(
				"This is some test content to verify that custom document printing works",
				leftMargin, titleBaseLine + 35, paint);
		
		if (pagenumber % 2 == 0)
			paint.setColor(Color.RED);
		else
			paint.setColor(Color.GREEN);
		
		PageInfo pageInfo = page.getInfo();
		
		canvas.drawCircle(pageInfo.getPageWidth() / 2,
				pageInfo.getPageHeight() / 2, 150, paint);
	}
	
	private boolean pageInRange(PageRange[] pageRanges, int page) {
		for (int i = 0; i < pageRanges.length; i++) {
			if ((page >= pageRanges[i].getStart())
					&& (page <= pageRanges[i].getEnd()))
				return true;
		}
		return false;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration
				.createDefault(getApplicationContext()));
		
		new SetImageNVideoAsyncTask().execute();
	}
	
	private class SetImageNVideoAsyncTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... params) {
			if (directoryPath != null && !directoryPath.isEmpty()) {
				filePathList = GetImageFiles
						.getFilesForDirectory(directoryPath);
			}
			else if (showAll) {
				filePathList = GetImageFiles.getFiles();
			}
			
			return null;
		}
		
		@Override
		public void onPostExecute(Void param) {
			if (filePathList != null && !filePathList.isEmpty()) {
				myImageAdapter = new ImageAdapter(GridViewActivity.this,
						filePathList, delteFileList, imageLoader);
				gridview.setAdapter(myImageAdapter);
				
			}
		}
		
	}
	
	public static void changeVisiblityOfButtons(boolean visible) {
		if (visible) {
			deleteButton.setVisibility(View.VISIBLE);
			if (patientObject != null && patientObject.getGender() != null) {
				exportButton.setVisibility(View.VISIBLE);
			}
			sendEmailButton.setVisibility(View.VISIBLE);
		}
		else {
			deleteButton.setVisibility(View.INVISIBLE);
			exportButton.setVisibility(View.INVISIBLE);
			sendEmailButton.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
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
			String fileName = null;
			if (resultCode == RESULT_OK) {
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
					Log.i(TAG, "height of bitmap is" + bmp.getHeight());
					Log.i(TAG, "width of bitmap is" + bmp.getWidth());
					
					EditText tempEditText = new EditText(
							getApplicationContext());
					
					tempEditText.setText("Id: "
							+ patientObject.getPatientId()
							+ " Name: "
							+ patientObject.getName()
							+ " DOB: "
							+ patientObject.getDOB()
							+ "\nDate:"
							+ new SimpleDateFormat("d/MM/yyyy H:m:s")
									.format(System.currentTimeMillis())
							+ "\nHospital Name:"
							+ patientObject.getHospitalName());
					
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
					fileName = saveToCacheFile(combined);
					
					tempEditText.setDrawingCacheEnabled(false);
					
					// deleting a photo from gallary
					getContentResolver().delete(imageUri, null, null);
				}
				else {
					Toast.makeText(getApplicationContext(),
							"Please take this photo again", Toast.LENGTH_SHORT)
							.show();
				}
				
				filePathList.add(fileName);
				
				capturePhoto();
			}
			else {
				myImageAdapter.notifyDataSetChanged();
			}
			
		}
		else if (requestCode == REQUEST_VIDEO_CAPTURE) {
			if (resultCode == RESULT_OK) {
				
				if (intent != null) {
					Uri videoUri = intent.getData();
					try {
						String savedVideoPath = videoUri.getPath();
						
						Log.i(TAG, "Saved path is " + savedVideoPath);
						if (savedVideoPath != null) {
							filePathList.add(savedVideoPath);
						}
					}
					catch (Throwable e) {
						Toast.makeText(getApplicationContext(),
								"Please take this video again",
								Toast.LENGTH_SHORT).show();
					}
					captureVideo();
				}
			}
			else {
				
				myImageAdapter.notifyDataSetChanged();
			}
		}
		else if (requestCode == PIC_CROP) {
			if (resultCode == RESULT_OK) {
				if (intent != null) {
					// get the returned data
					
					// Bitmap extras = BitmapFactory.decodeFile(exportFileList
					// .get(cropCount));
					File file = new File(
							Environment.getExternalStorageDirectory()
									+ File.separator + "croppedPic");
					
					try {
						
						cropCount++;
						
						if (cropCount < exportFileList.size()) {
							
							if (!file.exists()) {
								file.mkdirs();
							}
							File croppedFile = new File(file.getAbsolutePath()
									+ File.separator
									+ System.currentTimeMillis() + ".jpeg");
							croppedFile.createNewFile();
							
							// FileOutputStream fos = new
							// FileOutputStream(fileName);
							// extras.compress(CompressFormat.JPEG, 100, fos);
							// fos.flush();
							// fos.close();
							cropPic(exportFileList.get(cropCount), croppedFile);
						}
						else {
							if (isCropRequested) {
								List<String> croppedFiles = new ArrayList<String>();
								String[] files = file.list();
								for (int i = 0; i < files.length; i++) {
									croppedFiles.add(file.getAbsolutePath()
											+ File.separator + files[i]);
								}
								patientObject
										.setPhotoFilePathList(croppedFiles);
							}
							new ExportPdfAsyncTask().execute(patientObject);
						}
					}
					catch (IOException e) {
						Log.e("exc", "IOException", e);
					}
				}
			}
		}
		
	}
	
	public String saveToCacheFile(Bitmap bmp) {
		File file = null;
		try {
			file = new File(getSavePath().getAbsolutePath() + File.separator
					+ String.valueOf(System.currentTimeMillis() + ".JPEG"));
			FileOutputStream out = new FileOutputStream(file);
			bmp.compress(CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		}
		catch (IOException e) {
			Log.e(TAG, "Exception", e);
		}
		return file.getAbsolutePath();
	}
	
	public File getSavePath() {
		
		StringBuilder folderName = new StringBuilder();
		if (patientObject != null && patientObject.getGender() != null) {
			
			folderName.append(patientObject.getPatientId());
			
			folderName.append("_" + patientObject.getName());
			
			folderName.append("_" + patientObject.getDOB());
		}
		else {
			folderName.append(directoryPath);
		}
		
		File folder = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Remidio/" + folderName.toString());
		if (!folder.exists()) {
			folder.mkdirs();
			Log.i("foldercreated", " by return statement");
		}
		
		return folder;
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
					
					Intent in = new Intent(getApplicationContext(),
							TakePicActivity.class);
					in.putExtra(PATIENT_OBJECT, patientObject);
					in.putExtra("directoryPath", directoryPath);
					
					dialog.dismiss();
					startActivity(in);
					// finish();
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
	
	@SuppressLint("ValidFragment")
	public class ArrangePicDialog extends DialogFragment {
		// private static final String TAG = "datepicker";
		private List<String> newList;
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Dialog dialog = super.onCreateDialog(savedInstanceState);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
			dialog.setContentView(R.layout.dialog_rearrange);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			exportFileList = new ArrayList<String>();
			Iterator<String> iterator = filePathList.iterator();
			int counter = 0;
			while (iterator.hasNext()) {
				String pp = iterator.next();
				if (delteFileList.contains((Integer) counter)) {
					File f = new File(pp);
					if ((pp.endsWith("JPEG") || pp.endsWith("JPG"))
							&& f.exists()) {
						exportFileList.add(pp);
					}
				}
				counter++;
			}
			
			newList = new ArrayList<String>();
			
			final GridView gridview = (GridView) dialog
					.findViewById(R.id.arrangeGridView);
			Button okButton = (Button) dialog.findViewById(R.id.arrangeok);
			Button cancelButton = (Button) dialog
					.findViewById(R.id.arrangecancel);
			
			gridview.setAdapter(new RearrangeAdapter(GridViewActivity.this,
					exportFileList, newList, imageLoader));
			
			okButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (newList.size() > 0) {
						exportFileList = newList;
					}
					patientObject.setPhotoFilePathList(exportFileList);
					dismiss();
					showComment();
					
				}
				
			});
			
			cancelButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					newList = new ArrayList<String>();
					gridview.setAdapter(new RearrangeAdapter(
							GridViewActivity.this, exportFileList, newList,
							imageLoader));
				}
			});
			
			return dialog;
		}
	}
	
	private void showComment() {
		final EditText input = new EditText(GridViewActivity.this);
		
		input.setFilters(new InputFilter[] { new LengthFilter(60) });
		new AlertDialog.Builder(GridViewActivity.this)
				.setTitle("Add Comment")
				
				.setView(input)
				.setPositiveButton("Add",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								patientObject.setComment(input.getText()
										.toString());
								PatientHelper.updatePatient(
										GridViewActivity.this, patientObject);
								showCropDialog();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								showCropDialog();
							}
						}).show();
	}
}
