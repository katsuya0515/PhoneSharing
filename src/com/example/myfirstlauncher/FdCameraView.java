package com.example.myfirstlauncher;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
class FdCameraView extends SurfaceView implements SurfaceHolder.Callback {
	 
    private Activity mOwner;    // オーナーアクティビティ(画面の回転処理などに使う)
    private Camera mCamera; // カメラ
    private SurfaceHolder mHolder;  // サーフェイスホルダー
    Face[] detectedFaces;
    PersonRecognizer fr;
    labels labelsFile;
    String mPath="";
    ImageView checkimgImageView;
    TextView detectedName;
    boolean facedetected=false;
    /**
     * コンストラクタ
     * @param context
     */
    public FdCameraView(Context context) {
        super(context);
         
        // サーフェイスホルダーをとっとく
        this.mHolder = this.getHolder();
        this.mHolder.addCallback(this);
        this.mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    public FdCameraView(Context context, SurfaceView sv,ImageView imageView,String path,TextView tv) {
        super(context);
         
        
        // サーフェイスホルダーをとっとく
       this.mHolder = sv.getHolder();
       this.mHolder.addCallback(this);
       this.mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
       checkimgImageView=imageView;
       detectedName=tv;
       mPath=path;
    }
     

    /**
     * アクティビティをセットする
     * @param activity
     */
    public void setOwner(Activity activity) {
        this.mOwner = activity;
        
    }
    
    
    
     
    /**
     * サーフェイスが作られたときの呼び出し
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // カメラを開く
            this.mCamera = Camera.open(1);
             
            // プレビューディスプレイの設定
            this.mCamera.setPreviewDisplay(this.mHolder);
          
            labelsFile= new labels(mPath);
            
            fr=new PersonRecognizer(mPath);
            fr.load();
           
             
        } catch (Exception ep) {
            ep.printStackTrace();
             
            // 失敗したときはカメラを解放する
            this.mCamera.release();
            this.mCamera = null;
        }
    }

     // サーフェイスが破棄されたときに呼び出される
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
         
        // プレビューを停止
        this.mCamera.stopPreview();   
        this.mCamera.setPreviewCallback(null);
        
        // カメラを解放
        this.mCamera.release();
        this.mCamera = null;
     
    }
 
     
    /**
     * サーフェスの変更時の呼び出し
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
         
        // 何かする前に一度プレビューを停止する
        this.mCamera.stopPreview();
        mCamera.setPreviewCallback(captureCamera); 
        // カメラパラメータを取り出す
        Camera.Parameters params = this.mCamera.getParameters();
 
        // ベストなプレビューサイズを探す
        // 
        // 端末のサポートしているプレビューサイズを取り出す
        List<Size> listSize = params.getSupportedPreviewSizes();
         
        // 一番つ劣化が少ない画像のサイズ
        Size bestPrevSize = this.getBestPreviewSize(listSize, width, height);
         
        // プレビューサイズを設定する
        params.setPreviewSize(bestPrevSize.width, bestPrevSize.height);
         
        // ベストな保存サイズを取り出す
        //
         
        // 端末のサポートしてる画像サイズを取り出す
        listSize = params.getSupportedPictureSizes();
         
        // ベストな画像サイズを調べる
        Size bestPictureSize = this.getBestPreviewSize(listSize, width, height);
         
        // 画像サイズを設定する
        params.setPictureSize(bestPictureSize.width, bestPictureSize.height);
 
        // カメラの回転角度をセットする
        FdCameraView.setCameraDisplayOrientation(this.mOwner, 0, this.mCamera);
         
        // オートフォーカスの設定
      //  params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
         
        // パラメーターの更新
        this.mCamera.setParameters(params);
        
        // 顔検出用のリスナーを登録する
        mCamera.setFaceDetectionListener(getFace);
        
        String TAG="faces";
     // カメラに顔検出開始を指示する
        try {
            mCamera.startFaceDetection();
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "IllegalArgumentException.");
        } catch (RuntimeException e) {
            Log.e(TAG, "the method fails or the face detection is already running.");
        }
        // プレビューを再開
        this.mCamera.startPreview();
        
        // プレビューが再開したらオートフォーカスの設定(プレビュー中じゃないときにオートフォーカス設定すると落ちる)
        //this.mCamera.autoFocus(null);
    }
   
    
    private Camera.FaceDetectionListener getFace= new Camera.FaceDetectionListener() {	
		@Override
		public void onFaceDetection(Face[] faces, Camera camera) {
			if (faces.length == 0)facedetected=false;
		   else facedetected=true;
			}
    };
    
    private Camera.PreviewCallback captureCamera = new Camera.PreviewCallback() {
		
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			
		    	 Camera.Parameters parameters = camera.getParameters();

		         int width = parameters.getPreviewSize().width;
		         int height = parameters.getPreviewSize().height;
		       
		         if(facedetected){
		         ByteArrayOutputStream outstr = new ByteArrayOutputStream();
		         Rect rect = new Rect(0, 0, width, height); 
		         YuvImage yuvimage=new YuvImage(data,ImageFormat.NV21,width,height,null);
		         yuvimage.compressToJpeg(rect, 100, outstr);
		         Bitmap bmp = BitmapFactory.decodeByteArray(outstr.toByteArray(), 0, outstr.size());
		      // 回転マトリックス作成（90度回転）  
		         Matrix mat = new Matrix();  
		         mat.postRotate(-90);
		         
		         // 回転したビットマップを作成  
		         Bitmap bmp2 = Bitmap.createBitmap(bmp, 0, 0, width, height, mat, true); 
		         
		         Matrix mat2 = new Matrix();  
		         mat2.preScale(-1, 1);
		         
		         // 回転したビットマップを作成  
		         Bitmap bmp3 = Bitmap.createBitmap(bmp2, 0, 0, height, width, mat2, true); 
		         
		       
		         Mat m=new Mat();
		     Utils.bitmapToMat(bmp3, m);
		     Bitmap mBitmap = Bitmap.createBitmap(height,width, Bitmap.Config.ARGB_8888);
		    Utils.matToBitmap(m, mBitmap);
		    checkimgImageView.setImageBitmap(mBitmap);
		     // Log.i("FaceDetect" ,fr.predict(m));
		       detectedName.setText(fr.predict(m));
		    
		    	}else{
		    		  Bitmap mBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
		    		  checkimgImageView.setImageBitmap(mBitmap);
		    		  detectedName.setText(null);
		    	}
		}
	};
    
  
    private final float ASPECT_TOLERANCE = 0.05f;
     
    /**
     * 理想に近いサイズをプレビューサイズの中から探し出す
     * @param listPreviewSize サポートされているプレビューのサイズ
     * @param w 画面幅
     * @param h 画面の高さ
     * @return 適したプレビューサイズ
     */
    private Size getBestPreviewSize(List<Size> listPreviewSize, int w, int h) {
         
        // プレビューサイズリストがなかったときは何もしない
        if (listPreviewSize == null) {
            return null;
        }
         
        // 端末が立った状態の場合はWとHを入れ替える
        if (w < h) {
            int tmp = w;
            w = h;
            h = tmp;
        }
         
        float bestRatio = (float)w / h; // この比率に近いものをリストから探す
        float minHeightDiff = Float.MAX_VALUE;  // 一番高さに差がないもの
        int bestHeight = h; // プレビュー画面にベストな高さ
        float currRatio = 0;    // 今見ているもののアスペクト比
        Size bestSize = null;
         
        // 近いサイズのものを探す
        for (Size curr : listPreviewSize) {
             
            // 今見ているもののアスペクト比
            currRatio = (float)curr.width / curr.height;
             
            // 許容範囲を超えちゃってるやつは無視
            if (ASPECT_TOLERANCE < Math.abs(currRatio - bestRatio)) {
                continue;
            }
             
            // 前に見たやつより高さの差が少ない
            if (Math.abs(curr.height - bestHeight) < minHeightDiff) {
                 
                // 一番いいサイズの更新
                bestSize = curr;
                 
                // 今のところこれが一番差が少ない
                minHeightDiff = Math.abs(curr.height - bestHeight);
            }
        }
         
        // 理想的なものが見つからなかった場合、しょうがないので画面に入るようなやつを探しなおす
        if (bestSize == null) {
             
            // でっかい値をいれとく（未使用です）
            minHeightDiff = Float.MAX_VALUE;
 
            // 今度は画面に入りそうなものを探す
            for (Size curr : listPreviewSize) {
                 
                // 今見ているもののアスペクト比
                currRatio = (float)curr.width / curr.height;
                 
                // 前に見たやつより高さの差が少ない
                if (Math.abs(curr.height - bestHeight) < minHeightDiff) {
                     
                    // 一番いいサイズの更新
                    bestSize = curr;
                     
                    // 今のところこれが一番差が少ない
                    minHeightDiff = Math.abs(curr.height - bestHeight);
                }
            }
        }
         
        return bestSize;
    }
     
     
    /**
     * 画面の回転角度を設定する
     * @param activity アクティビティ
     * @param cameraId カメラID
     * @param camera カメラ
     */
    public static void setCameraDisplayOrientation(
            Activity activity, int cameraId, android.hardware.Camera camera) {
         
        // 向きを設定
        camera.setDisplayOrientation(FdCameraView.getCameraDisplayOrientation(activity));
    }
     
    /**
     * 画面の回転角度を取り出す
     * @param activity アクティビティ
     * @return 画面の回転角度
     */
    public static int getCameraDisplayOrientation(Activity activity) {
         
        // ディスプレイの回転角を取り出す
        int rot = activity.getWindowManager().getDefaultDisplay().getRotation();
         
        // 回転のデグリー角
        int degree = 0;
         
        // 取り出した角度から実際の角度への変換
        switch (rot) {
        case Surface.ROTATION_0:    degree = 0;     break;
        case Surface.ROTATION_90:   degree = 90;    break;
        case Surface.ROTATION_180:  degree = 180;   break;
        case Surface.ROTATION_270:  degree = 270;   break;
        }
         
        // 背面カメラだけの処理になるけど、画像を回転させて縦持ちに対応
        return (90 + 360 - degree) % 360;
    }
     
     
}