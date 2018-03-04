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

package com.scalardrone.hud.card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.scalardrone.commonsui.card.Card;
import com.scalardrone.hud.R;
import com.scalardrone.hud.utils.HudData;
import com.scalardrone.hud.utils.HudData.EnumMetricsType;
import com.scalardrone.hud.view.HudView;

public class HudCard implements Card {

	private View mView;
	private HudView mHudView;

	public HudCard(Context context) {
		// Inflate layout from XML
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = inflater.inflate(R.layout.activity_hud_layer, null);

		// Get hud view
		mHudView = (HudView) mView.findViewById(R.id.hud_layer);
	}

	public void setPitch(float pitch) {
		mHudView.setPitch(pitch);
	}

	public void setAzimuth(float azimuth) {
		mHudView.setAzimuth(azimuth);
	}

	public void setRoll(float roll) {
		mHudView.setRoll(roll);
	}

	public void setAltitude(float altitude) {
		mHudView.setAltitude(altitude);
	}

	public void setSpeed(float speed) {
		mHudView.setSpeed(speed);
	}
	
	public void setSignal(boolean signal) {
		mHudView.setSignal(signal);
	}

	public void setMetrics(HudData.EnumMetricsType enum_metrics) {
		if (enum_metrics == HudData.EnumMetricsType.KNOTS_FEETS) {
			mHudView.setMetrics(1);
		} else if (enum_metrics == HudData.EnumMetricsType.METERS_KMH) {
			mHudView.setMetrics(0);
		} else if (enum_metrics == EnumMetricsType.MILES_FEETS) {
            mHudView.setMetrics(2);
        }
	}

	@Override
	public View getView() {
		return mView;
	}

	@Override
	public boolean isSelectable() {
		return true;
	}

	@Override
	public void setSelectable(boolean selectable) {
		// Do nothing
	}

}
