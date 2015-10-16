package com.tilak.noteshare;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class DrawingView extends View {

	private float brushSize, lastBrushSize;
	private boolean erase = false;
	private boolean isDrawn = false;
	// drawing path
	private Path drawPath;
	// drawing and canvas paint
	private Paint drawPaint, canvasPaint;
	// initial color
	private int paintColor = 0xFF000000;
	// canvas
	private Canvas drawCanvas;
	// canvas bitmap
	private Bitmap canvasBitmap;

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		brushSize = getResources().getInteger(R.integer.medium_size);
		lastBrushSize = brushSize;
		setupDrawing();
	}

	public void setErase(boolean isErase) {
		// set erase true or false

		if (isErase==true)
		{
			///paintColor = 0xFFFFFFFF;
			drawPaint.setColor(0xFFFFFFFF);
			erase = isErase;
		}
		else
		{
			
			paintColor = getCurrentPaintColor();
			drawPaint.setColor(paintColor);
			erase = isErase;
		}
		
	}

	private void setupDrawing() {
		// get drawing area setup for interaction
		drawPath = new Path();
		drawPaint = new Paint();
		drawPaint.setColor(paintColor);
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(20);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		canvasPaint = new Paint(Paint.DITHER_FLAG);
		isDrawn=false;

		drawPaint.setStrokeWidth(brushSize);

		if (erase)
			drawPaint
					.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		else
			drawPaint.setXfermode(null);
		// draw
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// view given size
		super.onSizeChanged(w, h, oldw, oldh);
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// draw view
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		float touchX = event.getX();
		float touchY = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		    drawPath.moveTo(touchX, touchY);
		    isDrawn=true;
		    break;
		case MotionEvent.ACTION_MOVE:
		    drawPath.lineTo(touchX, touchY);
		    isDrawn=true;
		    break;
		case MotionEvent.ACTION_UP:
		    drawCanvas.drawPath(drawPath, drawPaint);
		    isDrawn=true;
		    drawPath.reset();
		    break;
		default:
			 isDrawn=false;
		    return false;
		}
		
		invalidate();
		isDrawn=true;
		return true;
	//detect user touch     
	}

	public void setBrushSize(float newSize) {
		// update size

		float pixelAmount = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, newSize, getResources()
						.getDisplayMetrics());
		brushSize = pixelAmount;
		drawPaint.setStrokeWidth(brushSize);
	}

	public void setLastBrushSize(float lastSize) {
		lastBrushSize = lastSize;
	}

	public float getLastBrushSize() {
		return lastBrushSize;
	}

	public void startNew() {
		drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		invalidate();
	}
	public void clear() 
	{
		setupDrawing();
	}
	public void setDrawColor(int newColor){
		//set color   
		invalidate();
		paintColor =newColor;// Color.parseColor(newColor);
		drawPaint.setColor(paintColor);
		}
	public int getCurrentPaintColor()
	{
		return paintColor;
	}
	public int getEraseColor()
	{
		
		return 0xFFFFFFFF;
	}
	
	public Bitmap  getBitMapimagae()
	{
		return canvasBitmap;	
	}
	public boolean getUserDrawn()
	{
		return isDrawn;
	}
	public void setUserDrawn(boolean isDraw)
	{
		 isDrawn=isDraw;
	}
}
