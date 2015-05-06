package com.aviary.android.feather.sdk.panels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.aviary.android.feather.headless.moa.MoaAction;
import com.aviary.android.feather.headless.moa.MoaActionFactory;
import com.aviary.android.feather.headless.moa.MoaActionList;
import com.aviary.android.feather.headless.moa.MoaGraphicsCommandParameter;
import com.aviary.android.feather.headless.moa.MoaGraphicsOperationParameter;
import com.aviary.android.feather.headless.moa.MoaPointParameter;
import com.aviary.android.feather.library.content.ToolEntry;
import com.aviary.android.feather.library.filters.ToolLoaderFactory;
import com.aviary.android.feather.library.services.ConfigService;
import com.aviary.android.feather.library.services.IAviaryController;
import com.aviary.android.feather.library.utils.BitmapUtils;
import com.aviary.android.feather.library.utils.UIConfiguration;
import com.aviary.android.feather.library.vo.ToolActionVO;
import com.aviary.android.feather.sdk.R;
import com.aviary.android.feather.sdk.graphics.PreviewFillColorDrawable;
import com.aviary.android.feather.sdk.graphics.PreviewSpotDrawable;
import com.aviary.android.feather.sdk.utils.UIUtils;
import com.aviary.android.feather.sdk.widget.AviaryAdapterView;
import com.aviary.android.feather.sdk.widget.AviaryGallery;
import com.aviary.android.feather.sdk.widget.AviaryGallery.OnItemsScrollListener;
import com.aviary.android.feather.sdk.widget.AviaryHighlightImageButton;
import com.aviary.android.feather.sdk.widget.ImageViewTouchAndDraw;
import com.aviary.android.feather.sdk.widget.ImageViewTouchAndDraw.OnDrawPathListener;
import com.aviary.android.feather.sdk.widget.ImageViewTouchAndDraw.OnDrawStartListener;
import com.aviary.android.feather.sdk.widget.ImageViewTouchAndDraw.TouchMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;

public class DrawingPanel extends AbstractContentPanel implements OnDrawStartListener, OnDrawPathListener, OnClickListener {
    private enum DrawinTool {
        Draw, Erase, Zoom,
    }

    protected AviaryHighlightImageButton mLensButton;
    protected AviaryGallery              mGallerySize;
    protected AviaryGallery              mGalleryColor;
    protected int mSelectedColorPosition, mSelectedSizePosition = 0;
    int[] mBrushSizes;
    int[] mBrushColors;
    private int mColor = 0;
    private int mSize  = 10;
    private int mBlur  = 1;
    private Paint         mPaint;
    private ConfigService mConfig;
    private DrawinTool    mSelectedTool;
    // width and height of the bitmap
    int mWidth, mHeight;
    MoaAction                                 mAction;
    MoaActionList                             mActionList;
    ToolActionVO<Integer>                     mToolAction;
    Collection<MoaGraphicsOperationParameter> mOperations;
    MoaGraphicsOperationParameter             mCurrentOperation;
    PreviewFillColorDrawable                  mDrawable;
    PreviewSpotDrawable                       mEraserDrawable;
    Toast                                     mToast;
    private float minRadiusSize;
    private float maxRadiusSize;
    private int   mBrushSizeIndex;
    private int   mBrushColorIndex;
    private int   minBrushSize;
    private int   maxBrushSize;
    String mEraserContentDescription;
    String mSizeContentDescription;
    String mColorContentDescription;

    public DrawingPanel(IAviaryController context, ToolEntry entry) {
        super(context, entry);
    }

