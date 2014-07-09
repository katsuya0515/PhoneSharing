package com.example.myfirstlauncher;

//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.ActionBar;
//import android.support.v4.app.Fragment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.core.Mat;



import org.opencv.objdetect.CascadeClassifier;

import android.R.integer;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.os.Build;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

public class MainActivity extends Activity {
	
	DrawerAdapter drawerAdapterObject;
	
	class Pac{
		
		Drawable icon;
		String name;
		String packagename;
		String label;
	}
	Pac[] pacs;
	PackageManager pm;
	GridView drawerGrid;
	SlidingDrawer slidingDrawer;
	RelativeLayout homeView;
	ImageView bgImage;
	ImageView swipeImage;
	ImageView checkImg;
	TextView detectName;
	int touchCount=0;
	static boolean appLaunchable=true;

	boolean modeA=true;
	
	float[] touchLocDown;
	float[] touchLocUp;
	float moveDistanceX;
	float moveDistanceY;
	
	MyAccountManager myAccountManager=null;
	CameraBridgeViewBase mCameraView ;
	
	SurfaceView sView;
	String mPath;
    PersonRecognizer fr;
	File mCascadeFile;
	CascadeClassifier mJavaDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mPath=getExternalStragePath()+"/myFirstLauncher/"; 

		myAccountManager=new MyAccountManager(this);
		//myAccountManager.getMyAccount();
		
		pm=getPackageManager();
		touchLocUp=new float[2];
		touchLocDown=new float[2];
	
	
		drawerGrid=(GridView) findViewById(R.id.content);
		//slidingDrawer=(SlidingDrawer) findViewById(R.id.drawer);
	//	homeView=(RelativeLayout)findViewById(R.id.home_view);
		bgImage=(ImageView)findViewById(R.id.bgimage);
		swipeImage=(ImageView)findViewById(R.id.swipe);
		swipeImage.setOnTouchListener(new ImageTouchedListener(this));
		checkImg=(ImageView)findViewById(R.id.checkimage);
		detectName=(TextView)findViewById(R.id.textView1);
	
		sView=(SurfaceView) findViewById(R.id.surfaceView1);
		 final FdCameraView cameraView = new FdCameraView(this,sView,checkImg,mPath,detectName);
	 cameraView.setOwner(this);
	   
		set_packs();
		
		/*
		slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			
			@Override
			public void onDrawerOpened() {
				appLaunchable=true;
			}
		});
	*/
	//For receiver, will be called when apps removed/added
	IntentFilter filter=new IntentFilter();
	filter.addAction(Intent.ACTION_PACKAGE_ADDED);
	filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
	filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
	filter.addDataScheme("package");
	
	registerReceiver(new PacReceiver(), filter);
	
	}
	
	@Override
    protected void onResume() {
        super.onResume();
      
        // static boolean initAsync(String Version, Context AppContext, LoaderCallbackInterface Callback)
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
    }
	
	 private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
	       

			@Override
	        public void onManagerConnected(int status) {
	            switch (status) {
	                case LoaderCallbackInterface.SUCCESS:
	                {
	                    Log.i("Opencv", "OpenCV loaded successfully");

	                    // Load native library after(!) OpenCV initialization
	                 //   System.loadLibrary("detection_based_tracker");
	            
	                  	 
	                    fr=new PersonRecognizer(mPath);
	                    fr.load();
	                    
	                    try {
	                        // load cascade file from application resources
	                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
	                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
	                        mCascadeFile = new File(cascadeDir, "lbpcascade.xml");
	                        FileOutputStream os = new FileOutputStream(mCascadeFile);

	                        byte[] buffer = new byte[4096];
	                        int bytesRead;
	                        while ((bytesRead = is.read(buffer)) != -1) {
	                            os.write(buffer, 0, bytesRead);
	                        }
	                        is.close();
	                        os.close();

	                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
	                        if (mJavaDetector.empty()) {
	                            Log.e("Opencv", "Failed to load cascade classifier");
	                            mJavaDetector = null;
	                        } else
	                            Log.i("Opencv", "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

	       //                 mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

	                        cascadeDir.delete();

	                    } catch (IOException e) {
	                        e.printStackTrace();
	                        Log.e("Opencv", "Failed to load cascade. Exception thrown: " + e);
	                    }

	                  
	              
	                } break;
	                default:
	                {
	                    super.onManagerConnected(status);
	                } break;
	                
	                
	            }
	        }
	    };
	// public abstract class BaseLoaderCallback implements LoaderCallbackInterface
 
	public void camera_setting() {
		Intent intent = new Intent(MainActivity.this, FdActivity.class);
		intent.putExtra("path", mPath);
        startActivity(intent);
	}

	public void set_packs(){
		
		//Get apps from main intent
		final Intent mainIntent= new Intent(Intent.ACTION_MAIN,null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> packsList = pm.queryIntentActivities(mainIntent, 0);
		
		
		pacs=new Pac[packsList.size()];
		for (int i=0;i<packsList.size();i++){
		
			pacs[i]=new Pac();
			pacs[i].icon=packsList.get(i).loadIcon(pm);
			pacs[i].packagename=packsList.get(i).activityInfo.packageName;
			pacs[i].name=packsList.get(i).activityInfo.name;
			pacs[i].label=packsList.get(i).loadLabel(pm).toString();
	
		}
		
		//sort
		if(modeA){
		new SortApps().exchange_sort(pacs);
		bgImage.setImageDrawable(getResources().getDrawable(R.drawable.boston));
		//myAccountManager.addMyAccount("KF0515", "com.twitter.android.auth.login", "fiky0515");
		}else{
			new SortApps().exchange_sort_reverse(pacs);
			bgImage.setImageDrawable(getResources().getDrawable(R.drawable.tokyo));
			
		}
		
		//put pacs to Gridview
		drawerAdapterObject=new DrawerAdapter(this, pacs);
		drawerGrid.setAdapter(drawerAdapterObject);
		drawerGrid.setOnItemClickListener(new DrawClickListener(this,pacs,pm));
		//drawerGrid.setOnItemLongClickListener(new DrawLongClickListener(this,slidingDrawer,homeView,pacs));
		
	}
	
	public class PacReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			set_packs();
		}
		
	}
	
	
	   /**
     * 外部ストレージのパスを返す
     * GALAXY S3とかパスが違うもので。
     * @return 外部ストレージのパス
     */
    public static String getExternalStragePath() {
         
        String path;
         
        // S3
        path = "/mnt/extSdCard";
        if (new File(path).exists()) {
            return path;
        }
         
        // MOTROLA
        path = System.getenv("EXTERNAL_ALT_STORAGE");
        if (path != null) {
            return path;
        }
         
        // Sumsung
        path = System.getenv("EXTERNAL_STORAGE2");
        if (path != null) {
            return path;
        }
         
        // 旧Sumsung と 標準
        path = System.getenv("EXTERNAL_STORAGE");
        if (path != null) {
            path = Environment.getExternalStorageDirectory().getPath();
        }
         
        // HTC
        File file = new File(path + "/ext_sd");
        if (file.exists()) {
            path = file.getPath();
        }
         
        return path;
    }


}
