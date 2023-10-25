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