    @Override
    public void onCreate(Bitmap bitmap, Bundle options) {
        super.onCreate(bitmap, options);

        mConfig = getContext().getService(ConfigService.class);

        minRadiusSize = (float) mConfig.getInteger(R.integer.aviary_spot_gallery_item_min_size) / 100;
        maxRadiusSize = (float) mConfig.getInteger(R.integer.aviary_spot_gallery_item_max_size) / 100;

        mEraserContentDescription = mConfig.getString(R.string.feather_colorsplash_eraser);
        mColorContentDescription = mConfig.getString(R.string.feather_acc_color);
        mSizeContentDescription = mConfig.getString(R.string.feather_acc_size);

        mBrushSizes = mConfig.getIntArray(R.array.aviary_draw_brush_sizes);
        mBrushSizeIndex = mConfig.getInteger(R.integer.aviary_draw_brush_index);

        mBrushColors = mConfig.getIntArray(R.array.aviary_draw_fill_colors);
        mBrushColorIndex = mConfig.getInteger(R.integer.aviary_draw_fill_color_index);

        minBrushSize = mBrushSizes[0];
        maxBrushSize = mBrushSizes[mBrushSizes.length - 1];

        mBlur = mConfig.getInteger(R.integer.aviary_draw_brush_softValue);

        mColor = mBrushColors[mBrushColorIndex];
        mSize = mBrushSizes[mBrushSizeIndex];

        mLensButton = (AviaryHighlightImageButton) getContentView().findViewById(R.id.aviary_lens_button);

        mGallerySize = (AviaryGallery) getOptionView().findViewById(R.id.aviary_gallery);
        mGalleryColor = (AviaryGallery) getOptionView().findViewById(R.id.aviary_gallery2);

        mImageView = (ImageViewTouchAndDraw) getContentView().findViewById(R.id.image);
        mImageView.setDisplayType(DisplayType.FIT_IF_BIGGER);

        mToast = makeToast();

        mWidth = mBitmap.getWidth();
        mHeight = mBitmap.getHeight();

        resetBitmap();

        mSelectedColorPosition = 1;
        mSelectedSizePosition = 0;

        mOperations = new ArrayList<MoaGraphicsOperationParameter>();
        mCurrentOperation = null;

        mActionList = MoaActionFactory.actionList("draw");
        mToolAction = new ToolActionVO<Integer>(0);

        mAction = mActionList.get(0);
        mAction.setValue("previewSize", new MoaPointParameter(mWidth, mHeight));
        mAction.setValue("commands", mOperations);

        setupFill();
        setupSize();

        mPaint = initPaint(((ImageViewTouchAndDraw) mImageView).getPaint());
        ((ImageViewTouchAndDraw) mImageView).setPaint(mPaint);
    }

    private void setupSize() {
        mGallerySize.setDefaultPosition(mBrushSizeIndex);
        mGallerySize.setCallbackDuringFling(true);
        mGallerySize.setAutoSelectChild(true);
        mGallerySize.setAdapter(new GallerySizeAdapter(getContext().getBaseContext(), mBrushSizes));
    }

    private void setupFill() {
        mGalleryColor.setDefaultPosition(mBrushColorIndex);
        mGalleryColor.setCallbackDuringFling(true);
        mGalleryColor.setAutoSelectChild(true);
        mGalleryColor.setAdapter(new GalleryColorAdapter(getContext().getBaseContext(), mBrushColors));
    }

    private void resetBitmap() {
        @SuppressWarnings ("unused") Matrix current = getContext().getCurrentImageViewMatrix();
        mImageView.setImageBitmap(mBitmap, null, -1, UIConfiguration.IMAGE_VIEW_MAX_ZOOM);
        ((ImageViewTouchAndDraw) mImageView).setDrawMode(TouchMode.DRAW);
    }

