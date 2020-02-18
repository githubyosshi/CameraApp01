package com.bird_brown.cameraapp01;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

@SuppressWarnings("deprecation")
public class CameraView extends SurfaceView implements Callback, PictureCallback {
    private Camera camera; //カメラ

    public CameraView(Context context) {
        super(context);

        //SurfaceHolderを取得
        SurfaceHolder holder = getHolder();

        //コールバック・オブジェクトとして登録
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //カメラのオブジェクトを生成
        camera = Camera.open();

        //カメラのプレビューにSurfaceHolderを設定
        try {
            camera.setPreviewDisplay(holder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //カメラのプレビューを停止
        camera.stopPreview();

        //カメラのパラメータ（設定値）を取得
        Camera.Parameters params = camera.getParameters();

        //パラメータにプレビューサイズ（width, height）を設定
        params.setPreviewSize(width, height);

        //カメラにパラメータを設定
        camera.setParameters(params);

        //カメラのプレビューを開始
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //カメラのプレビューを停止
        camera.stopPreview();

        //カメラを開放
        camera.release();

        //カメラをnullにする
        camera = null;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        //byte配列として撮影された画像データをBitmapに変換して取得
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        //Contextを取得
        Context context = getContext();

        //Bitmap画像をSDカードに保存
        ContentResolver resolver = context.getContentResolver();
        MediaStore.Images.Media.insertImage(resolver, bitmap, "", null);

        //Cameraのプレビューを開始
        camera.startPreview();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //カメラで撮影
            camera.takePicture(null, null, this);
        }

        return true;
    }
}
