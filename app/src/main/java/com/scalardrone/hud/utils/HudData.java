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

package com.scalardrone.hud.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class HudData {

	private static final String PREFS_NAME = "hud_preferences";
	public static final String PARAM_METRICS = "metrics";

	public static enum EnumMetricsType {
		KNOTS_FEETS, METERS_KMH, MILES_FEETS;

		public static EnumMetricsType toMyEnum(String myEnumString) {
			try {
				return valueOf(myEnumString);
			} catch (Exception ex) {
				// For error cases
				return KNOTS_FEETS;
			}
		}
	}

	public static EnumMetricsType getMetrics(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		String metrics = settings.getString(PARAM_METRICS, EnumMetricsType.KNOTS_FEETS.toString());
		return EnumMetricsType.toMyEnum(metrics);
	}

	public static void setMetrics(Context context, EnumMetricsType myMetrics) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PARAM_METRICS, myMetrics.toString());
		// Commit the edits!
		editor.commit();
	}
}
