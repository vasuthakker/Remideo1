package com.remedeo.remedeoapp.activities;

import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.remedeo.remedeoapp.R;

public class RGBActivity extends Activity {
	
	private RelativeLayout frame;
	private SeekBar redSeekBar;
	private SeekBar blueSeekBar;
	private SeekBar greenSeekBar;
	private SeekBar alphaSeekBar;
	
	int red = 255;
	int blue = 255;
	int green = 255;
	int alpha = 255;
	
	private String imagePath;
	
	private ImageLoader imageLoader;
	
	private ImageView imageImageView;
	
	private TextView redValueTextView;
	private TextView greenValueTextView;
	private TextView blueValueTextView;
	private TextView alphaValueTextView;
	
	private RelativeLayout imageFullLayout;
	
	private ImageView saveImageView;
	private static final String IMAGEPATH = "IMAGEPATH";
	protected static final int FULL_PROGRESS = 510;
	private Bitmap bmp;
	
	private ImageView imgRedFull;
	private ImageView imgGreenFull;
	private ImageView imgBlueFull;
	private ImageView imgAlphaFull;
	private ImageView imgRedEmpty;
	private ImageView imgGreenEmpty;
	private ImageView imgBlueEmpty;
	private ImageView imgAlphaEmpty;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rgb);
		
		frame = (RelativeLayout) findViewById(R.id.frame);
		redSeekBar = (SeekBar) findViewById(R.id.redseekbar);
		greenSeekBar = (SeekBar) findViewById(R.id.greenseekbar);
		blueSeekBar = (SeekBar) findViewById(R.id.blueseekbar);
		alphaSeekBar = (SeekBar) findViewById(R.id.alphaseekbar);
		
		imagePath = getIntent().getStringExtra(IMAGEPATH);
		
		redValueTextView = (TextView) findViewById(R.id.red_value_textview);
		greenValueTextView = (TextView) findViewById(R.id.green_value_textview);
		blueValueTextView = (TextView) findViewById(R.id.blue_value_textview);
		alphaValueTextView = (TextView) findViewById(R.id.alpha_textview);
		
		imgRedFull = (ImageView) findViewById(R.id.red_full);
		imgGreenFull = (ImageView) findViewById(R.id.green_full);
		imgBlueFull = (ImageView) findViewById(R.id.blue_full);
		imgAlphaFull = (ImageView) findViewById(R.id.alpha_full);
		imgAlphaEmpty = (ImageView) findViewById(R.id.alpha_empty);
		imgBlueEmpty = (ImageView) findViewById(R.id.blue_empty);
		imgGreenEmpty = (ImageView) findViewById(R.id.green_empty);
		imgRedEmpty = (ImageView) findViewById(R.id.red_empty);
		
		//alphaSeekBar.setProgress(alpha);
		//alphaValueTextView.setText(String.valueOf(alpha));
		
		imgRedFull.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				redSeekBar.setProgress(FULL_PROGRESS);
			}
		});
		imgGreenFull.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				greenSeekBar.setProgress(FULL_PROGRESS);
			}
		});
		imgBlueFull.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				blueSeekBar.setProgress(FULL_PROGRESS);
			}
		});
		imgAlphaFull.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alphaSeekBar.setProgress(FULL_PROGRESS);
			}
		});
		
		imgRedEmpty.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				redSeekBar.setProgress(0);
			}
		});
		imgGreenEmpty.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				greenSeekBar.setProgress(0);
			}
		});
		imgBlueEmpty.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				blueSeekBar.setProgress(0);
			}
		});
		imgAlphaEmpty.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alphaSeekBar.setProgress(0);
			}
		});
		
	/*	imgRedFull.setColorFilter(Color.RED, Mode.ADD);
		imgRedEmpty.setColorFilter(Color.RED, Mode.ADD);
		imgBlueFull.setColorFilter(Color.BLUE, Mode.ADD);
		imgBlueEmpty.setColorFilter(Color.BLUE, Mode.ADD);
		imgGreenEmpty.setColorFilter(Color.GREEN, Mode.ADD);
		imgGreenFull.setColorFilter(Color.GREEN, Mode.ADD);
		*/
		saveImageView = (ImageView) findViewById(R.id.save_image_imageview);
		
		imageFullLayout = (RelativeLayout) findViewById(R.id.image_full_layout);
		
		imageImageView = (ImageView) findViewById(R.id.rgb_image_imageview);
		
		// frame.setBackgroundColor(Color.argb(50, 0, 0, 0));
		redSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				red = progress;
				redValueTextView.setText(String.valueOf(progress - 255));
				// frame.setBackgroundColor(Color.argb(alpha, red, green,
				// blue));
				setMatrix(red, green, blue, alpha);
			}
		});
		greenSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				green = progress;
				greenValueTextView.setText(String.valueOf(progress - 255));
				// frame.setBackgroundColor(Color.argb(alpha, red, green,
				// blue));
				setMatrix(red, green, blue, alpha);
			}
		});
		blueSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				blue = progress;
				blueValueTextView.setText(String.valueOf(progress - 255));
				// frame.setBackgroundColor(Color.argb(alpha, red, green,
				// blue));
				setMatrix(red, green, blue, alpha);
			}
		});
		alphaSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				alpha = progress;
				alphaValueTextView.setText(String.valueOf(progress - 255));
				// frame.setBackgroundColor(Color.argb(alpha, red, green,
				// blue));
				setMatrix(red, green, blue, alpha);
			}
		});
		
		saveImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveFile(imagePath);
				finish();
			}
		});
	}
	
	private void setMatrix(int red, int green, int blue, int alpha) {
		float redValue = ((float) red) / 255;
		float greenValue = ((float) green) / 255;
		float blueValue = ((float) blue) / 255;
		float alphaValue = ((float) alpha) / 255;
		
		ColorMatrix cm = new ColorMatrix();
		cm.set(new float[] { redValue, 0, 0, 0, 0, 0, greenValue, 0, 0, 0, 0,
				0, blueValue, 0, 0, 0, 0, 0, alphaValue, 0 });
		// cm.setSaturation(progress - 5);
		imageImageView.setColorFilter(new ColorMatrixColorFilter(cm));
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration
				.createDefault(getApplicationContext()));
		
		if (imagePath != null && !imagePath.isEmpty()) {
			imageLoader.displayImage("file://" + imagePath, imageImageView);
			// Matrix mat = new Matrix();
			// mat.postRotate(90);
			//
			// Bitmap loadedImage=BitmapFactory.decodeFile(imagePath);
			
			// mainImageView.setImageBitmap(Bitmap.createBitmap(loadedImage, 0,
			// 0, loadedImage.getWidth(), loadedImage.getHeight(), mat, true));
			
			// imageLoader.loadImage("file://" + imagePath,
			// new SimpleImageLoadingListener() {
			// @Override
			// public void onLoadingComplete(String imageUri,
			// View view, Bitmap loadedImage) {
			// // Matrix mat = new Matrix();
			// // mat.postRotate(90);
			// //
			// // imageView.setImageBitmap(Bitmap.createBitmap(
			// // loadedImage, 0, 0, loadedImage.getWidth(),
			// // loadedImage.getHeight(), mat, true));
			// bmp = loadedImage;
			// imageImageView.setImageBitmap(loadedImage);
			// }
			// });
			
			// mainImageView.setImageBitmap(loadedImage);
			
		}
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		System.gc();
	}
	
	private void saveFile(String imagepath) {
		try {
			// imageFullLayout.setDrawingCacheEnabled(true);
			FileOutputStream out = new FileOutputStream(imagepath);
			// Bitmap bm =
			// Bitmap.createBitmap(imageFullLayout.getDrawingCache(),
			// 0, 0, bmp.getWidth(), bmp.getHeight());
			Bitmap bm = loadBitmapFromView(imageImageView);
			
			bm.compress(CompressFormat.JPEG, 100, out);
			imageFullLayout.setDrawingCacheEnabled(false);
			out.flush();
			out.close();
		}
		catch (IOException e) {
			Log.e("ex", "IOException", e);
		}
	}
	
	private Bitmap loadBitmapFromView(View v) {
		Bitmap bm = ((BitmapDrawable) imageImageView.getDrawable()).getBitmap();
		final Bitmap b = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
				Bitmap.Config.ARGB_8888);
		final Canvas c = new Canvas(b);
		// v.layout(0, 0, w, h);
		v.layout(v.getLeft(), v.getTop(), bm.getWidth(), bm.getHeight());
		v.draw(c);
		return b;
	}
}
