package com.example.myfirstlauncher;


import android.app.ActionBar.LayoutParams;
import android.content.ClipData.Item;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

public class DrawLongClickListener implements OnItemLongClickListener{

	SlidingDrawer drawerForAdapter;
	RelativeLayout homeViewForAdapter;
	Context mContext;
	MainActivity.Pac[] pacsForListener;
	
	public DrawLongClickListener(Context ctxt,SlidingDrawer slingDrawer,RelativeLayout homeView,MainActivity.Pac[] pacs){
	mContext=ctxt;
	drawerForAdapter=slingDrawer;
	homeViewForAdapter=homeView;
	pacsForListener=pacs;
	}
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View item, int pos,
			long arg3) {
		MainActivity.appLaunchable=false;
		// TODO Auto-generated method stub
		LayoutParams lp=new LayoutParams(item.getWidth(),item.getHeight());
		
		lp.leftMargin=(int)item.getX();
		lp.topMargin=(int)item.getY();
		
		LayoutInflater li=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout ll=(LinearLayout) li.inflate(R.layout.draw_item, null);
		((ImageView)ll.findViewById(R.id.icon_image)).setImageDrawable(((ImageView)item.findViewById(R.id.icon_image)).getDrawable());
		((TextView)ll.findViewById(R.id.icon_text)).setText(((TextView)item.findViewById(R.id.icon_text)).getText());
		
		ll.setOnTouchListener(new AppTouchListener(item.getWidth()));
		ll.setOnClickListener(new AppClickListener(pacsForListener,mContext));
		
		String [] data=new String[2];
		data[0]=pacsForListener[pos].packagename;
		data[1]=pacsForListener[pos].name;
		ll.setTag(data);
		
		homeViewForAdapter.addView(ll,lp);
		drawerForAdapter.animateClose();
		drawerForAdapter.bringToFront();
		
		return false;
	}

}
