package com.balloon.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.core.view.ViewCompat;

import com.balloon.R;


public class ShapesImage extends androidx.appcompat.widget.AppCompatImageView {
    private Paint mBlackPaint;
    private Paint mMaskedPaint;
    private Rect mBounds;
    private RectF mBoundsF;
    private Drawable mMaskDrawable;

    private boolean mCacheValid = false;
    private Bitmap mCacheBitmap;
    private int mCachedWidth;
    private int mCachedHeight;
    private int mImageShape;

    public static final int CUSTOM = 0;
    public static final int GUITAR_PICK = 34;


    public ShapesImage(Context context) {
        this(context, null);
    }

    public ShapesImage(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Attribute initialization
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShapesImage);
        mImageShape = a.getInteger(R.styleable.ShapesImage_shape, 0);

        if(mImageShape == CUSTOM) {
            mMaskDrawable = a.getDrawable(R.styleable.ShapesImage_shapeDrawable);
            if (mMaskDrawable != null) {
                mMaskDrawable.setCallback(this);
            }
        }

        prepareDrawables(mImageShape);
        a.recycle();
        setUpPaints();
    }

    private void prepareDrawables(int checkShape) {
        switch (checkShape)
        {
            case GUITAR_PICK:
                if(Build.VERSION.SDK_INT >= 21)
                    mMaskDrawable = getResources().getDrawable(R.drawable.abc123, null);
                else
                    mMaskDrawable = getResources().getDrawable(R.drawable.abc123);
                if (mMaskDrawable != null) {
                    mMaskDrawable.setCallback(this);
                }
                break;

        }
    }

    private void setUpPaints() {
        mBlackPaint = new Paint();
        mBlackPaint.setColor(0xff000000);
        mMaskedPaint = new Paint();
        mMaskedPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        mCacheBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        final boolean changed = super.setFrame(l, t, r, b);
        mBounds = new Rect(0, 0, r - l, b - t);
        mBoundsF = new RectF(mBounds);
        if (mMaskDrawable != null) {
            mMaskDrawable.setBounds(mBounds);
        }
        if (changed) {
            mCacheValid = false;
        }
        return changed;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBounds == null) {
            return;
        }
        int width = mBounds.width();
        int height = mBounds.height();
        if (width == 0 || height == 0) {
            return;
        }

        if (!mCacheValid || width != mCachedWidth || height != mCachedHeight) {
            // Need to redraw the cache
            if (width == mCachedWidth && height == mCachedHeight) {
                // Have a correct-sized bitmap cache already allocated. Just erase it.
                mCacheBitmap.eraseColor(0);
            } else {
                // Allocate a new bitmap with the correct dimensions.
                mCacheBitmap.recycle();
                //noinspection AndroidLintDrawAllocation
                mCacheBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                mCachedWidth = width;
                mCachedHeight = height;
            }

            Canvas cacheCanvas = new Canvas(mCacheBitmap);
            if (mMaskDrawable != null) {
                int sc = cacheCanvas.save();
                mMaskDrawable.draw(cacheCanvas);
                cacheCanvas.saveLayer(mBoundsF, mMaskedPaint,
                        Canvas.ALL_SAVE_FLAG);
                super.onDraw(cacheCanvas);
                cacheCanvas.restoreToCount(sc);
            }else {
                super.onDraw(cacheCanvas);
            }
        }
        // Draw from cache
        canvas.drawBitmap(mCacheBitmap, mBounds.left, mBounds.top, null);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mMaskDrawable != null && mMaskDrawable.isStateful()) {
            mMaskDrawable.setState(getDrawableState());
        }
        if (isDuplicateParentStateEnabled()) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        if (who == mMaskDrawable) {
            invalidate();
        } else {
            super.invalidateDrawable(who);
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mMaskDrawable || super.verifyDrawable(who);
    }

    /**
     * Sets the Drawable
     *
     * @param drawable Drawable object
     */
    public void setShapeDrawable(Drawable drawable) {
        this.mMaskDrawable = drawable;
        if (mMaskDrawable != null) {
            mMaskDrawable.setCallback(this);
        }
        setUpPaints();
    }
    /**
     * Sets the Drawable resource
     *
     * @param drawable Drawable resource
     */
    public void setShapeDrawable(int drawable) {
        if(drawable != CUSTOM) {
            mImageShape = drawable;
            prepareDrawables(mImageShape);
            setUpPaints();
        }
   }

}