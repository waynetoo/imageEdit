package com.wicloud.eidtimage;

import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * 字
 * 
 * @author Administrator
 * 
 */
public class Word {
	private float left, top;
	private Paint paint;
	private String wordString;

	public float getLeft() {
		return left;
	}

	public void setLeft(float left) {
		this.left = left;
	}

	public float getTop() {
		return top;
	}

	public void setTop(float top) {
		this.top = top;
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	public String getWordString() {
		return wordString;
	}

	public void setWordString(String wordString) {
		this.wordString = wordString;
	}

	public Word(float left, float top, Paint paint, String wordString) {
		super();
		this.left = left;
		this.top = top;
		this.paint = paint;
		this.wordString = wordString;
	}

	public Word() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void reset(Matrix matrix) {
		float[] values = new float[9];
		matrix.getValues(values);
		left = (int) ((left - values[Matrix.MTRANS_X]) / values[Matrix.MSCALE_X]);
		top = (int) ((top - values[Matrix.MTRANS_Y]) / values[Matrix.MSCALE_Y]);
		paint.setTextSize(paint.getTextSize() / values[Matrix.MSCALE_X]);
	}

}