    @Override
    public void onActivate() {
        super.onActivate();

        disableHapticIsNecessary(mGalleryColor, mGallerySize);

        mGallerySize.setOnItemsScrollListener(new AviaryGallery.OnItemsScrollListener() {
            @Override
            public void onScrollFinished(AviaryAdapterView<?> parent, View view, int position, long id) {
                mSize = Math.max(4, mBrushSizes[position]);
                mPaint.setStrokeWidth(mSize * 2);

                // the user may change brush size while in zoom mode, so need to make sure we are using the right tool:
                if (mGalleryColor.getAdapter().getItemViewType(mGalleryColor.getSelectedItemPosition())
                    == GalleryColorAdapter.ERASER_POSITION) {
                    setSelectedTool(DrawinTool.Erase);
                } else {
                    setSelectedTool(DrawinTool.Draw);
                }
            }

            @Override
            public void onScrollStarted(AviaryAdapterView<?> parent, View view, int position, long id) {
                if (getSelectedTool() == DrawinTool.Zoom) {
                    setSelectedTool(DrawinTool.Draw);
                }
            }

            @Override
            public void onScroll(AviaryAdapterView<?> parent, View view, int position, long id) {
                mLogger.log("onScroll: " + position);
                updateToast();
            }
        });

        mGalleryColor.setOnItemsScrollListener(new OnItemsScrollListener() {
            @Override
            public void onScrollFinished(AviaryAdapterView<?> parent, View view, int position, long id) {
                mColor = mBrushColors[position];
                mPaint.setColor(mColor);

                final boolean isEraser = mColor == 0;

                if (getSelectedTool() == DrawinTool.Zoom) {
                    if (isEraser) {
                        setSelectedTool(DrawinTool.Erase);
                    } else {
                        setSelectedTool(DrawinTool.Draw);
                    }
                } else {
                    if (isEraser && getSelectedTool() != DrawinTool.Erase) {
                        setSelectedTool(DrawinTool.Erase);
                    } else if (!isEraser && getSelectedTool() != DrawinTool.Draw) {
                        setSelectedTool(DrawinTool.Draw);
                    }
                }
            }

            @Override
            public void onScrollStarted(AviaryAdapterView<?> parent, View view, int position, long id) {
                if (getSelectedTool() == DrawinTool.Zoom) {
                    setSelectedTool(DrawinTool.Draw);
                }
            }

            @Override
            public void onScroll(AviaryAdapterView<?> parent, View view, int position, long id) {
                mLogger.log("onScroll: " + position);
                updateToast();
            }
        });

        mLensButton.setOnClickListener(this);

        setSelectedTool(DrawinTool.Draw);

        ((ImageViewTouchAndDraw) mImageView).setOnDrawStartListener(this);
        ((ImageViewTouchAndDraw) mImageView).setOnDrawPathListener(this);

        getContentView().setVisibility(View.VISIBLE);
        contentReady();
    }

    @Override
    public void onDeactivate() {
        ((ImageViewTouchAndDraw) mImageView).setOnDrawStartListener(null);
        ((ImageViewTouchAndDraw) mImageView).setOnDrawPathListener(null);
        mLensButton.setOnClickListener(null);

        mGalleryColor.setOnItemsScrollListener(null);
        mGallerySize.setOnItemsScrollListener(null);

        if (null != mToast) {
            mToast.cancel();
        }
        super.onDeactivate();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        if (id == mLensButton.getId()) {
            boolean selected = v.isSelected();
            v.setSelected(!selected);

            mLogger.info("isSelected: " + v.isSelected());
            mLogger.info("selectedToolPos: " + mGalleryColor.getSelectedItemPosition());

            if (v.isSelected()) {
                setSelectedTool(DrawinTool.Zoom);
            } else {
                if (mGalleryColor.getSelectedItemPosition() == 0) {
                    setSelectedTool(DrawinTool.Erase);
                } else {
                    setSelectedTool(DrawinTool.Draw);
                }
            }
        }
    }

    private Toast makeToast() {
        mDrawable = new PreviewFillColorDrawable(getContext().getBaseContext());
        mEraserDrawable = new PreviewSpotDrawable(this.getContext().getBaseContext());
        Toast t = UIUtils.makeCustomToast(getContext().getBaseContext());
        ImageView image = (ImageView) t.getView().findViewById(R.id.image);
        image.setImageDrawable(mDrawable);
        return t;
    }

    private void updateToast() {
        int colorPos = mGalleryColor.getSelectedItemPosition();
        int sizePos = mGallerySize.getSelectedItemPosition();

        if (null != mToast && colorPos > -1 && sizePos > -1) {
            int color = mBrushColors[colorPos];
            int size = mBrushSizes[sizePos];

            ImageView image = (ImageView) mToast.getView().findViewById(R.id.image);

            if (color != 0) {
                mDrawable.setFixedRadius(size);
                mDrawable.setColor(color);
                image.setImageDrawable(mDrawable);
            } else {
                mEraserDrawable.setFixedRadius(size);
                image.setImageDrawable(mEraserDrawable);
            }
            mToast.show();
        }
    }

