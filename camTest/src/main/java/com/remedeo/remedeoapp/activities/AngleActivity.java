package com.remedeo.remedeoapp.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.remedeo.remedeoapp.R;
import com.remedeo.remedeoapp.utils.Utils;

public class AngleActivity extends Activity {
	
	Paint mPaint;
	float Mx1, My1;
	float x, y;
	float startX = 0;
	float startY = 0;
	private RelativeLayout baseLayout;
	
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;
	
	private RelativeLayout cEndPointLayout;
	private RelativeLayout l1EndPointLayout;
	private RelativeLayout l2EndPointLayout;
	private LayoutParams la;
	private static int lineCount = 0;
	
	private PointF centerPoint = new PointF();
	private List<PointF> endPoints = new ArrayList<PointF>();
	
	private String imagePath;
	
	private static final String IMAGEPATH = "IMAGEPATH";
	
	private ImageView mainImageView;
	private TextView angelTextView;
	
	private int l1EndXPos;
	private int l1EndYPos;
	
	private int cEndXPos;
	private int cEndYPos;
	
	private int l2EndXPos;
	private int l2EndYPos;
	
	private int paramsHeightnWidth;
	
	private ImageView l1XPlusImageView;
	private ImageView l1XMinusImageView;
	private ImageView l1YPlusImageView;
	private ImageView l1YMinusImageView;
	
	private ImageView cXPlusImageView;
	private ImageView cXMinusImageView;
	private ImageView cYPlusImageView;
	private ImageView cYMinusImageView;
	
	private ImageView l2XPlusImageView;
	private ImageView l2XMinusImageView;
	private ImageView l2YPlusImageView;
	private ImageView l2YMinusImageView;
	
	private RelativeLayout cScaleLayout;
	private RelativeLayout l1ScaleLayout;
	private RelativeLayout l2ScaleLayout;
	
	private LayoutInflater inflater;
	
	private Drawable orangeButtonBackground;
	
	private ImageLoader imageLoader;
	
