package com.example.myfirstlauncher;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class ImageTouchedListener implements OnTouchListener {
	
	float[] touchLocDown;
	float[] touchLocUp;
	float moveDistanceX;
	float moveDistanceY;
	int touchCount;
	Context mContext;
	MainActivity mActivity;
	boolean modeA= true;
	
	public ImageTouchedListener(MainActivity act) {
		// TODO Auto-generated constructor stub
		//mContext=ctxt;\
		mActivity=act;
		touchLocUp=new float[2];
		touchLocDown=new float[2];
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				 String action = "";
				    switch (event.getAction()) {
				    case MotionEvent.ACTION_DOWN:
				        action = "ACTION_DOWN";
				        touchLocDown[0]=event.getX();
				        touchLocDown[1]=event.getY();
				        Log.v("MotionEvent","X:"+event.getX()+" Y:"+event.getY());
				        touchCount++;
				        break;
				    case MotionEvent.ACTION_UP:
				        action = "ACTION_UP";
				        touchLocUp[0]=event.getX();
				        touchLocUp[1]=event.getY();
				        Log.v("MotionEvent","X:"+event.getX()+" Y:"+event.getY());
				        moveDistanceX=touchLocDown[0]-touchLocUp[0];
				        moveDistanceY=touchLocDown[1]-touchLocUp[1];
				        
				        if(moveDistanceX>300 && touchLocDown[1]<200&&touchLocUp[1]<200){
				        	Log.v("MotionEvent","Drag left");
				        //	modeA=true;
				        	//set_packs();
				        	
				        }else if(moveDistanceX<-300 && touchLocDown[1]<200&&touchLocUp[1]<200){
				        	Log.v("MotionEvent","Drag right");
				        //	modeA=false;
				       // 	set_packs();
				        	mActivity.camera_setting();
				        	
				        }
				        break;
				    case MotionEvent.ACTION_MOVE:
				        action = "ACTION_MOVE";
				        break;
				    case MotionEvent.ACTION_CANCEL:
				        action = "ACTION_CANCEL";
				        break;
				    }
				    
				  if(touchCount==3){
					 mActivity.modeA=!mActivity.modeA;
					mActivity.set_packs();
					  touchCount=0;
				  }
		return true;
	}


	

}