    private void setSelectedTool(DrawinTool which) {

        switch (which) {
            case Draw:
                ((ImageViewTouchAndDraw) mImageView).setDrawMode(TouchMode.DRAW);
                mPaint.setAlpha(255);
                mPaint.setXfermode(null);
                break;

            case Erase:
                ((ImageViewTouchAndDraw) mImageView).setDrawMode(TouchMode.DRAW);
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                mPaint.setAlpha(0);

                getContext().getTracker()
                    .tagEvent(ToolLoaderFactory.Tools.DRAW.name().toLowerCase(Locale.US) + ": eraser_selected");

                break;

            case Zoom:
                ((ImageViewTouchAndDraw) mImageView).setDrawMode(TouchMode.IMAGE);
                break;

            default:
                break;
        }

        mLensButton.setSelected(which == DrawinTool.Zoom);
        setPanelEnabled(which != DrawinTool.Zoom);
        mSelectedTool = which;
    }

    public void setPanelEnabled(boolean value) {
        if (!isActive()) {
            return;
        }

        if (value) {
            getContext().restoreToolbarTitle();
        } else {
            getContext().setToolbarTitle(R.string.feather_zoom_mode);
        }
        mOptionView.findViewById(R.id.aviary_disable_status).setVisibility(value ? View.INVISIBLE : View.VISIBLE);
    }

    private DrawinTool getSelectedTool() {
        return mSelectedTool;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageView.clear();
    }

    private Paint initPaint(Paint original) {
        original.setDither(true);
        original.setAntiAlias(true);
        original.setFilterBitmap(false);
        original.setColor(mColor);
        original.setStrokeWidth(mSize * 2);
        original.setStyle(Paint.Style.STROKE);
        original.setStrokeJoin(Paint.Join.ROUND);
        original.setStrokeCap(Paint.Cap.ROUND);
        original.setMaskFilter(new BlurMaskFilter(mBlur, Blur.NORMAL));
        return original;
    }

