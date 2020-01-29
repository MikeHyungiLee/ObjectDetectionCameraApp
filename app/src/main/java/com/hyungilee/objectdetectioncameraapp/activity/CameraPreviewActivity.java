/**
 * @file CameraPreviewActivity.java
 * @brief カメラ機能を起動する画面
 *
 * @author Hyungi Lee(李鉉基)
 * @date 2020/01/26
 */

package com.hyungilee.objectdetectioncameraapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.hyungilee.objectdetectioncameraapp.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.hyungilee.objectdetectioncameraapp.fragment.ObjectListFragment.EXTRA_STRING_SELECT_MENU;
import static com.hyungilee.objectdetectioncameraapp.utility.Constant.EXTRA_STRING_FILE_NAME;
import static com.hyungilee.objectdetectioncameraapp.utility.Constant.EXTRA_STRING_IMAGE_FILE_PATH;
import static com.hyungilee.objectdetectioncameraapp.utility.Constant.EXTRA_STRING_TIME_STAMP;
import static com.hyungilee.objectdetectioncameraapp.utility.Constant.savePath;


/**
 * @brief カメラ機能を起動する画面と関するクラス
 *
 * @par 概要
 * 画面は写真をとるボタンとPreview部分で構成されています。
 * 写真を撮った後で/storage/emulated/NTT-East経路でsaveします。
 * Saveされた写真ファイル名は"yyyyMMdd HHmm＿検査項目名"
 *
 */
public class CameraPreviewActivity extends AppCompatActivity {

    /*出力画像の状態方向を確認します。*/
    private static final SparseIntArray ORIENTATIONS=new SparseIntArray();

    static{
        ORIENTATIONS.append(Surface.ROTATION_0,90);
        ORIENTATIONS.append(Surface.ROTATION_90,0);
        ORIENTATIONS.append(Surface.ROTATION_180,270);
        ORIENTATIONS.append(Surface.ROTATION_270,180);
    }

    private static final int REQUEST_CAMERA_PERMISSION=200;

    /*写真Preview画面を見せるTextureView変数*/
    private TextureView textureView;

    /*写真が保存される時間の情報を保存する変数*/
    String timeStampValue;

    /*ファイル名を含むすべての経路を保存する変数*/
    String path;

    /*保存されるファイル名だけを保存する変数*/
    String imageFileName;

    /*リストビューで選択した項目名を保存する変数*/
    String menu;

    /*Camera装置を使用するためのcameraDevice変数*/
    private CameraDevice cameraDevice;

    /*カメラのキャプチャセッション情報を保存する変数*/
    private android.hardware.camera2.CameraCaptureSession CameraCaptureSession;

    /*Cameraキャプチャを要請するためのbuilder変数*/
    private CaptureRequest.Builder captureRequestBuilder;

    /*写真のサイズ情報を保存する変数*/
    private Size imageDimension;

    private Handler mBackgroundHandler;

    private HandlerThread mBackgroundThread;

