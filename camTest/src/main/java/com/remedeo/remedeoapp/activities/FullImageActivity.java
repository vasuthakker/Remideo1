package com.remedeo.remedeoapp.activities;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aviary.android.feather.library.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.remedeo.remedeoapp.R;
import com.remedeo.remedeoapp.utils.TouchImageView;
import com.remedeo.remedeoapp.utils.Utils;

public class FullImageActivity extends Activity {
	
	protected static final int EDIT_IMAGE = 1;
	private static final String TAG = "FullImageActivity";
	private ImageViewTouch imageView;
	private RelativeLayout editImageLayout;
	private static String imagePath;
	private static final String IMAGEPATH = "IMAGEPATH";
	private ImageView angleImageView;
	private ViewPager imageGallary;
	private RelativeLayout setRGBLayout;
	private List<String> filePathList;
	private ImageLoader imageLoader;
	private int currentPos = 0;
	private File copiedFile;
	private TextView totalImageCountTextView;
	
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	private PointF start = new PointF();
	
	private PointF mid = new PointF();
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	
	private int mode = NONE;
	private float oldDist = 1f;
	
	private int totalImageCount = 0;
	
	private String[] effectList = new String[] { "SPLASH", "EFFECTS",
			"SHARPNESS", "WHITEN", "BLEMISH", "TEXT", "REDEYE", "ENHANCE",
			"COLOR" };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_image);
		
		imageGallary = (ViewPager) findViewById(R.id.imagegallary);
		
		editImageLayout = (RelativeLayout) findViewById(R.id.edit_image_layout);
		setRGBLayout = (RelativeLayout) findViewById(R.id.rgb_layout);
		totalImageCountTextView = (TextView) findViewById(R.id.fullimage_totalCount);
		// Selected image id
		// int position = i.getExtras().getInt("id");
		
		imagePath = getIntent().getStringExtra("imagepath");
		filePathList = getIntent().getStringArrayListExtra("fileList");
		currentPos = getIntent().getIntExtra("currentPos", 0);
		
		if (filePathList != null) {
			totalImageCount = filePathList.size();
		}
		
		imageView = (ImageViewTouch) findViewById(R.id.full_image_view);
		imageView.setVisibility(View.VISIBLE);
		imageView.setDoubleTapEnabled(true);
		imageView.setDisplayType(DisplayType.FIT_IF_BIGGER);
		
		angleImageView = (ImageView) findViewById(R.id.angle_imageview);
		
		editImageLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String fileName = null;
				try {
					fileName = filePathList.get(imageGallary.getCurrentItem());
					copyFile(
							new File(fileName),
							new File(fileName.substring(0,
									fileName.lastIndexOf(File.separator))
									+ File.separator
									+ System.currentTimeMillis() + ".JPEG"));
				}
				catch (IOException e) {
					Log.e("IOException", "IOException", e);
				}
				// Intent newIntent = new Intent(getApplicationContext(),
				// FeatherActivity.class);
				// newIntent.setData(Uri.parse(fileName));
				// newIntent.putExtra(Constants.EXTRA_OUTPUT_QUALITY, 100);
				// newIntent.putExtra(Constants.EXTRA_OUTPUT,
				// Uri.parse(imagePath));
				// newIntent.putExtra(Constants.EXTRA_TOOLS_LIST, effectList);
				// newIntent.putExtra(
				// Constants.EXTRA_EFFECTS_ENABLE_EXTERNAL_PACKS, false);
				// startActivityForResult(newIntent, EDIT_IMAGE);
				
				Intent newIntent = new Intent(FullImageActivity.this,
						com.aviary.android.feather.sdk.FeatherActivity.class);
				newIntent.setData(Uri.parse(imagePath));
				newIntent.putExtra(Constants.EXTRA_IN_API_KEY_SECRET,
						Utils.AVIARY_SECRET);
				newIntent.putExtra(Constants.EXTRA_OUTPUT_QUALITY, 100);
				newIntent.putExtra(Constants.EXTRA_OUTPUT, Uri.parse(imagePath));
				// newIntent.putExtra(Constants.EXTRA_TOOLS_LIST, effectList);
				// newIntent.putExtra( Constants.EXTRA_IN_HIRES_MEGAPIXELS,
				// MegaPixels.Mp13.ordinal() );
				newIntent.putExtra(Constants.EXTRA_WHITELABEL, false);
				startActivityForResult(newIntent, 1);
				
			}
		});
		
		setRGBLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						RGBActivity.class);
				intent.putExtra(IMAGEPATH,
						filePathList.get(imageGallary.getCurrentItem()));
				startActivity(intent);
				
			}
		});
		
		angleImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(getApplicationContext(),
						AngleActivity.class);
				intent.putExtra(IMAGEPATH,
						filePathList.get(imageGallary.getCurrentItem()));
				startActivity(intent);
			}
		});
		
		totalImageCountTextView.setText("1 / " + totalImageCount);
		
		imageGallary.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
			
				currentPos = position;
				
				totalImageCountTextView.setText((position + 1) + " / "
						+ totalImageCount);
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration
				.createDefault(getApplicationContext()));
		
	}
	
	public void copyFile(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);
		
		copiedFile = dst;
		
		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}
	
	private class imageAdapter extends PagerAdapter {
		
		@Override
		public int getCount() {
			
			return filePathList.size();
		}
		
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((TouchImageView) object);
		}
		
		@Override
		public View instantiateItem(ViewGroup container, final int position) {
			final TouchImageView imageView = new TouchImageView(
					FullImageActivity.this);
			// imageView.setDoubleTapEnabled(true);
			// imageView.setDisplayType(DisplayType.FIT_IF_BIGGER);
			// imageLoader.loadImage("file://" + filePathList.get(position),
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
			// // imageView.setImageBitmap(loadedImage);
			//
			// Log.v("bmp", loadedImage.getHeight() + " "
			// + loadedImage.getWidth());
			//
			// // imageView.
			// }
			// });
			imageLoader.displayImage(
					Uri.fromFile(new File(filePathList.get(position)))
							.toString(), imageView);
			
			// imageView.setRotation(90f);
			// Add viewpager_item.xml to ViewPager
			//
			// imageView.setImageURI(Uri.fromFile(new File(filePathList
			// .get(position))));
			
			// imageView.setTag("myview" + position);
			((ViewPager) container).addView(imageView);
			return imageView;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(((TouchImageView) object));
		}
		
	}
	
	/** Determine the space between the first two fingers */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
	
	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
	
	private class IVOnTouchListner implements OnTouchListener {
		
		private ImageView view;
		
		IVOnTouchListner(ImageView view) {
			this.view = view;
		}
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			
			ImageView view = (ImageView) v;
			view.setScaleType(ScaleType.MATRIX);
			
			// Handle touch events here...
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());
					Log.d(TAG, "mode=DRAG");
					mode = DRAG;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);
					Log.d(TAG, "oldDist=" + oldDist);
					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(mid, event);
						mode = ZOOM;
						Log.d(TAG, "mode=ZOOM");
					}
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					Log.d(TAG, "mode=NONE");
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						// // ...
						matrix.set(savedMatrix);
						matrix.postTranslate(event.getX() - start.x,
								event.getY() - start.y);
					}
					else if (mode == ZOOM) {
						float newDist = spacing(event);
						Log.d(TAG, "newDist=" + newDist);
						if (newDist > 10f) {
							matrix.set(savedMatrix);
							float scale = newDist / oldDist;
							matrix.postScale(scale, scale, mid.x, mid.y);
						}
					}
					break;
			}
			
			view.setImageMatrix(matrix);
			return true;
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
		
		Log.i(TAG, "Resumed ");
		
		// Bitmap bm = BitmapFactory.decodeFile(imagePath);
		
		Log.i(TAG, "Image path is " + imagePath);
		
		// // imageView.setImageBitmap(bm);
		// if (imagePath != null) {
		// imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath), null,
		// -1, UIConfiguration.IMAGE_VIEW_MAX_ZOOM);
		// }
		
		imageGallary.setAdapter(new imageAdapter());
		
		imageGallary.setCurrentItem(currentPos, false);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		imageView.setImageBitmap(null);
		finish();
		System.gc();
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == EDIT_IMAGE) {
				// output image path
				Uri mImageUri = data.getData();
				Log.i(TAG, "uri is " + mImageUri.toString());
				Bundle extra = data.getExtras();
				if (null != extra) {
					boolean changed = extra
							.getBoolean(Constants.EXTRA_OUT_BITMAP_CHANGED);
					if (!changed) {
						if (copiedFile != null && copiedFile.exists()) {
							copiedFile.delete();
						}
					}
					
				}
			}
		}
		else if (resultCode == RESULT_CANCELED) {
			if (copiedFile != null && copiedFile.exists()) {
				copiedFile.delete();
			}
		}
	}
}
