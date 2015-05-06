package com.remedeo.remedeoapp.activities;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.remedeo.remedeoapp.R;

public class FullVideoActivity extends Activity {
	
	private static final String VIDEO_PATH = "VIDEO_PATH";
	private VideoView fullVideoView;
	private String videoPath;
	private ImageView  frameGrabber;
	private volatile boolean flag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_video);
		
		
		
		fullVideoView = (VideoView) findViewById(R.id.full_video);
		frameGrabber=(ImageView)findViewById(R.id.full_video_frame_grabber);
		videoPath = getIntent().getStringExtra(VIDEO_PATH);
		
		MediaController mediaController = new MediaController(this);
		mediaController.setAnchorView(fullVideoView);
		fullVideoView.setMediaController(mediaController);
		
		if (videoPath != null) {
			fullVideoView.setVideoURI(Uri.parse("file://" + videoPath));
			fullVideoView.start();
		}
		
		frameGrabber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
	}
	
//	private class Encoder extends AsyncTask<File, Integer, Integer> {
//		private static final String TAG = "ENCODER";
//
//		protected Integer doInBackground(File... params) {
//			SequenceEncoder se = null;
//			try {
//				se = new SequenceEncoder(new File(params[0].getParentFile(),
//						"jcodec_enc.mp4"));
//				for (int i = 0; !flag; i++) {
//					File img = new File(params[0].getParentFile(),
//							String.format(params[0].getName(), i));
//					if (!img.exists())
//						break;
//					Bitmap frame = BitmapFactory.decodeFile(img
//							.getAbsolutePath());
//					se.encodeNativeFrame(pic);
//
//					publishProgress(i);
//
//				}
//				se.finish();
//			} catch (IOException e) {
//				Log.e(TAG, "IO", e);
//			}
//
//			return 0;
//		}
//
//		@Override
//		protected void onProgressUpdate(Integer... values) {
//			//progress.setText(String.valueOf(values[0]));
//		}
//	}
//
//	private class Decoder extends AsyncTask<File, Integer, Integer> {
//		private static final String TAG = "DECODER";
//
//		protected Integer doInBackground(File... params) {
//			FileChannelWrapper ch = null;
//			try {
//				ch = NIOUtils.readableFileChannel(params[0]);
//				FrameGrab frameGrab = new FrameGrab(ch);
//				MediaInfo mi = frameGrab.getMediaInfo();
//				Bitmap frame = Bitmap.createBitmap(mi.getDim().getWidth(), mi
//						.getDim().getHeight(), Bitmap.Config.ARGB_8888);
//
//				for (int i = 0; !flag; i++) {
//					frameGrab.getFrame(frame);
//					if (frame == null)
//						break;
//					OutputStream os = null;
//					try {
//						os = new BufferedOutputStream(new FileOutputStream(
//								new File(params[0].getParentFile(),
//										String.format("img%08d.jpg", i))));
//						frame.compress(CompressFormat.JPEG, 90, os);
//					} finally {
//						if (os != null)
//							os.close();
//					}
//					publishProgress(i);
//
//				}
//			} catch (IOException e) {
//				Log.e(TAG, "IO", e);
//			} catch (JCodecException e) {
//				Log.e(TAG, "JCodec", e);
//			} finally {
//				NIOUtils.closeQuietly(ch);
//			}
//			return 0;
//		}
//
//		@Override
//		protected void onProgressUpdate(Integer... values) {
//			//progress.setText(String.valueOf(values[0]));
//		}
//	}

	
}
