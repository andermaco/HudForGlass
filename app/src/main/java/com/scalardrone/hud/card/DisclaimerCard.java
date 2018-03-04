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
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.glass.widget.CardBuilder;
import com.scalardrone.commonsui.card.Card;

public class DisclaimerCard implements Card {

	private String mText;
	private String mFootnote;
	private Context mContext;

	public DisclaimerCard(Context context) {
		mText = "";
		mFootnote = "";
		mContext = context;
	}

	public void setText(String text) {
		mText = text;
	}

	public void setFootnote(String footnote) {
		mFootnote = footnote;
	}

	@Override
	public View getView() {
		CardBuilder textCard = new CardBuilder(mContext, CardBuilder.Layout.TEXT)
				.setText(mText);
		CardBuilder footerCard = new CardBuilder(mContext, CardBuilder.Layout.ALERT)
				.setFootnote(mFootnote);
		FrameLayout layout = new FrameLayout(mContext);
		layout.addView(textCard.getView());
		layout.addView(footerCard.getView());
		return layout;
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
