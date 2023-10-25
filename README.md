# swRecordVideo
仿微信短视频录制
# 效果图
![image](image/1.png)
![image](image/2.png)
# 1 如何引入自己的项目
## 1.1 下载源码 
https://github.com/qiangzhouliang/Swan_record
## 1.2 在自己的工厂引入 SW_Record_Video module

## 1.3 添加依赖项
settings.gradle
~~~
include(":SW_Record_Video")
~~~
~~~
dependencies {
	  implementation(project(":SW_Record_Video"))
}
~~~
# 2 如何使用
## 2.1 xml
~~~
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <com.swan.sw_record_video.camera.widget.CameraView
        android:id="@+id/surface_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <com.swan.sw_record_video.camera.widget.CameraFocusView
        android:id="@+id/camera_focus_view"
        android:layout_width="100dp"
        android:layout_height="100dp"
        app:stroke_width="5dp" />

    <com.swan.sw_record_video.record.widget.RecordProgressButton
        android:id="@+id/record_button"
        android:layout_width="150dp"
        android:layout_height="150dp"
        android:layout_marginBottom="40dp"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true" />

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        android:onClick="recordVideo"
        android:text="录制"
        android:visibility="gone" />
</RelativeLayout>
~~~
## 2.2 在要使用的地方写上如下代码
- 1 java 代码中
~~~
package com.swan.swan_record;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.swan.sw_record_video.camera.widget.CameraFocusView;
import com.swan.sw_record_video.camera.widget.CameraView;
import com.swan.sw_record_video.record.DefaultVideoRecorder;
import com.swan.sw_record_video.record.widget.RecordProgressButton;
import com.swan.swan_record.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private CameraView mCameraView;
    private CameraFocusView mFocusView;
    private DefaultVideoRecorder mVideoRecorder;
    private RecordProgressButton mRecordButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getPermission();

        mCameraView = binding.surfaceView;
        mFocusView = binding.cameraFocusView;
        mRecordButton = binding.recordButton;
        mRecordButton.setMaxProgress(60000);

        mRecordButton.setOnRecordListener(new RecordProgressButton.RecordListener() {
            @Override
            public void onStart() {
                mVideoRecorder = new DefaultVideoRecorder(MainActivity.this, mCameraView.getEglContext(),
                    mCameraView.getTextureId());
                mVideoRecorder.initVideo(MainActivity.this,
                    getApplication().getFilesDir().getPath() + "/live_pusher.mp4",
                    720, 1280);
                mVideoRecorder.start();
                mVideoRecorder.setOnRecordListener(times ->{
                    mRecordButton.setCurrentProgress((int)times);
                    //Log.e("TAG", "录制了： "+times);
                });
            }

            @Override
            public void onEnd() {
                mVideoRecorder.stopRecord();
            }
        });
        // 聚焦监听
        mCameraView.setOnFocusListener(new CameraView.FocusListener() {
            @Override
            public void beginFocus(int x, int y) {
                mFocusView.beginFocus(x, y);
            }

            @Override
            public void endFocus(boolean success) {
                mFocusView.endFocus(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraView.onDestroy();
        if (mVideoRecorder != null) {
            mVideoRecorder.stopRecord();
        }
    }

    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO}, 1);
        }
    }
}
~~~

# 3 版本更新说明
## 3.1 1.0.0 方微信短视频拍摄



