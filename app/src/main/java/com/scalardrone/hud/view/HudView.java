/*
 * HudForGlass
 * Copyright (C) 2014 ScalarDrone.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.scalardrone.hud.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.scalardrone.hud.R;

public class HudView extends View {
	private int centerX, centerY;
	private int height, width;
	private Context mContext;
	// Density
	private float D;
	// Drawing styles
	private Paint mPaintLine, mPaintSpeedLine, mRect;
	private Paint mPaintTextCenter, mPaintTextLeft, mPaintTextRight,
			mPaintPitchTextLeft, mPaintPitchTextRight, mPainTextAzimuth,
			mPainTextGPS;
	private float mAltitude;
	private float mAzimuth;
	private float mPitch;
	private float mSpeed;
	private float mRoll;
	private String SpeedString;
	private String ALTString;
	private int mMetrics = 0;
	private float ringWidth;
	private int radius;
	private boolean mSignal;

	public HudView(Context context) {
		super(context);
		init(context);
	}

	public HudView(Context context, AttributeSet attributeset) {
		super(context, attributeset);
		init(context);
	}

	public HudView(Context context, AttributeSet attributeset, int i) {
		super(context, attributeset, i);
		init(context);
	}

	protected void onMeasure(int i, int j) {
		setMeasuredDimension(android.view.View.MeasureSpec.getSize(i),
				android.view.View.MeasureSpec.getSize(j));
		height = getMeasuredHeight();
		width = getMeasuredWidth();
		centerX = width / 2;
		centerY = height / 2;
		ringWidth = 15 * D;
		radius = 2 * centerY / 3;
	}

	private void init(Context context) {
		mContext = context;
		WindowManager windowmanager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		windowmanager.getDefaultDisplay().getMetrics(displaymetrics);
		D = displaymetrics.density;
		float f_1 = 2F;
		float f_2 = 18F;
		float f_3 = 13F;
		mPaintLine = new Paint(1);
		mPaintLine.setStyle(android.graphics.Paint.Style.STROKE);
		mPaintLine.setColor(0xFFFFFFFF);
		mPaintLine.setStrokeWidth(f_1 * D);

		mPaintSpeedLine = new Paint(1);
		mPaintSpeedLine.setStyle(android.graphics.Paint.Style.STROKE);
		mPaintSpeedLine.setColor(0xFFFFFFFF);
		mPaintSpeedLine.setStrokeWidth(f_1 * D);

		mRect = new Paint(1);
		mRect.setStyle(android.graphics.Paint.Style.FILL);
		mRect.setColor(Color.BLACK);
		mPaintTextCenter = new Paint(1);
		mPaintTextCenter.setColor(0xFFFFFFFF);
		mPaintTextCenter.setTextAlign(Align.CENTER);
		mPaintTextCenter.setTextSize(f_2 * D);
		mPainTextAzimuth = new Paint(1);
		mPainTextAzimuth.setColor(0xFFFFFFFF);
		mPainTextAzimuth.setTextAlign(Align.CENTER);
		mPainTextAzimuth.setTextSize(f_3 * D);
		mPaintTextRight = new Paint(1);
		mPaintTextRight.setColor(0xFFFFFFFF);
		mPaintTextRight.setTextAlign(android.graphics.Paint.Align.RIGHT);
		mPaintTextRight.setTextSize(f_2 * D);
		mPaintTextLeft = new Paint(1);
		mPaintTextLeft.setColor(0xFFFFFFFF);
		mPaintTextLeft.setTextAlign(android.graphics.Paint.Align.LEFT);
		mPaintTextLeft.setTextSize(f_2 * D);

		mPaintPitchTextLeft = new Paint(1);
		mPaintPitchTextLeft.setColor(0xFFFFFFFF);
		mPaintPitchTextLeft.setTextAlign(android.graphics.Paint.Align.LEFT);
		mPaintPitchTextLeft.setTextSize(f_2 * D);

		mPaintPitchTextRight = new Paint(1);
		mPaintPitchTextRight.setColor(0xFFFFFFFF);
		mPaintPitchTextRight.setTextAlign(android.graphics.Paint.Align.RIGHT);
		mPaintPitchTextRight.setTextSize(f_2 * D);

		// Gps signal
		mPainTextGPS = new Paint(1);
		mPainTextGPS.setColor(0xFFFFFFFF);
		mPainTextGPS.setTextSize(f_2 * D);
		mSignal = false;
	}

	/**
	 * @return the mAltitude
	 */
	public float getAltitude() {
		return mAltitude;
	}

	/**
	 * @param altitude the mAltitude to set
	 */
	public void setAltitude(float altitude) {
		this.mAltitude = altitude;
	}

	/**
	 * @return the mAzimuth
	 */
	public float getAzimuth() {
		return mAzimuth;
	}

	/**
	 * @param azimuth the mAzimuth to set
	 */
	public void setAzimuth(float azimuth) {
		this.mAzimuth = azimuth;
	}

	/**
	 * @return the mPitch
	 */
	public float getPitch() {
		return mPitch;
	}

	/**
	 * @param pitch the mPitch to set
	 */
	public void setPitch(float pitch) {
		this.mPitch = pitch;
	}

	/**
	 * @return the mSpeed
	 */
	public float getSpeed() {
		return mSpeed;
	}

	/**
	 * @param speed the mSpeed to set
	 */
	public void setSpeed(float speed) {
		this.mSpeed = speed;
	}

	/**
	 * @return the mRoll
	 */
	public float getRoll() {
		return mRoll;
	}

	/**
	 * @param roll the mRoll to set
	 */
	public void setRoll(float roll) {
		this.mRoll = roll;
	}

	/**
	 * @return the mMetrics
	 */
	public int getMetrics() {
		return mMetrics;
	}

	/**
	 * @param metrics the mMetrics to set
	 */
	public void setMetrics(int metrics) {
		this.mMetrics = metrics;
	}

	protected void onDraw(Canvas canvas) {
		drawGpsSignal(canvas);
		drawArrow(canvas);
		drawSpeed(canvas, (int) mSpeed);
		drawAltitude(canvas, (int) mAltitude);
		drawPitch(canvas, (int) mPitch);
		drawRoll(canvas, (int) mRoll);
		drawAzimuth(canvas, (int) mAzimuth);
	}

	/**
	 * Draw Arrows, pitch line and GPS
	 * 
	 * @param canvas
	 */
	private void drawArrow(Canvas canvas) {
		Point center = new Point(centerX, height);
		RectF innerBoundingBox = new RectF(center.x - radius + ringWidth,
				center.y - radius + ringWidth, center.x + radius - ringWidth,
				center.y + radius - ringWidth);

		// Draw Pitch line
		canvas.drawLine((float) centerX + 40, centerY, centerX + 80,
				(float) centerY, mPaintLine);
		canvas.drawLine((float) centerX - 40, centerY, centerX - 80,
				(float) centerY, mPaintLine);
		canvas.drawLine((float) centerX + 80, centerY + 2, centerX + 80,
				(float) centerY - D * 15, mPaintLine);
		canvas.drawLine((float) centerX - 80, centerY + 2, centerX - 80,
				(float) centerY - D * 15, mPaintLine);

		// Draw High Arrow
		Path path = new Path();
		path.moveTo(centerX, D * 55);
		path.lineTo((float) centerX - D * 3, D * centerY / 3);
		path.lineTo((float) centerX + D * 3, D * centerY / 3);
		path.close();
		canvas.drawPath(path, mPaintLine);

		// Draw Bottom Arrow. Heading (Rosa de Rumbos)
		path = new Path();
		path.moveTo(centerX, innerBoundingBox.top + 30);
		path.lineTo((float) centerX - D * 5, innerBoundingBox.top + 40);
		path.lineTo((float) centerX + D * 5, innerBoundingBox.top + 40);
		path.close();
		canvas.drawPath(path, mPaintLine);
		canvas.drawLine(centerX, innerBoundingBox.top + 40, (float) centerX,
				height, mPaintLine); // Draw vertical line
		canvas.drawLine(centerX - 10, height - 10, centerX + 10, height - 10,
				mPaintLine); // Draw horizontal line

	}

	/**
	 * Draw Azimuth (compass)
	 * 
	 * @param canvas
	 * @param a
	 */
	private void drawAzimuth(Canvas canvas, int a) {
		int i = 0;
		int G = 0;
		Point center = new Point(centerX, height);
		canvas.rotate(-a, center.x, center.y);
		RectF innerBoundingBox = new RectF(center.x - radius + ringWidth,
				center.y - radius + ringWidth, center.x + radius - ringWidth,
				center.y + radius - ringWidth);
		while (i < 36) {
			canvas.drawLine(center.x, (int) innerBoundingBox.top, center.x,
					(int) innerBoundingBox.top + 7, mPaintLine);

			if (G % 30 == 0) {
				if (G == 0 || G == 360)
					canvas.drawText(String.valueOf("N"), center.x,
							(int) innerBoundingBox.top + 25, mPainTextAzimuth);
				else if (G == 90)
					canvas.drawText(String.valueOf("E"), center.x,
							(int) innerBoundingBox.top + 25, mPainTextAzimuth);
				else if (G == 180)
					canvas.drawText(String.valueOf("S"), center.x,
							(int) innerBoundingBox.top + 25, mPainTextAzimuth);
				else if (G == 270)
					canvas.drawText(String.valueOf("W"), center.x,
							(int) innerBoundingBox.top + 25, mPainTextAzimuth);
				else
					canvas.drawText(String.valueOf(G / 10), center.x,
							(int) innerBoundingBox.top + 25, mPainTextAzimuth);
			}
			canvas.rotate(10, center.x, center.y);
			i++;
			G = G + 10;
		}
	}

	/**
	 * Draw Speed
	 * 
	 * @param canvas
	 * @param S
	 */
	private void drawSpeed(Canvas canvas, int S) {
		if (mMetrics == 1) {
			SpeedString = mContext.getString(R.string.speed_knots);
			S = (int) (S * 0.539956803);
		} else if (mMetrics == 2) {
            SpeedString = "Mph";
            S = (int) (S * 0.621371192);
        } else
        {
			SpeedString = mContext.getString(R.string.speed_kmh);
		}
		canvas.drawText(mContext.getString(R.string.speed), D * 65, D * 30
				- +centerY / 9, mPaintTextRight);
		canvas.drawText(SpeedString, D * 65, 5 * centerY / 3 + D * 20 + centerY
				/ 18, mPaintTextRight);
		canvas.drawLine(D * 25, 5 * centerY / 3 + centerY / 18, D * 65, 5
				* centerY / 3 + centerY / 18, mPaintSpeedLine);
		canvas.drawLine(D * 25, centerY / 3 - centerY / 9, D * 65, centerY / 3
				- centerY / 9, mPaintSpeedLine);
		float f2 = mPaintTextRight.measureText(String.valueOf(S));
		canvas.drawLine(D * 65, centerY / 3 - centerY / 9, D * 65, 5 * centerY
				/ 3 + centerY / 18, mPaintSpeedLine);
		// Draw Arrow
		canvas.drawLine(D * 65, centerY, D * 80, centerY - D * 14,
				mPaintSpeedLine);
		canvas.drawLine(D * 65, centerY, D * 80, centerY + D * 14,
				mPaintSpeedLine);
		canvas.drawLine(D * 80, centerY - D * 14, (80F + f2) * D, centerY - D
				* 14, mPaintSpeedLine);
		canvas.drawLine(D * 80, centerY + D * 14, (80F + f2) * D, centerY + D
				* 14, mPaintSpeedLine);
		canvas.drawLine((80F + f2) * D, centerY - D * 14, (80F + f2) * D,
				centerY + D * 14, mPaintSpeedLine);
		canvas.drawText(String.valueOf(S), ((72F + f2) - 1.0F) * D, centerY + D
				* 5, mPaintTextRight);
		int s_5 = S % 5;
		int k = 0;
		while (k < 12) {
			int s = S + 30 - s_5 - k * 5;// value
			int L = (int) (centerY / 3 + k * centerY / 9 + s_5 * centerY / 45);// coordinate
			if (s % 10 == 0) {
				canvas.drawLine(D * 50, L, D * 65, L, mPaintSpeedLine);
				canvas.drawText(String.valueOf(s), D * 40, L + D * 5,
						mPaintTextRight);
			} else {
				canvas.drawLine(D * 55, L, D * 65, L, mPaintSpeedLine);
			}
			k++;
		}
	}

	/**
	 * Draw Altitude
	 * 
	 * @param canvas
	 * @param A
	 */
	private void drawAltitude(Canvas canvas, int A) {
		if (mMetrics == 1 || mMetrics == 2) {
			A = (int) (A * 3.2808);
			ALTString = mContext.getString(R.string.alt_feet);
		} else
        {
			ALTString = mContext.getString(R.string.alt_meters);
		}
		float f2 = mPaintTextLeft.measureText(String.valueOf(A));
		canvas.drawText(mContext.getString(R.string.alt), width - D * 65, D
				* 30 - centerY / 9, mPaintTextLeft); // Draw ALT text
		canvas.drawText(ALTString, width - D * 65, 5 * centerY / 3 + D * 20
				+ centerY / 18, mPaintTextLeft);
		canvas.drawLine(width - 10 - D * 55, centerY / 3 - centerY / 9, width
				- 10 - D * 55, 5 * centerY / 3 + centerY / 18, mPaintLine);
		canvas.drawLine(width - 10 - D * 55, 5 * centerY / 3 + centerY / 18,
				width - 10 - D * 25, 5 * centerY / 3 + centerY / 18, mPaintLine);
		canvas.drawLine(width - 10 - D * 55, centerY / 3 - centerY / 9, width
				- 10 - D * 25, centerY / 3 - centerY / 9, mPaintLine);
		// Draw Arrow
		canvas.drawLine(width - 10 - D * 55, centerY, width - 10 - D * 70,
				centerY - D * 14, mPaintLine);
		canvas.drawLine(width - 10 - D * 55, centerY, width - 10 - D * 70,
				centerY + D * 14, mPaintLine);
		canvas.drawLine(width - 10 - D * 70, centerY - D * 14, width - 10
				- (70F + f2) * D, centerY - D * 14, mPaintLine);
		canvas.drawLine(width - 10 - D * 70, centerY + D * 14, width - 10
				- (70F + f2) * D, centerY + D * 14, mPaintLine);
		canvas.drawLine(width - 10 - (70F + f2) * D, centerY - D * 14, width
				- 10 - (70F + f2) * D, centerY + D * 14, mPaintLine);
		canvas.drawText(String.valueOf(A), width - ((70F + f2) - 1.0F) * D,
				centerY + D * 5, mPaintTextLeft); // Draw Alt in meters
		int a_20 = A % 20;
		int k = 0;
		while (k < 20) {
			int a = A + 200 - a_20 - k * 20;// value
			int L = (int) (centerY / 3 + k * centerY / 15 + a_20 * centerY
					/ 300);// coordinate
			if (a % 100 == 0) {
				canvas.drawLine(width - 10 - D * 55, L, width - 10 - D * 40, L,
						mPaintLine);
				canvas.drawText(String.valueOf(a), width - D * 40, L + D * 5,
						mPaintTextLeft);
			} else {
				canvas.drawLine(width - 10 - D * 55, L, width - 10 - D * 45, L,
						mPaintLine);
			}
			k++;
		}
	}

	/**
	 * Draw Roll
	 * 
	 * @param canvas
	 * @param degrees
	 */
	private void drawRoll(Canvas canvas, float degrees) {

		int i = 0;
		if (degrees < 0) {
			degrees = 360 + degrees;
		}
		int t = (int) (degrees % 30);
		Point center2 = new Point(centerX, centerY / 2);
		int radius2 = centerY / 2;
		RectF innerBoundingBox = new RectF(center2.x - radius2 + ringWidth,
				center2.y - radius2 + ringWidth, center2.x + radius2
						- ringWidth, center2.y + radius2 - ringWidth);
		canvas.rotate(t, center2.x, center2.y);
		while (i < 6) {
			if (t - 90 == degrees || t + 270 == degrees) {

				Path path = new Path();
				path.moveTo((int) innerBoundingBox.left, center2.y + 4);
				path.lineTo((int) innerBoundingBox.left + 10, center2.y);
				path.lineTo((int) innerBoundingBox.left, center2.y - 4);
				path.close();
				canvas.drawPath(path, mPaintLine);

			} else {
				canvas.drawLine((int) innerBoundingBox.left, center2.y,
						(int) innerBoundingBox.left + 5, center2.y, mPaintLine);
			}
			canvas.rotate(30, center2.x, center2.y);
			t = t + 30;
			i++;

		}

		canvas.rotate(-t, center2.x, center2.y);
	}

	/**
	 * Draw pitch
	 * 
	 * @param canvas
	 * @param P
	 */
	private void drawPitch(Canvas canvas, int P) {
		// Draw plane pitch
		canvas.drawLine(centerX - 25, centerY + P, centerX + 25, centerY + P,
				mPaintLine);
		canvas.drawLine(centerX, centerY + P, centerX, centerY + P - 12,
				mPaintLine);

		// Draw plane pitch data
		canvas.drawText(String.valueOf(P), centerX - 85 - D * 2, centerY,
				mPaintPitchTextRight);
		canvas.drawText(String.valueOf(P), centerX + 85 + D * 2, centerY,
				mPaintPitchTextLeft);
	}

	private void drawGpsSignal(Canvas canvas) {
		if (mSignal) {
			canvas.drawText(mContext.getString(R.string.gps_signal),
					(width / 4) - 20, height - 50, mPainTextGPS);
		}
	}

	public boolean hasSignal() {
		return mSignal;
	}

	public void setSignal(boolean signal) {
		mSignal = signal;
	}
}
