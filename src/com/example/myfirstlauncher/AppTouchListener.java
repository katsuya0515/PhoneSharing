package com.example.myfirstlauncher;

//import android.app.ActionBar.LayoutParams;
//import android.app.ActionBar.LayoutParams;
//import android.view.ViewGroup.LayoutParams
import android.widget.RelativeLayout.LayoutParams;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class AppTouchListener implements OnTouchListener{

	int iconSize;
	
	public AppTouchListener (int size) {
		iconSize=size;
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			LayoutParams lp=new LayoutParams(iconSize,iconSize);
			lp.leftMargin=(int)event.getRawX()-iconSize/2;
			lp.topMargin=(int)event.getRawY()-iconSize/2;
			
			v.setLayoutParams(lp);
		}
		return true;
	}

}
