package com.tilak.noteshare;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class RoundImage extends Drawable  {
	private final Bitmap mBitmap;
	private final Paint mPaint;
	private final RectF mRectF;
	private final int mBitmapWidth;
	private final int mBitmapHeight;

	public RoundImage(Bitmap bitmap) 
	{
		mBitmap = bitmap;
		mRectF = new RectF();
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		final BitmapShader shader = new BitmapShader(bitmap,
				Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		mPaint.setShader(shader);

		mBitmapWidth = mBitmap.getWidth();
		mBitmapHeight = mBitmap.getHeight();
	}

	public void draw(Canvas canvas) {
		canvas.drawOval(mRectF, mPaint);
	}

	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		mRectF.set(bounds);
	}

	@Override
	public void setAlpha(int alpha) {
		if (mPaint.getAlpha() != alpha) {
			mPaint.setAlpha(alpha);
			invalidateSelf();
		}
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mPaint.setColorFilter(cf);
	}

	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	public int getIntrinsicWidth() {
		return mBitmapWidth;
	}

	public int getIntrinsicHeight() {
		return mBitmapHeight;
	}

	public void setAntiAlias(boolean aa) {
		mPaint.setAntiAlias(aa);
		invalidateSelf();
	}

	public void setFilterBitmap(boolean filter) {
		mPaint.setFilterBitmap(filter);
		invalidateSelf();
	}

	public void setDither(boolean dither) {
		mPaint.setDither(dither);
		invalidateSelf();
	}

	public Bitmap getBitmap() {
		return mBitmap;
	}
}
