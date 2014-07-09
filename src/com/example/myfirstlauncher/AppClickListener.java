package com.example.myfirstlauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract.Contacts.Data;
import android.view.View;
import android.view.View.OnClickListener;

public class AppClickListener implements OnClickListener{
	MainActivity.Pac[] pacsForLister;
	Context mContext;
	
	public AppClickListener(MainActivity.Pac[] pacs,Context ctxt){
		pacsForLister=pacs;
		mContext=ctxt;
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		String [] data;
		data=(String [])v.getTag();
		
		//Intent launchIntent = pmForListener.getLaunchIntentForPackage(pacsForAdapter[pos].name);
				Intent launchIntent = new Intent(Intent.ACTION_MAIN);
				launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				
				
				//for photos in order not to open by g-drive.
				ComponentName cp= new ComponentName(data[0],data[1]);
				launchIntent.setComponent(cp);
				
				mContext.startActivity(launchIntent);
	}

	

}
