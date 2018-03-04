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

package com.scalardrone.hud;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.glass.widget.CardBuilder;
import com.scalardrone.commonsui.activity.CardScrollActivity;
import com.scalardrone.commonsui.card.SmartCard;
import com.scalardrone.hud.card.DisclaimerCard;
import com.scalardrone.hud.card.HudCard;
import com.scalardrone.hud.utils.HudData;

import java.math.BigDecimal;
import java.util.List;

public class HudLayerActivity extends CardScrollActivity implements
		SensorEventListener, LocationListener {

	private static final String TAG = HudLayerActivity.class.getSimpleName();

	private HudData.EnumMetricsType enum_metrics;
	private HudCard mHudCard;
	private boolean mOpenMenu = false;
	private int mScreenStatus;

	// Sensors
	private SensorManager mSensorManager;
	private float mOrientation[];
	private float mRotationM[];
	private float mRemapedRotationM[];

	// Location
	private LocationManager mLocationManager;
	private volatile int mGpsStatus;
	private long mLastLocationMillis;
	private long mLoadingStartTime;
	private volatile boolean checkStatus;
	private CheckGpsStatus checkGpsStatus;
	private static final Long GPS_LOADING_TIMEOUT_MILLISCNS = Long
			.valueOf(10000);
	private static final Long GPS_TIMEOUT_MILLISCNS = Long.valueOf(10000);
	private static final Long GPS_CHECK_DELAY = Long.valueOf(1000);

	// The minimum distance desired between location notifications.
	private static final long METERS_BETWEEN_LOCATIONS = 0;
	// The minimum elapsed time desired between location notifications.
	private static final long MILLIS_BETWEEN_LOCATIONS = 500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get stored metrics
		enum_metrics = HudData.getMetrics(getApplicationContext());

		// Keep screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Init sensor arrays
		mOrientation = new float[3];
		mRotationM = new float[9];
		mRemapedRotationM = new float[9];

		// Get audio manager
		audio = (AudioManager) getApplicationContext().getSystemService(
				Context.AUDIO_SERVICE);

		// Init Managers
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		initListeners();

		// Provider criteria
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setBearingRequired(true);
		criteria.setBearingAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(true);
		criteria.setSpeedRequired(true);
		criteria.setSpeedAccuracy(Criteria.ACCURACY_FINE);

		// Get location requests for all providers
		mLastLocationMillis = 0;
		List<String> providers = mLocationManager.getProviders(criteria, true);
		for (String provider : providers) {
			Log.d(TAG, "Register location provider: " + provider);
			mLocationManager.requestLocationUpdates(provider,
					MILLIS_BETWEEN_LOCATIONS, METERS_BETWEEN_LOCATIONS, this);
		}

		// Show discmlaimer
		showDisclaimerView();
	}

	@Override
	protected void onDestroy() {
		// Stopping thread
		checkStatus = false;

		// Stop location updates
		mLocationManager.removeUpdates(this);

		// Stop sensors
		mSensorManager.unregisterListener(this);

		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (enum_metrics == HudData.EnumMetricsType.METERS_KMH) {
			menu.findItem(R.id.meters_kmh).setVisible(false);
			menu.findItem(R.id.knots_feets).setVisible(true);
		} else if (enum_metrics == HudData.EnumMetricsType.KNOTS_FEETS) {
			menu.findItem(R.id.meters_kmh).setVisible(true);
			menu.findItem(R.id.knots_feets).setVisible(false);
		}

		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection.
		switch (item.getItemId()) {
		case R.id.knots_feets:
			Log.d(TAG, "Knots selected");
			// Store selected action in preferences
			enum_metrics = HudData.EnumMetricsType.KNOTS_FEETS;
			HudData.setMetrics(getApplicationContext(), enum_metrics);

			// Start hud layer for knots feet
			mHudCard.setMetrics(enum_metrics);
			return true;
		case R.id.meters_kmh:
			Log.d(TAG, "Meters selected");
			// Store selected action in preferences
			enum_metrics = HudData.EnumMetricsType.METERS_KMH;
			HudData.setMetrics(getApplicationContext(), enum_metrics);

			// Update view for meters kmh
			mHudCard.setMetrics(enum_metrics);
			return true;
        case R.id.mph_feets:
                Log.d(TAG, "Mph selected");
                // Store selected action in preferences
                enum_metrics = HudData.EnumMetricsType.MILES_FEETS;
                HudData.setMetrics(getApplicationContext(), enum_metrics);

                // Update view for meters kmh
                mHudCard.setMetrics(enum_metrics);
                return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		mOpenMenu = false;
		super.onOptionsMenuClosed(menu);
	}

	/**
	 * Show HUB layer
	 */
	private void showHubLayerView() {
		// Update screen status to 2
		mScreenStatus = 2;
		
		// Hide indeterminate
		hideIndeterminate();

		// Update status view to 0
		mGpsStatus = 0;

		// Set layout
		mHudCard = new HudCard(getApplicationContext());
		mHudCard.setMetrics(enum_metrics);

		// Update view
		if (getCardCount() > 0) {
			clearCards();
		}

		// Add hud card
		addCard(mHudCard);
	}

	/**
	 * Show GPS loading screen
	 */
	private void showDisclaimerView() {
		// Update screen status to 0
		mScreenStatus = 0;

		// Create progress bar loading card
		DisclaimerCard disclaimerCard = new DisclaimerCard(getApplicationContext());
		disclaimerCard.setText(getString(R.string.disclaimer));
		disclaimerCard.setFootnote(getString(R.string.disclaimer_footnote));

		// Update view
		if (getCardCount() > 0) {
			clearCards();
		}

		// Add progress card
		addCard(disclaimerCard);
	}
	
	/**
	 * Show GPS loading screen
	 */
	private void showLoadingGPSView() {
		// Update screen status to 1
		mScreenStatus = 1;
		
		// Update status view to 1
		mGpsStatus = 1;

		// Create progress bar loading card
		SmartCard progressCard = new SmartCard(getApplicationContext(),
				CardBuilder.Layout.TITLE);
		progressCard.addImage(R.drawable.hud_logo_white);
		progressCard.setSelectable(false);

		// Update view
		if (getCardCount() > 0) {
			clearCards();
		}

		// Add progress card
		addCard(progressCard);

		// Start indeterminate
		showIndeterminate();
	}

	private void hideGpsSignal() {
		// Update status view to 1
		mGpsStatus = 1;

		// Disable gps signal icon
		mHudCard.setSignal(false);
		if(!mOpenMenu) {
			refresh();
		}
	}

	private void showGpsSignal() {
		// Update status view to 1
		mGpsStatus = 0;

		// Disable gps signal icon
		mHudCard.setSignal(true);
		if(!mOpenMenu) {
			refresh();
		}
	}

	/**
	 * Show GPS error message
	 */
	private void showGPSError() {
		// Hide indeterminate
		hideIndeterminate();

		// Clear cards
		clearCards();

		// Update status view to 2
		mGpsStatus = 2;

		// Show alert message
		showAlert(com.scalardrone.commonsui.R.drawable.ic_warning_150,
				R.string.location_error, R.string.location_error_footer,
				new Runnable() {

					@Override
					public void run() {
						// Finish
						finish();
					}
				}, new Runnable() {

					@Override
					public void run() {
						// Finish and show settings
						startActivity(new Intent(
								Settings.ACTION_BLUETOOTH_SETTINGS));
						finish();
					}
				});
	}

	/**
	 * Initialize sensor and location listeners
	 */
	public void initListeners() {
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
				SensorManager.SENSOR_DELAY_UI);

		// The rotation vector sensor doesn't give us accuracy updates, so we
		// observe the
		// magnetic field sensor solely for those.
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_UI);
	}

	/**
	 * SensorEventListener implemented methods
	 */
	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {

		if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR
				|| sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			// Get the current heading from the sensor, then notify the
			// listeners of the change.
			SensorManager.getRotationMatrixFromVector(mRotationM,
					sensorEvent.values);
			SensorManager.remapCoordinateSystem(mRotationM,
					SensorManager.AXIS_X, SensorManager.AXIS_Z,
					mRemapedRotationM);
			SensorManager.getOrientation(mRemapedRotationM, mOrientation);

			if (mHudCard != null) {
				// Store the pitch (used to display a message indicating that
				// the user's head
				// angle is too steep to produce reliable results.
				mHudCard.setPitch((float) Math.toDegrees(mOrientation[1]));
				mHudCard.setAzimuth((360.0F + (float) Math.round(2.0D * Math
						.toDegrees(mOrientation[0])) / 2.0F) % 360.0F);
				float f3 = new BigDecimal(Math.toDegrees(mOrientation[2]))
						.setScale(1, 4).floatValue();
				mHudCard.setRoll(-1.0F * f3);
				if(!mOpenMenu) {
					refresh();
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/**
	 * LocationListener implemented methods
	 */
	@Override
	public void onLocationChanged(Location location) {
		if (location == null
				|| (location.getAltitude() == 0.0 && location.getSpeed() == 0.0))
			return;

		// Get location data
		Log.d(TAG, "onLocationChanged " + location.getAccuracy() + " "
				+ location.getLatitude() + " " + location.getAltitude() + " "
				+ location.getSpeed() + " " + location.getProvider());
		mLastLocationMillis = SystemClock.elapsedRealtime();

		// Check is we are showing the hub layer
		if (mGpsStatus != 0) {
			// Show hub layer
			showHubLayerView();
		}

		// Update hub view values for altitude and speed
		if (mHudCard != null) {
			float f = (new BigDecimal(location.getAltitude())).setScale(1, 4)
					.floatValue();
			float f1 = (new BigDecimal(3.6F * location.getSpeed())).setScale(1, 4)
					.floatValue();
			mHudCard.setAltitude(f);
			mHudCard.setSpeed(f1);
			if(!mOpenMenu) {
				refresh();
			}
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(TAG, "onProviderDisabled " + provider);
		// Don't need to do anything here.
	}

	@Override
	public void onProviderEnabled(String provider) {
		// Don't need to do anything here.
		Log.d(TAG, "onProviderEnabled " + provider);
	}

	@Override
	public void onStatusChanged(String provider, int statusLoc, Bundle extras) {
		// Don't need to do anything here.
	}

	/**
	 * Thread to check GPS status
	 * 
	 * @author joss
	 * 
	 */
	private class CheckGpsStatus extends Thread {

		public void run() {
			Log.d(TAG, "Starting thread checkGPSStatus...");
			Long currentTime;
			mLoadingStartTime = SystemClock.elapsedRealtime();
			do {
				// Update current time
				currentTime = SystemClock.elapsedRealtime();

				// Check screen showing depending on GPS status
				if (mGpsStatus == 0) {
					// Check last update pass timeout limit
					if (currentTime - mLastLocationMillis > GPS_TIMEOUT_MILLISCNS) {
						// Hide gps signal icon
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								hideGpsSignal();
							}

						});

						// Initialize loading start time
						mLoadingStartTime = SystemClock.elapsedRealtime();
					} else {
						// Enable gps signal icon
						// Hide gps signal icon
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								showGpsSignal();
							}

						});
					}
				} else if (mGpsStatus == 1) {
					// Check loading start time pass timeout limit
					if (currentTime - mLoadingStartTime > GPS_LOADING_TIMEOUT_MILLISCNS) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// Show error screen
								showGPSError();
							}
						});

					}
				}

				try {
					// Sleep until next check
					java.lang.Thread.sleep(GPS_CHECK_DELAY);
				} catch (InterruptedException e) {
				}

			} while (checkStatus);
			Log.d(TAG, "Thread CheckGpsStatus stopped");
		}

	}

	@Override
	public void onCardSelected(int position) {	
		if(mScreenStatus == 0) {
			// Init thread to check gps status
			checkStatus = true;
			checkGpsStatus = new CheckGpsStatus();
			checkGpsStatus.start();
			
			// Show loading GPS
			showLoadingGPSView();
		} else if(mScreenStatus == 2) {
			// Open menu
			mOpenMenu = true;
			openOptionsMenu();
		}
	}

}
