package com.remedeo.remedeoapp.adpater;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.remedeo.remedeoapp.R;

public class RearrangeAdapter extends BaseAdapter {
	public static int myflag = 0;
	private Activity mContext;
	private List<String> itemList;
	private List<String> newList;
	private LayoutInflater li;
	private ImageLoader imageLoader;
	private static final String VIDEO_PATH = "VIDEO_PATH";
	private static final String TAG = "ImageAdapter";

	public RearrangeAdapter(Activity c, List<String> filePathList,
			List<String> newList, ImageLoader imageLoader) {
		mContext = c;
		itemList = filePathList;
		this.newList = newList;
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

		final TextView sequenceNumber = (TextView) convertView
				.findViewById(R.id.sequenceNumber);
		final RelativeLayout layout = (RelativeLayout) convertView
				.findViewById(R.id.gridview_item_bg_layout);
		final ImageView imageView = (ImageView) convertView
				.findViewById(R.id.thumbImage);
		ImageView playImageView = (ImageView) convertView
				.findViewById(R.id.video_imageview);
		TextView dateTextView = (TextView) convertView
				.findViewById(R.id.grid_date_textview);

	

		final String fileName = itemList.get(position);

		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.v("pos","Position is ="+position);
				
				if (!newList.contains(fileName)) {
					sequenceNumber.setVisibility(View.VISIBLE);
					newList.add(fileName);
					sequenceNumber.setText(String.valueOf(newList.size()));
				}
			
			}
		});

		File file = new File(fileName);
		if (file != null && file.exists()) {
			dateTextView.setText(new SimpleDateFormat("d/MM/yy").format(file
					.lastModified()));
		}

		if (fileName.endsWith(".JPEG")) {

			// imageView.setRotation(-90f);

			playImageView.setVisibility(View.GONE);
			
			imageLoader.displayImage("file://" + fileName, imageView);
//			imageLoader.loadImage("file://" + fileName,
//					new SimpleImageLoadingListener() {
//						@Override
//						public void onLoadingComplete(String imageUri,
//								View view, Bitmap loadedImage) {
//							Matrix mat = new Matrix();
//							mat.postRotate(-90);
//							/*Bitmap bm = Bitmap.createScaledBitmap(loadedImage,
//									dpToPx(mContext, 250),
//									dpToPx(mContext, 190), true);*/
//							imageView.setImageBitmap(Bitmap.createBitmap(loadedImage, 0,
//									0, loadedImage.getWidth(), loadedImage.getHeight(), mat, true));
//
//							// imageView.setImageBitmap(bm);
//						}
//					});

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


	private int calculateInSampleSize(

	BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
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
