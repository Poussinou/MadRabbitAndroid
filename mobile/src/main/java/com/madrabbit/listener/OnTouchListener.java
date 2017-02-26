package com.madrabbit.listener;

import android.view.MotionEvent;
import android.view.View;

public class OnTouchListener implements View.OnTouchListener
{
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_UP:
				if(!v.hasFocus()) {
					v.requestFocus();
				}
				break;
		}
		return false;
	}
}