    CameraDevice.StateCallback stateCallBack=new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice=camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            cameraDevice=null;
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_preview);

        /* menu barに前の画面に戻るボタンを見せる */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        Intent menu=getIntent();
        String checkmenu=menu.getStringExtra(EXTRA_STRING_SELECT_MENU);

        /* menu barの名前を指定 */
        actionBar.setTitle(getString(R.string.item)+" : "+checkmenu);

        textureView=(TextureView)findViewById(R.id.textureView);

        /*写真を撮るボタン変数*/
        Button btnCapture = findViewById(R.id.btnCapture);

        /* 写真を撮るボタンのイベント処理 */
        btnCapture.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                takePicture();

            }
        });

        assert textureView != null;
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //ネビゲーションの"戻る"ボタンを押すと,検査項目名を保存したまま次の画面を起動します。
        switch(item.getItemId()){
            case android.R.id.home://"戻る"ボタンを押す場合の処理イベント
                Intent intent=new Intent(CameraPreviewActivity.this, SelectObjectActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    /*写真を撮るボタンのイベント処理*/
    private void takePicture(){

        if(cameraDevice==null)
            return;
        CameraManager manager=(CameraManager)getSystemService(Context.CAMERA_SERVICE);

        try{
            CameraCharacteristics characteristics=manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes=null;
            if(characteristics !=null)
                jpegSizes=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        .getOutputSizes(ImageFormat.JPEG);

            //カスタムサイズで画像をキャプチャします。
            int width=640;
            int height=480;
            if(jpegSizes != null && jpegSizes.length>0)
            {
                width=jpegSizes[0].getWidth();
                height=jpegSizes[0].getHeight();
            }
            final ImageReader reader= ImageReader.newInstance(width,height, ImageFormat.JPEG,1);
            List<Surface> outputSurface=new ArrayList<>(2);
            outputSurface.add(reader.getSurface());
            outputSurface.add(new Surface(textureView.getSurfaceTexture()));


            final CaptureRequest.Builder captureBuilder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            /*デバイスの向きを確認します。*/
            int rotation=getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATIONS.get(rotation));

            /*写真を撮った時の時間情報を保存する変数(フォーメット:yyyyMMdd HHmm)*/
            timeStampValue=new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(new Date());

            /*保存するファイル名と拡張子を保存する変数*/
            Intent intent=getIntent();
            menu=intent.getStringExtra(EXTRA_STRING_SELECT_MENU);

            imageFileName=timeStampValue+"_"+menu+getString(R.string.pictureExtension);

            path= savePath +"/"+imageFileName;

            if(!savePath.exists()){
                savePath.mkdir();
            }

            ImageReader.OnImageAvailableListener readerlistener=new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    Image image=null;
                    try{
                        /*ByteBufferで受けた情報をByteArray変数に保存して写真ファイルを保存*/
                        image=reader.acquireLatestImage();
                        ByteBuffer buffer=image.getPlanes()[0].getBuffer();
                        byte[] bytes=new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);

                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                    finally {
                        if(image != null){
                            image.close();
                        }
                    }
                }

                private void save(byte[] bytes)throws IOException {
                    //撮った写真情報を保存するメソッド
                    OutputStream outputStream=null;

                    try{
                        outputStream=new FileOutputStream(path);
                        outputStream.write(bytes);
                    }finally {
                        //もしファイルのOutputStream情報がnullならFileOutputStreamをcloseします。
                        if(outputStream != null){
                            outputStream.close();

                            Intent selectedMenu=getIntent();

                            /*リストビューで選択した項目名を持ってきて,menu変数に保存します。*/
                            String menu=selectedMenu.getExtras().getString(EXTRA_STRING_SELECT_MENU);

                            /*写真チェック画面起動*/
                            Intent intent=new Intent(CameraPreviewActivity.this,PictureCheckActivity.class);

                            /*写真チェック画面起動して同時に,時間情報,選択項目名,ファイル保存位置,保存ファイル名を送ります。*/
                            intent.putExtra(EXTRA_STRING_TIME_STAMP,timeStampValue);
                            intent.putExtra(EXTRA_STRING_SELECT_MENU,menu);
                            intent.putExtra(EXTRA_STRING_IMAGE_FILE_PATH,path);
                            intent.putExtra(EXTRA_STRING_FILE_NAME,imageFileName);

                            startActivity(intent);
                        }
                    }
                }

            };

            reader.setOnImageAvailableListener(readerlistener,mBackgroundHandler);
            final android.hardware.camera2.CameraCaptureSession.CaptureCallback captureListener=new android.hardware.camera2.CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull android.hardware.camera2.CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    createCameraPreview();
                }
            };

            cameraDevice.createCaptureSession(outputSurface, new android.hardware.camera2.CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull android.hardware.camera2.CameraCaptureSession cameraCaptureSession) {

                    try{
                        cameraCaptureSession.capture(captureBuilder.build(),captureListener,mBackgroundHandler);
                    }catch (CameraAccessException e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onConfigureFailed(@NonNull android.hardware.camera2.CameraCaptureSession cameraCaptureSession) {

                }
            },mBackgroundHandler);

        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }


    /*Preview画面を初期化*/
    private void createCameraPreview() {
        try{
            SurfaceTexture texture=textureView.getSurfaceTexture();
            assert texture !=null;
            texture.setDefaultBufferSize(imageDimension.getWidth(),imageDimension.getHeight());
            Surface surface=new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new android.hardware.camera2.CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull android.hardware.camera2.CameraCaptureSession cameraCaptureSession) {
                    if(cameraDevice==null) {
                        return;
                    }
                    CameraCaptureSession=cameraCaptureSession;
                    updatePreview();

                }

                @Override
                public void onConfigureFailed(@NonNull android.hardware.camera2.CameraCaptureSession cameraCaptureSession) {

                }
            },null);

        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }


    /*Preview画面をアップデート*/
    private void updatePreview() {
        if(cameraDevice==null)
            Toast.makeText(this,"エラー", Toast.LENGTH_SHORT).show();

        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);

        try{
            CameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(),null,mBackgroundHandler);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }


    }


    /*カメラ機能を起動します。*/
    private void openCamera(){
        CameraManager manager=(CameraManager)getSystemService(Context.CAMERA_SERVICE);

        try{
            String cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics=manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension=map.getOutputSizes(SurfaceTexture.class)[0];

            //もしAPIバージョンが23の以上だったらrealtime許可を確認します。
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },REQUEST_CAMERA_PERMISSION);
                return;
            }else if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

                /*次ページを起動する前にGPS Permission情報を設定します。*/
                ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
            }

            manager.openCamera(cameraId,stateCallBack,null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    TextureView.SurfaceTextureListener textureListener=new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CAMERA_PERMISSION){

            if(grantResults[0]!= PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permissionMsg, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        startBackgroundThread();
        if(textureView.isAvailable())
            openCamera();
        else
            textureView.setSurfaceTextureListener(textureListener);
    }

    @Override
    protected void onPause() {
        stopBackgroundThread();
        super.onPause();
    }

    private void startBackgroundThread(){
        mBackgroundThread=new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler=new Handler(mBackgroundThread.getLooper());

    }

    private void stopBackgroundThread(){
        mBackgroundThread.quitSafely();

        try{
            mBackgroundThread.join();
            mBackgroundThread=null;
            mBackgroundHandler=null;
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

