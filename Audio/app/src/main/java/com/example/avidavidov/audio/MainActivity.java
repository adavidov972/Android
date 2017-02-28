package com.example.avidavidov.audio;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends Activity {

    public static final int REQUEST_CODE = 123;
    Button btnStartRecording;
    AudioRecorder audioRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartRecording = (Button)
                findViewById(R.id.btnStartRecording);
        audioRecorder = new AudioRecorder();
        audioRecorder.setExternalStorageDir(getExternalFilesDir(null));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        for (int grantResult : grantResults){
            if (grantResult != PackageManager.PERMISSION_GRANTED)
                return;
        }
        toggleRecording();
    }

    public void btnStartRecording(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            ArrayList<String> permissions = new ArrayList<>();
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) !=
            PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            else {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (permissions.size()>0){
                    String [] permissionsString = new String[permissions.size()];
                    permissions.toArray(permissionsString);
                    requestPermissions((String[]) permissions.toArray(), REQUEST_CODE);
                    return;
                }
            }
        }
        toggleRecording();
    }

    private void toggleRecording() {
        if (!audioRecorder.isRecording()) {
            audioRecorder.start();
            btnStartRecording.setText("stop recording");
        } else {
            audioRecorder.stop();
            btnStartRecording.setText("start recording");
        }
    }

    public void btnStartPlaying(View view) {
        audioRecorder.play();
    }
}

