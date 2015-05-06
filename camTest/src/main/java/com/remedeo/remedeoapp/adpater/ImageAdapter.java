package com.remedeo.remedeoapp.adpater;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.remedeo.remedeoapp.R;
import com.remedeo.remedeoapp.activities.FullImageActivity;
import com.remedeo.remedeoapp.activities.FullVideoActivity;
import com.remedeo.remedeoapp.activities.GridViewActivity;

public class ImageAdapter extends BaseAdapter {
	public static int myflag = 0;
	private Activity mContext;
	private List<String> itemList;
	private List<Integer> deleteFileList;
	private LayoutInflater li;
	private ImageLoader imageLoader;
	private static final String VIDEO_PATH = "VIDEO_PATH";
	private static final String TAG = "ImageAdapter";
	
	public ImageAdapter(Activity c, List<String> filePathList,
			List<Integer> deleteFileList, ImageLoader imageLoader) {
		mContext = c;
		itemList = filePathList;
		this.deleteFileList = deleteFileList;
		this.imageLoader = imageLoader;
		li = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() {
		return itemList.size();
	}
	
	public Object getItem(int position) {
		return position;
	}
	
	public long getItemId(int position) {
		return position;
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			convertView = li.inflate(R.layout.galleryitem, null);
		}
		
		final RelativeLayout layout = (RelativeLayout) convertView
				.findViewById(R.id.gridview_item_bg_layout);
		final ImageView imageView = (ImageView) convertView
				.findViewById(R.id.thumbImage);
		ImageView playImageView = (ImageView) convertView
				.findViewById(R.id.video_imageview);
		TextView dateTextView = (TextView) convertView
				.findViewById(R.id.grid_date_textview);
		// holder.checkbox = (CheckBox) convertView
		// .findViewById(R.id.itemCheckBox);
		
		// holder.checkbox.setId(position);
		
		imageView.setOnLongClickListener(new OnLongClickListener() {
			
			public boolean onLongClick(View v) {
				if (!deleteFileList.contains(position)) {
					deleteFileList.add(position);
					layout.setBackgroundResource(R.drawable.grid_bg_activiate);
				}
				else {
					deleteFileList.remove((Integer) position);
					layout.setBackgroundResource(R.drawable.grid_bg_deactiviate);
				}
				
				if (deleteFileList.size() > 0) {
					GridViewActivity.changeVisiblityOfButtons(true);
				}
				else {
					GridViewActivity.changeVisiblityOfButtons(false);
				}
				
				return true;
			}
		});
		
		if (deleteFileList.contains(position)) {
			layout.setBackgroundResource(R.drawable.grid_bg_activiate);
		}
		else {
			layout.setBackgroundResource(R.drawable.grid_bg_deactiviate);
		}
		
		final String fileName = itemList.get(position);
		
		File file = new File(fileName);
		if (file != null && file.exists()) {
			dateTextView.setText(new SimpleDateFormat("d/MM/yy").format(file
					.lastModified()));
		}
		
		if (fileName.endsWith(".JPEG")) {
			
			//imageView.setRotation(-90f);
			
			playImageView.setVisibility(View.GONE);
			
			imageLoader.displayImage("file://" + fileName, imageView);
//			imageLoader.loadImage("file://" + fileName,
//					new SimpleImageLoadingListener() {
//						@Override
//						public void onLoadingComplete(String imageUri,
//								View view, Bitmap loadedImage) {
//							Matrix mat = new Matrix();
//							mat.postRotate(-90);
//						/*	Bitmap bm = Bitmap.createScaledBitmap(loadedImage,
//									dpToPx(mContext, 250),
//									dpToPx(mContext, 190), true);*/
//							imageView.setImageBitmap(Bitmap.createBitmap(loadedImage, 0,
//									0, loadedImage.getWidth(), loadedImage.getHeight(), mat, true));
//							
//							//imageView.setImageBitmap(bm);
//						}
//					});
			
			imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext,
							FullImageActivity.class);
					intent.putExtra("imagepath", fileName);
					intent.putExtra("currentPos", position);
					intent.putStringArrayListExtra(
							"fileList",
							new ArrayList<String>(itemList));
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intent);
					//mContext.finish();
				}
			});
		}
		else if (fileName.endsWith(".mp4")) {
			playImageView.setVisibility(View.VISIBLE);
			
			//
			// Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(fileName,
			// MediaStore.Images.Thumbnails.MINI_KIND);
			// if (thumbnail != null) {
			// imageView.setImageBitmap(Bitmap.createScaledBitmap(
			// BitmapFactory.decodeFile(fileName),
			// dpToPx(mContext, 100), dpToPx(mContext, 100), true));
			// }
			
			new VideoImageAsyncTask(imageView).execute(fileName);
			
			imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				
					
					
					Intent intent = new Intent(mContext,
							FullVideoActivity.class);
					intent.putExtra(VIDEO_PATH, fileName);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intent);
				}
			});
		}
		
		return convertView;
		
	}
	
	/**
	 * Method for converting dp to pixels
	 * 
	 * @param context
	 * @param dp
	 * @return
	 */
	private static int dpToPx(Context context, int dp) {
		float density = context.getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}
	
	private Bitmap decodeSampledBitmapFromUri(Bitmap bm, int reqWidth,
			int reqHeight) {
		
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		
		return bm;
	}
	
	private int calculateInSampleSize(
	
	BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		
		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			}
			else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		
		return inSampleSize;
	}
	
	private class VideoImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
		private ImageView videoImageView;
		
		VideoImageAsyncTask(ImageView videoImageView) {
			this.videoImageView = videoImageView;
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bm = ThumbnailUtils.createVideoThumbnail(params[0],
					MediaStore.Images.Thumbnails.MINI_KIND);
			return bm;
		}
		
		@Override
		public void onPostExecute(Bitmap bmp) {
			if (bmp != null) {
				Matrix mat = new Matrix();
				mat.postRotate(-90);
				Bitmap bm = Bitmap.createScaledBitmap(bmp,
						dpToPx(mContext, 120), dpToPx(mContext, 90), true);
				videoImageView.setImageBitmap(Bitmap.createBitmap(bm, 0, 0,
						bm.getWidth(), bm.getHeight(), mat, true));
			}
		}
	}
	
	private Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels) {
		Bitmap result = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		
		int color = 0xff424242;
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		int roundPx = pixels;
		
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		
		return result;
	}
}