    @SuppressLint ("InflateParams")
    @Override
    protected View generateContentView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.aviary_content_draw, null);
    }

    @Override
    protected ViewGroup generateOptionView(LayoutInflater inflater, ViewGroup parent) {
        return (ViewGroup) inflater.inflate(R.layout.aviary_panel_draw, parent, false);
    }

    @Override
    protected void onGenerateResult() {
        Bitmap bitmap = null;

        if (!mBitmap.isMutable()) {
            bitmap = BitmapUtils.copy(mBitmap, mBitmap.getConfig());
        } else {
            bitmap = mBitmap;
        }

        Canvas canvas = new Canvas(bitmap);
        ((ImageViewTouchAndDraw) mImageView).commit(canvas);
        ((ImageViewTouchAndDraw) mImageView).setImageBitmap(bitmap, mImageView.getDisplayMatrix(), -1, -1);

        mEditResult.setToolAction(mToolAction);
        mEditResult.setActionList(mActionList);

        onComplete(bitmap);
    }

    @Override
    public void onDrawStart() {
        setIsChanged(true);
    }

    @Override
    public void onStart() {
        mLogger.info("onStart");
        final float scale = ((ImageViewTouchAndDraw) mImageView).getDrawingScale();
        mCurrentOperation =
            new MoaGraphicsOperationParameter(mBlur, mSize * 2 * scale, mColor, getSelectedTool() == DrawinTool.Erase ? 1 : 0);
    }

    @Override
    public void onMoveTo(float x, float y) {
        if (null != mCurrentOperation) {
            mCurrentOperation.addCommand(new MoaGraphicsCommandParameter(MoaGraphicsCommandParameter.COMMAND_MOVETO, x, y));
        }
    }

    @Override
    public void onLineTo(float x, float y) {
        if (null != mCurrentOperation) {
            mCurrentOperation.addCommand(new MoaGraphicsCommandParameter(MoaGraphicsCommandParameter.COMMAND_LINETO, x, y));
        }
    }

    @Override
    public void onQuadTo(float x, float y, float x1, float y1) {
        if (null != mCurrentOperation) {
            mCurrentOperation.addCommand(new MoaGraphicsCommandParameter(MoaGraphicsCommandParameter.COMMAND_QUADTO, x, y, x1, y1));
        }
    }

    @Override
    public void onEnd() {
        mLogger.info("onEnd");
        if (null != mCurrentOperation) {
            mOperations.add(mCurrentOperation);
        }
    }

    class GallerySizeAdapter extends BaseAdapter {
        private static final int   VALID_POSITION   = 0;
        private static final int   INVALID_POSITION = 1;
        public static final  float BRUSH_SIZE_RATIO = 0.55f;
        private int[] sizes;
        LayoutInflater mLayoutInflater;
        Resources      mRes;

        public GallerySizeAdapter(Context context, int[] values) {
            mLayoutInflater = LayoutInflater.from(context);
            sizes = values;
            mRes = context.getResources();
        }

        @Override
        public int getCount() {
            return sizes.length;
        }

        @Override
        public Object getItem(int position) {
            return sizes[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            final boolean valid = position >= 0 && position < getCount();
            return valid ? VALID_POSITION : INVALID_POSITION;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final int type = getItemViewType(position);

            PreviewSpotDrawable drawable = null;
            int size = 1;

            if (convertView == null) {

                convertView = mLayoutInflater.inflate(R.layout.aviary_gallery_item_view, parent, false);

                if (type == VALID_POSITION) {
                    drawable = new PreviewSpotDrawable(getContext().getBaseContext());
                    ImageView image = (ImageView) convertView.findViewById(R.id.image);
                    image.setImageDrawable(drawable);
                    convertView.setTag(drawable);
                }
            } else {
                if (type == VALID_POSITION) {
                    drawable = (PreviewSpotDrawable) convertView.getTag();
                }
            }

            if (drawable != null && type == VALID_POSITION) {
                size = sizes[position];
                // float value = (float) size / biggest;

                float value = minRadiusSize + (
                    (((float) size - minBrushSize) / (maxBrushSize - minBrushSize) * (maxRadiusSize - minRadiusSize))
                        * BRUSH_SIZE_RATIO);

                try {
                    convertView.setContentDescription(mSizeContentDescription + " " + Float.toString(value));
                } catch (Exception e) {
                }

                drawable.setRadius(value);
            }

            convertView.setId(position);
            return convertView;
        }
    }

    class GalleryColorAdapter extends BaseAdapter {
        public static final int VALID_POSITION   = 0;
        public static final int INVALID_POSITION = 1;
        public static final int ERASER_POSITION  = 2;
        private int[] sizes;
        LayoutInflater mLayoutInflater;
        Resources      mRes;

        public GalleryColorAdapter(Context context, int[] values) {
            mLayoutInflater = LayoutInflater.from(context);
            sizes = values;
            mRes = context.getResources();
        }

        @Override
        public int getCount() {
            return sizes.length;
        }

        @Override
        public Object getItem(int position) {
            return sizes[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            final boolean valid = position >= 0 && position < getCount();

            if (!valid) {
                return INVALID_POSITION;
            } else {
                final Integer color = (Integer) getItem(position);
                if (color == 0) {
                    return ERASER_POSITION;
                } else {
                    return VALID_POSITION;
                }
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final int type = getItemViewType(position);

            PreviewFillColorDrawable drawable = null;
            int color = 0;

            if (convertView == null) {

                if (type == ERASER_POSITION) {
                    convertView = mLayoutInflater.inflate(R.layout.aviary_gallery_item_highlight_view, parent, false);
                    convertView.setContentDescription(mEraserContentDescription);
                } else {
                    convertView = mLayoutInflater.inflate(R.layout.aviary_gallery_item_view, parent, false);
                }

                if (type == VALID_POSITION) {
                    drawable = new PreviewFillColorDrawable(getContext().getBaseContext());
                    ImageView image = (ImageView) convertView.findViewById(R.id.image);
                    image.setImageDrawable(drawable);
                    convertView.setTag(drawable);
                }

            } else {
                if (type == VALID_POSITION) {
                    drawable = (PreviewFillColorDrawable) convertView.getTag();
                }
            }

            if (drawable != null && type == VALID_POSITION) {
                color = sizes[position];
                drawable.setColor(color);
                try {
                    convertView.setContentDescription(mColorContentDescription + " " + Integer.toHexString(color));
                } catch (Exception e) {
                }
            }

            convertView.setId(position);
            return convertView;
        }
    }
}