	private MyView view1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_angle);
		
		paramsHeightnWidth = Utils.dpToPx(getApplicationContext(), 100);
		
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		orangeButtonBackground = getResources().getDrawable(
				R.drawable.button_selector);
		
		cEndPointLayout = (RelativeLayout) inflater.inflate(
				R.layout.endpoint_layout, null);
		l1EndPointLayout = (RelativeLayout) inflater.inflate(
				R.layout.endpoint_layout, null);
		l2EndPointLayout = (RelativeLayout) inflater.inflate(
				R.layout.endpoint_layout, null);
		// la = new LayoutParams(20, 20);
		
		// end point edit layout
		l1XMinusImageView = (ImageView) l1EndPointLayout
				.findViewById(R.id.x_minus_imageview);
		l1XPlusImageView = (ImageView) l1EndPointLayout
				.findViewById(R.id.x_plus_imageview);
		l1YPlusImageView = (ImageView) l1EndPointLayout
				.findViewById(R.id.y_plus_imageview);
		l1YMinusImageView = (ImageView) l1EndPointLayout
				.findViewById(R.id.y_minus_imageview);
		l1ScaleLayout = (RelativeLayout) l1EndPointLayout
				.findViewById(R.id.scale_layout);
		
		l2XMinusImageView = (ImageView) l2EndPointLayout
				.findViewById(R.id.x_minus_imageview);
		l2XPlusImageView = (ImageView) l2EndPointLayout
				.findViewById(R.id.x_plus_imageview);
		l2YPlusImageView = (ImageView) l2EndPointLayout
				.findViewById(R.id.y_plus_imageview);
		l2YMinusImageView = (ImageView) l2EndPointLayout
				.findViewById(R.id.y_minus_imageview);
		l2ScaleLayout = (RelativeLayout) l2EndPointLayout
				.findViewById(R.id.scale_layout);
		
		cXMinusImageView = (ImageView) cEndPointLayout
				.findViewById(R.id.x_minus_imageview);
		cXPlusImageView = (ImageView) cEndPointLayout
				.findViewById(R.id.x_plus_imageview);
		cYPlusImageView = (ImageView) cEndPointLayout
				.findViewById(R.id.y_plus_imageview);
		cYMinusImageView = (ImageView) cEndPointLayout
				.findViewById(R.id.y_minus_imageview);
		cScaleLayout = (RelativeLayout) cEndPointLayout
				.findViewById(R.id.scale_layout);
		
		lineCount = 0;
		
		baseLayout = (RelativeLayout) findViewById(R.id.angle_base_layout);
		mainImageView = (ImageView) findViewById(R.id.angle_imageview);
		angelTextView = (TextView) findViewById(R.id.angle_textview);
		
		imagePath = getIntent().getStringExtra(IMAGEPATH);
		
		
		view1 = new MyView(this);
		view1.setBackgroundColor(Color.TRANSPARENT);
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		// mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(5);
		
		view1.setLayoutParams(new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		baseLayout.addView(view1);
		
		Button calculateAngleButton = new Button(this);
		calculateAngleButton.setText("Calculate Angle");
		calculateAngleButton.setBackground(orangeButtonBackground);
		calculateAngleButton.setLayoutParams(new LayoutParams(Utils.dpToPx(
				getApplicationContext(), 170), Utils.dpToPx(
				getApplicationContext(), 50)));
		
		calculateAngleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				double angle = calculateAngle(centerPoint, endPoints);
				Log.i("ANGLE", "angle is " + angle);
				Toast.makeText(getApplicationContext(),
						"Angle is " + Math.abs(angle), Toast.LENGTH_SHORT)
						.show();
			}
		});
		
		baseLayout.addView(calculateAngleButton);
		
		Button clearButton = new Button(this);
		clearButton.setText("Clear");
		clearButton.setBackground(orangeButtonBackground);
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				Utils.dpToPx(getApplicationContext(), 80), Utils.dpToPx(
						getApplicationContext(), 50));
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
		clearButton.setLayoutParams(lp);
		
		clearButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = getIntent();
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				finish();
				startActivity(intent);
			}
		});
		
		baseLayout.addView(clearButton);
		
		baseLayout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int X = (int) event.getRawX();
				final int Y = (int) event.getRawY();
				
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) angelTextView
								.getLayoutParams();
						l1EndXPos = X - lParams.leftMargin;
						l1EndYPos = Y - lParams.topMargin;
						l1ScaleLayout.setVisibility(View.GONE);
						l2ScaleLayout.setVisibility(View.GONE);
						cScaleLayout.setVisibility(View.GONE);
						break;
					case MotionEvent.ACTION_UP:
						break;
					case MotionEvent.ACTION_POINTER_DOWN:
						break;
					case MotionEvent.ACTION_POINTER_UP:
						break;
					case MotionEvent.ACTION_MOVE:
						RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) angelTextView
								.getLayoutParams();
						layoutParams.leftMargin = X - l1EndXPos;
						layoutParams.topMargin = Y - l1EndYPos;
						layoutParams.rightMargin = -250;
						layoutParams.bottomMargin = -250;
						angelTextView.setLayoutParams(layoutParams);
						
						break;
				}
				baseLayout.invalidate();
				return true;
			}
		});
		
		cEndPointLayout.setOnTouchListener(cEndPointTouchListner);
		cEndPointLayout.setOnClickListener(cEndPointClickListner);
		
		l1EndPointLayout.setOnTouchListener(l1EndPointTouchListner);
		l1EndPointLayout.setOnClickListener(l1EndPointClickListner);
		
		l2EndPointLayout.setOnTouchListener(l2EndPointTouchListner);
		l2EndPointLayout.setOnClickListener(l2EndPointClickListner);
	}
	
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration
				.createDefault(getApplicationContext()));
		
		if (imagePath != null && !imagePath.isEmpty()) {
//			Matrix mat = new Matrix();
//			mat.postRotate(90);
//			
			//Bitmap loadedImage=BitmapFactory.decodeFile(imagePath);
			
//			mainImageView.setImageBitmap(Bitmap.createBitmap(loadedImage, 0,
//					0, loadedImage.getWidth(), loadedImage.getHeight(), mat, true));
			
			imageLoader.loadImage("file://" + imagePath,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
//							Matrix mat = new Matrix();
//							mat.postRotate(90);
//
//							imageView.setImageBitmap(Bitmap.createBitmap(
//									loadedImage, 0, 0, loadedImage.getWidth(),
//									loadedImage.getHeight(), mat, true));
							mainImageView.setImageBitmap(loadedImage);
						}
					});
			
			//mainImageView.setImageBitmap(loadedImage);
		
		}
		
	}
	private OnTouchListener cEndPointTouchListner = new OnTouchListener() {
		
		int finalX = 0;
		int finalY = 0;
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			final int X = (int) event.getRawX();
			final int Y = (int) event.getRawY();
			
			switch (event.getAction() & event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					
					RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) cEndPointLayout
							.getLayoutParams();
					cEndXPos = X - lParams.leftMargin;
					cEndYPos = Y - lParams.topMargin;
					break;
				case MotionEvent.ACTION_UP:
					
					centerPoint.x = finalX + paramsHeightnWidth / 2;
					centerPoint.y = finalY + paramsHeightnWidth / 2;
					
					break;
				case MotionEvent.ACTION_MOVE:
					mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
					
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cEndPointLayout
							.getLayoutParams();
					
					finalX = X - cEndXPos;
					finalY = Y - cEndYPos;
					
					layoutParams.leftMargin = finalX;
					layoutParams.topMargin = finalY;
					
					cEndPointLayout.setLayoutParams(layoutParams);
					
					final float newX = finalX + paramsHeightnWidth / 2;
					final float newY = finalY + paramsHeightnWidth / 2;
					
					mCanvas.drawLine(newX, newY, endPoints.get(0).x,
							endPoints.get(0).y, mPaint);
					
					if (endPoints.size() > 1) {
						mCanvas.drawLine(newX, newY, endPoints.get(1).x,
								endPoints.get(1).y, mPaint);
					}
					
					view1.invalidate();
					
					break;
			}
			baseLayout.invalidate();
			return false;
		}
	};
	
	private OnTouchListener l1EndPointTouchListner = new OnTouchListener() {
		int finalX = 0;
		int finalY = 0;
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			final int X = (int) event.getRawX();
			final int Y = (int) event.getRawY();
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			
				case MotionEvent.ACTION_DOWN:
					// mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
					// isL1EndPointMoved = false;
					
					RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) l1EndPointLayout
							.getLayoutParams();
					l1EndXPos = X - lParams.leftMargin;
					l1EndYPos = Y - lParams.topMargin;
					
					break;
				case MotionEvent.ACTION_UP:
					endPoints.get(0).x = finalX + paramsHeightnWidth / 2;
					endPoints.get(0).y = finalY + paramsHeightnWidth / 2;
					break;
				case MotionEvent.ACTION_MOVE:
					mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
					
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) l1EndPointLayout
							.getLayoutParams();
					
					finalX = X - l1EndXPos;
					finalY = Y - l1EndYPos;
					
					layoutParams.leftMargin = finalX;
					layoutParams.topMargin = finalY;
					l1EndPointLayout.setLayoutParams(layoutParams);
					
					mCanvas.drawLine(centerPoint.x, centerPoint.y, finalX
							+ paramsHeightnWidth / 2, finalY
							+ paramsHeightnWidth / 2, mPaint);
					
					if (endPoints.size() > 1) {
						mCanvas.drawLine(centerPoint.x, centerPoint.y,
								endPoints.get(1).x, endPoints.get(1).y, mPaint);
					}
					
					view1.invalidate();
					
					break;
			}
			baseLayout.invalidate();
			return false;
		}
	};
	
	private OnTouchListener l2EndPointTouchListner = new OnTouchListener() {
		int finalX = 0;
		int finalY = 0;
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			final int X = (int) event.getRawX();
			final int Y = (int) event.getRawY();
			switch (event.getAction() & event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) l2EndPointLayout
							.getLayoutParams();
					l2EndXPos = X - lParams.leftMargin;
					l2EndYPos = Y - lParams.topMargin;
					break;
				case MotionEvent.ACTION_UP:
					
					endPoints.get(1).x = finalX + paramsHeightnWidth / 2;
					endPoints.get(1).y = finalY + paramsHeightnWidth / 2;
					break;
				case MotionEvent.ACTION_MOVE:
					mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
					
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) l2EndPointLayout
							.getLayoutParams();
					
					finalX = X - l2EndXPos;
					finalY = Y - l2EndYPos;
					
					layoutParams.leftMargin = finalX;
					layoutParams.topMargin = finalY;
					l2EndPointLayout.setLayoutParams(layoutParams);
					
					mCanvas.drawLine(centerPoint.x, centerPoint.y, finalX
							+ paramsHeightnWidth / 2, finalY
							+ paramsHeightnWidth / 2, mPaint);
					
					if (endPoints.size() > 1) {
						mCanvas.drawLine(centerPoint.x, centerPoint.y,
								endPoints.get(0).x, endPoints.get(0).y, mPaint);
					}
					
					view1.invalidate();
					
					break;
			}
			baseLayout.invalidate();
			return false;
		}
	};
	
	private OnClickListener cEndPointClickListner = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int visiblity = View.GONE;
			if (cScaleLayout.getVisibility() == View.GONE) {
				visiblity = View.VISIBLE;
			}
			cScaleLayout.setVisibility(visiblity);
			l1ScaleLayout.setVisibility(View.GONE);
			l2ScaleLayout.setVisibility(View.GONE);
			if (visiblity == View.VISIBLE) {
				cXMinusImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						float scale = cEndPointLayout.getScaleX();
						if (scale > 0.5) {
							cEndPointLayout.setScaleX(scale - 0.25f);
						}
					}
				});
				cYMinusImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						float scale = cEndPointLayout.getScaleY();
						if (scale > 0.5) {
							cEndPointLayout.setScaleY(scale - 0.25f);
						}
					}
				});
				cXPlusImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						cEndPointLayout.setScaleX(cEndPointLayout.getScaleX() + 0.25f);
					}
				});
				cYPlusImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						cEndPointLayout.setScaleY(cEndPointLayout.getScaleY() + 0.25f);
					}
				});
			}
		}
	};
	
	private OnClickListener l1EndPointClickListner = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int visiblity = View.GONE;
			if (l1ScaleLayout.getVisibility() == View.GONE) {
				visiblity = View.VISIBLE;
			}
			l1ScaleLayout.setVisibility(visiblity);
			cScaleLayout.setVisibility(View.GONE);
			l2ScaleLayout.setVisibility(View.GONE);
			if (visiblity == View.VISIBLE) {
				l1XMinusImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						float scale = l1EndPointLayout.getScaleX();
						if (scale > 0.5) {
							l1EndPointLayout.setScaleX(scale - 0.25f);
						}
					}
				});
				l1YMinusImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						float scale = l1EndPointLayout.getScaleY();
						if (scale > 0.5) {
							l1EndPointLayout.setScaleY(scale - 0.25f);
						}
					}
				});
				l1XPlusImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						l1EndPointLayout.setScaleX(l1EndPointLayout.getScaleX() + 0.25f);
					}
				});
				l1YPlusImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						l1EndPointLayout.setScaleY(l1EndPointLayout.getScaleY() + 0.25f);
					}
				});
			}
		}
	};
	
	private OnClickListener l2EndPointClickListner = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int visiblity = View.GONE;
			if (l2ScaleLayout.getVisibility() == View.GONE) {
				visiblity = View.VISIBLE;
			}
			
			l2ScaleLayout.setVisibility(visiblity);
			l1ScaleLayout.setVisibility(View.GONE);
			cScaleLayout.setVisibility(View.GONE);
			if (visiblity == View.VISIBLE) {
				l2XMinusImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						float scale = l2EndPointLayout.getScaleX();
						if (scale > 0.5) {
							l2EndPointLayout.setScaleX(scale - 0.25f);
						}
					}
				});
				l2YMinusImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						float scale = l2EndPointLayout.getScaleY();
						if (scale > 0.5) {
							l2EndPointLayout.setScaleY(scale - 0.25f);
						}
					}
				});
				l2XPlusImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						l2EndPointLayout.setScaleX(l2EndPointLayout.getScaleX() + 0.25f);
					}
				});
				l2YPlusImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						l2EndPointLayout.setScaleY(l2EndPointLayout.getScaleY() + 0.25f);
					}
				});
			}
			
		}
	};
	
	public class MyView extends View {
		
		public MyView(Context c) {
			super(c);
			
			mPath = new Path();
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		}
		
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			// mBitmap =
			// Bitmap.createBitmap(BitmapFactory.decodeFile(imagePath),
			// 0, 0, w, h);
			mCanvas = new Canvas(mBitmap);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			// canvas.drawColor(0, Mode.OVERLAY);
			// canvas.drawColor(Color.WHITE);
			// canvas.drawLine(mX, mY, Mx1, My1, mPaint);
			// canvas.drawLine(mX, mY, x, y, mPaint);
			canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
			// canvas.drawPath(mPath, mPaint);
			
		}
		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// touch_start(x, y);
					if (startX == 0 && startY == 0) {
						startX = event.getX();
						startY = event.getY();
						
						la = new LayoutParams(paramsHeightnWidth,
								paramsHeightnWidth);
						la.setMargins((int) startX - paramsHeightnWidth / 2,
								(int) startY - paramsHeightnWidth / 2, 0, 0);
						cEndPointLayout.setLayoutParams(la);
						// centerLayout.setBackground(redRoundDrawable);
						baseLayout.addView(cEndPointLayout);
						
						centerPoint.x = startX;
						centerPoint.y = startY;
						
					}
					if (l1ScaleLayout.getVisibility() == View.VISIBLE
							|| cScaleLayout.getVisibility() == View.VISIBLE
							|| l2ScaleLayout.getVisibility() == View.VISIBLE) {
						l1ScaleLayout.setVisibility(View.GONE);
						l2ScaleLayout.setVisibility(View.GONE);
						cScaleLayout.setVisibility(View.GONE);
					}
					break;
				case MotionEvent.ACTION_MOVE:
					// touch_move(x, y);
					// mCanvas.drawColor(0, Mode.CLEAR);
					if (lineCount < 2) {
						mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
						// mCanvas.drawPath(mPath, mPaint);
						mCanvas.drawLine(centerPoint.x, centerPoint.y,
								event.getX(), event.getY(), mPaint);
					}
					if (lineCount == 1) {
						mCanvas.drawLine(centerPoint.x, centerPoint.y,
								endPoints.get(0).x, endPoints.get(0).y, mPaint);
					}
					invalidate();
					
					break;
				case MotionEvent.ACTION_UP:
					if (lineCount < 2) {
						mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
						mCanvas.drawLine(centerPoint.x, centerPoint.y,
								event.getX(), event.getY(), mPaint);
						mPath.moveTo(startX, startY);
						mPath.lineTo(event.getX(), event.getY());
						
						PointF endPoint = new PointF(event.getX(), event.getY());
						endPoints.add(endPoint);
					}
					if (lineCount == 0) {
						la = new LayoutParams(paramsHeightnWidth,
								paramsHeightnWidth);
						la.setMargins((int) event.getX() - paramsHeightnWidth
								/ 2, (int) event.getY() - paramsHeightnWidth
								/ 2, 0, 0);
						l1EndPointLayout.setLayoutParams(la);
						// l1EndPointLayout.setBackground(redRoundDrawable);
						baseLayout.addView(l1EndPointLayout);
					}
					else if (lineCount == 1) {
						mCanvas.drawLine(centerPoint.x, centerPoint.y,
								endPoints.get(0).x, endPoints.get(0).y, mPaint);
						
						la = new LayoutParams(paramsHeightnWidth,
								paramsHeightnWidth);
						la.setMargins((int) event.getX() - paramsHeightnWidth
								/ 2, (int) event.getY() - paramsHeightnWidth
								/ 2, 0, 0);
						l2EndPointLayout.setLayoutParams(la);
						// l2EndPointLayout.setBackground(redRoundDrawable);
						baseLayout.addView(l2EndPointLayout);
						
					}
					
					lineCount++;
					invalidate();
					break;
			}
			return true;
		}
	}
	
	private double calculateAngle(PointF centerPoint, List<PointF> endPoints) {
		double angle[] = new double[2];
		
		for (int i = 0; i < endPoints.size(); i++) {
			PointF point = endPoints.get(i);
			angle[i] = Math.atan2(centerPoint.y - point.y, centerPoint.x
					- point.x);
		}
		return Math.abs(angle[0] - angle[1]) * 57.2957795;
	}
}
