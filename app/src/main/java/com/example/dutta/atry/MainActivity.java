package com.example.dutta.atry;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ImageButton androidimageButton;
    Camera camera;
    Camera.Parameters parameters;
    //    CameraCaptureSession cameraCaptureSession;
//    CaptureRequest.Builder builder;
//    CameraDevice cameraDevice;
    CameraManager cameraManager;
    boolean isFlash = false;
    boolean isOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MediaPlayer mp1 = MediaPlayer.create(this, R.raw.charging);
        final MediaPlayer mp2 = MediaPlayer.create(this, R.raw.discharging);
        androidimageButton = (ImageButton) findViewById(R.id.imageButton);
        final Handler handler=new Handler();

        if (Build.VERSION.SDK_INT >= 23) {
            try {
                androidimageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isOn) {
                            try {
                                cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                                mp1.start();
                                if (Build.VERSION.SDK_INT >= 23) {
                                    final String cameraId = cameraManager.getCameraIdList()[0];
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (Build.VERSION.SDK_INT >= 23) {
                                                try {
                                                    cameraManager.setTorchMode(cameraId, true);
                                                } catch (Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    }, 1000);

                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                            }
                            isOn = true;

                        } else {
                            try {
                                cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                                mp2.start();
                                if (Build.VERSION.SDK_INT >= 23) {
                                    final String cameraId = cameraManager.getCameraIdList()[0];
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (Build.VERSION.SDK_INT >= 23) {
                                                try {
                                                    cameraManager.setTorchMode(cameraId, false);
                                                } catch (Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    }, 1000);
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                            }
                            isOn = false;


                        }
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        }else{

                if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                    camera = Camera.open();
                    parameters = camera.getParameters();
                    isFlash = true;
                }
                androidimageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isFlash) {
                            if (!isOn) {
                                mp1.start();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                        camera.setParameters(parameters);
                                        camera.startPreview();
                                        isOn = true;
                                    }
                                },1000);
                            } else {
                                mp2.start();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                        camera.setParameters(parameters);
                                        camera.stopPreview();
                                        isOn = false;
                                    }
                                },1000);
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Umm...");
                            builder.setMessage("Flash isn't available on this device.");
                            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                        }
                    }
                });
            }

        }

    @Override
    protected void onStop() {
        super.onStop();
        if(camera!=null){
            camera.release();
        }
    }
}
