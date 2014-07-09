package com.example.myfirstlauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerAdapter extends BaseAdapter{

	Context mContext;
	MainActivity.Pac[] pacsForAdapterPacs;
	
	public DrawerAdapter(Context c,MainActivity.Pac pacs[]){
		mContext =c;
		pacsForAdapterPacs =pacs;
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return pacsForAdapterPacs.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	static class ViewHolder{
		TextView text;
		ImageView icon;
		
	}
	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		
		
		ViewHolder viewHolder;
		LayoutInflater li=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if(convertView==null){
			convertView=li.inflate(R.layout.draw_item, null);
			viewHolder=new ViewHolder();
			viewHolder.text=(TextView)convertView.findViewById(R.id.icon_text);
			viewHolder.icon=(ImageView)convertView.findViewById(R.id.icon_image);
			
			convertView.setTag(viewHolder);
			
		}else viewHolder=(ViewHolder)convertView.getTag();
		if(pacsForAdapterPacs[pos].label.equalsIgnoreCase("tinder")){//LOL
			viewHolder.text.setText(pacsForAdapterPacs[pos+5].label);
			viewHolder.icon.setImageDrawable(pacsForAdapterPacs[pos+5].icon);
		}else{
		viewHolder.text.setText(pacsForAdapterPacs[pos].label);
		viewHolder.icon.setImageDrawable(pacsForAdapterPacs[pos].icon);
		}
		
		return convertView;
	}

}
