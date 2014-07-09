package com.example.myfirstlauncher;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SubActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.opencv_camera);
	Button btn = (Button)findViewById(R.id.button1);
	
	btn.setOnClickListener(new View.OnClickListener() {
		@Override
			public void onClick(View v) {
			finish();
			}
	});;	
}
	
}
