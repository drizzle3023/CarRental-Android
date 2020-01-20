package com.drizzle.carrental.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.opengl.GLException;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.gsm.GsmCellLocation;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ListenableWorker;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.daasuu.camerarecorder.CameraRecordListener;
import com.daasuu.camerarecorder.CameraRecorder;
import com.daasuu.camerarecorder.CameraRecorderBuilder;
import com.daasuu.camerarecorder.LensFacing;
import com.drizzle.carrental.R;
import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.VolleyMultipartRequest;
import com.drizzle.carrental.cameracomponents.PortraitFrameLayout;
import com.drizzle.carrental.cameracomponents.SampleGLView;
import com.drizzle.carrental.enumerators.CoverageState;
import com.drizzle.carrental.globals.AppHelper;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.globals.Utils;
import com.drizzle.carrental.models.Coverage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;


import bg.devlabs.fullscreenvideoview.FullscreenVideoView;
import bg.devlabs.fullscreenvideoview.listener.OnVideoCompletedListener;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

public class BaseCameraActivity extends AppCompatActivity {

    private SampleGLView sampleGLView;
    protected CameraRecorder cameraRecorder;
    private String filepath;
    private Button recordBtn;
    protected LensFacing lensFacing = LensFacing.BACK;
    protected int cameraWidth = 1280;
    protected int cameraHeight = 720;
    protected int videoWidth = 720;
    protected int videoHeight = 1280;
    private AlertDialog filterDialog;
    private boolean toggleClick = false;

    protected Button buttonCancel;

    private BroadcastReceiver mReceiver;

    private Button buttonPlay;

    private ViewGroup.LayoutParams layoutParams = null;

    ProgressDialog progressDialog;

    FullscreenVideoView fullscreenVideoView = null;

    BroadcastReceiver broadcastReceiver;

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreateActivity() {


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        recordBtn = findViewById(R.id.btn_record);
        layoutParams = recordBtn.getLayoutParams();

        recordBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        if (recordBtn.getText().toString().equals(getString(R.string.app_record))) {
                            File file = new File(getVideoFilePath());
                            if (file.exists()) file.delete();

                            filepath = getVideoFilePath();
                            cameraRecorder.start(filepath);
                            recordBtn.setText(getString(R.string.app_record_stop));
                            recordBtn.setLayoutParams(layoutParams);
                            recordBtn.setBackgroundResource(R.drawable.record_start_button);
                            buttonPlay.setVisibility(View.GONE);
                            fullscreenVideoView.setVisibility(View.GONE);
                            FrameLayout frameLayout = findViewById(R.id.wrap_view);
                            frameLayout.setVisibility(View.VISIBLE);
                            sampleGLView.setVisibility(View.VISIBLE);

                        } else if (recordBtn.getText().toString().equals(getString(R.string.app_record_done))) {

                            saveFirstFrameOfVideoAsPng();

                            if (Constants.isRecordingVehicleOrMileOrDamagedPart == 1 || Constants.isRecordingVehicleOrMileOrDamagedPart == 2) {
                                submitCoverageVehicleVideo();
                            } else {
                                backToPreviousActivity();
                            }
                        }


                        break;
                    case MotionEvent.ACTION_UP:

                        cameraRecorder.stop();


                        break;
                    default:
                        break;
                }

                return true;

            }
        });


        buttonPlay = findViewById(R.id.button_play);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPlay.setVisibility(View.GONE);
                if (fullscreenVideoView != null) {
                    fullscreenVideoView.play();
                }
            }
        });


        buttonCancel = findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                setResult(RESULT_CANCELED);
                finish();
            }
        });

        fullscreenVideoView = findViewById(R.id.fullscreenVideoView);
        fullscreenVideoView.setVisibility(View.GONE);
        //fullscreenVideoView.portraitOrientation(PortraitOrientation.DEFAULT);


        setUpCamera();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    private void saveFirstFrameOfVideoAsPng() {

        File file = new File(getVideoFilePath());
        if (!file.exists() || !file.canRead() || !file.isFile()) {
            return;
        }

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(getVideoFilePath());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }


        Bitmap bitmap = retriever.getFrameAtTime();

        File file1 = new File(getImageFilePath());
        if (file1.exists()) file1.delete();
        try {
            FileOutputStream out = new FileOutputStream(file1);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showWaitingScreen() {


        progressDialog.show();
    }

    private void hideWaitingScreen() {

        progressDialog.dismiss();
    }

    private void backToPreviousActivity() {

        setResult(RESULT_OK);
        finish();
    }


    private void submitCoverageVehicleVideo() {

        showWaitingScreen();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(
                Request.Method.POST, Constants.SERVER_HTTP_URL + "/api/add-coverage",
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideWaitingScreen();

                        String res = new String(response.data);
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            JSONObject data = jsonObject.getJSONObject("data");

                            if (jsonObject.getString("success").equals("true")) {

                                Toast.makeText(BaseCameraActivity.this, data.getString("message"), Toast.LENGTH_SHORT).show();
                                Globals.coverage.setId(data.getLong("coverage_id"));
                                if (Constants.isRecordingVehicleOrMileOrDamagedPart == 1) {

                                    Globals.coverage.setUrlVideoVehicle("file://" + getVideoFilePath());
                                    Globals.coverage.setUrlImageVehicle("file://" + getImageFilePath());

                                } else if (Constants.isRecordingVehicleOrMileOrDamagedPart == 2) {

                                    Globals.coverage.setUrlVideoMile("file://" + getVideoFilePath());
                                    Globals.coverage.setUrlImageMile("file://" + getImageFilePath());
                                } else {

                                    Globals.selectedClaim.setVideoURL("file://" + getVideoFilePath());
                                    Globals.selectedClaim.setImageURL("file://" + getImageFilePath());
                                }

                                if (data.getString("token_state").equals("valid")) {

                                    Iterator<String> keys = jsonObject.getJSONObject("data").keys();

                                    for (Iterator i = keys; i.hasNext(); ) {

                                        if (i.next().equals("refresh_token")) {
                                            String newPayload = data.get("refresh_token").toString();
                                            String newToken = data.getString("access_token");

                                            SharedHelper.putKey(BaseCameraActivity.this, "access_token", newToken);
                                            SharedHelper.putKey(BaseCameraActivity.this, "payload", newPayload);

                                            Utils.initHabitSDK(BaseCameraActivity.this);
                                        }
                                    }
                                }

                                backToPreviousActivity();
                            } else if (jsonObject.getString("success").equals("false")){

                                Toast.makeText(BaseCameraActivity.this, data.getString("message"), Toast.LENGTH_SHORT).show();

                                if (data.getString("token_state").equals("invalid")) {

                                    Utils.logout(BaseCameraActivity.this, BaseCameraActivity.this);
                                }


                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BaseCameraActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideWaitingScreen();
                Toast.makeText(BaseCameraActivity.this, getResources().getString(R.string.message_no_response), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramObject = new HashMap<>();

                paramObject.put("access_token", SharedHelper.getKey(BaseCameraActivity.this, "access_token"));
                //paramObject.put("coverage_id", Globals.coverage.getId().toString());
                if (Constants.isRecordingVehicleOrMileOrDamagedPart == 1) {
                    paramObject.put("state", Integer.valueOf(CoverageState.UNCOVERED.getIntValue()).toString());
                } else if (Constants.isRecordingVehicleOrMileOrDamagedPart == 2) {
                    paramObject.put("state", Integer.valueOf(CoverageState.COVERED.getIntValue()).toString());
                }


                return paramObject;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError {
                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();

                if (Constants.isRecordingVehicleOrMileOrDamagedPart == 1) {

                    params.put("video-vehicle", new VolleyMultipartRequest.DataPart("video-vehicle" + Globals.coverage.getId() + ".mp4", AppHelper.getFileDataFromUri(getVideoFilePath()), "video/mp4"));
                    params.put("image-vehicle", new VolleyMultipartRequest.DataPart("image-vehicle" + Globals.coverage.getId() + ".png", AppHelper.getFileDataFromUri(getImageFilePath()), "image/png"));


                } else if (Constants.isRecordingVehicleOrMileOrDamagedPart == 2) {

                    params.put("video-mile", new VolleyMultipartRequest.DataPart("video-mile" + Globals.coverage.getId() + ".mp4", AppHelper.getFileDataFromUri(getVideoFilePath()), "video/mp4"));
                    params.put("image-mile", new VolleyMultipartRequest.DataPart("image-mile" + Globals.coverage.getId() + ".png", AppHelper.getFileDataFromUri(getImageFilePath()), "image/png"));
                } else {
                    params.put("video", new VolleyMultipartRequest.DataPart("video-claim" + ".mp4", AppHelper.getFileDataFromUri(getVideoFilePath()), "video/mp4"));
                    params.put("image", new VolleyMultipartRequest.DataPart("image-claim" + ".png", AppHelper.getFileDataFromUri(getImageFilePath()), "image/png"));
                }
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.CONNECTION_TIMEOUT * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(volleyMultipartRequest);
    }

    @Override
    public void onBackPressed() {

        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }


    @Override
    protected void onResume() {

        super.onResume();

//        IntentFilter intentFilter = new IntentFilter(
//                "android.intent.action.ACTION_MEDIA_SCANNER_SCAN_FILE");
//
//        mReceiver = new BroadcastReceiver() {
//
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                //extract our message from intent
//                Toast.makeText(BaseCameraActivity.this, "Apple", Toast.LENGTH_SHORT).show();
//                String msg_for_me = intent.getStringExtra("some_msg");
//                //log our message value
//                Log.i("InchooTutorial", msg_for_me);
//
//            }
//        };
//        //registering our receiver
//        this.registerReceiver(mReceiver, intentFilter);

        if (buttonPlay.getVisibility() != View.VISIBLE) {
            setUpCamera();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
    }

    private void releaseCamera() {
        if (sampleGLView != null) {
            sampleGLView.onPause();
        }

        if (cameraRecorder != null) {
            cameraRecorder.stop();
            cameraRecorder.release();
            cameraRecorder = null;
        }

        if (sampleGLView != null) {
            ((FrameLayout) findViewById(R.id.wrap_view)).removeView(sampleGLView);
            sampleGLView = null;
        }
    }


    private void setUpCameraView() {
        runOnUiThread(() -> {
            FrameLayout frameLayout = findViewById(R.id.wrap_view);
            frameLayout.removeAllViews();
            sampleGLView = null;
            sampleGLView = new SampleGLView(getApplicationContext());
            sampleGLView.setTouchListener(new SampleGLView.TouchListener() {
                @Override
                public void onTouch(MotionEvent event, int width, int height) {
                    if (cameraRecorder == null) return;
                    cameraRecorder.changeManualFocusPoint(event.getX(), event.getY(), width, height);
                }
            });
            frameLayout.addView(sampleGLView);
        });
    }


    private void setUpCamera() {
        setUpCameraView();

        cameraRecorder = new CameraRecorderBuilder(this, sampleGLView)
                //.recordNoFilter(true)
                .cameraRecordListener(new CameraRecordListener() {

                    @Override
                    public void onGetFlashSupport(boolean flashSupport) {

                    }

                    @Override
                    public void onRecordComplete() {
                        exportMp4ToGallery(getApplicationContext(), filepath);

                        ProgressDialog progressDialog = new ProgressDialog(BaseCameraActivity.this);
                        progressDialog.setMessage("Please wait...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        DisplayMetrics metrics = getResources().getDisplayMetrics();
                        ViewGroup.LayoutParams buttonLayoutParams = recordBtn.getLayoutParams();
                        buttonLayoutParams.width = (int) (metrics.density * 120);
                        buttonLayoutParams.height = (int) (metrics.density * 40);


                        recordBtn.setText(getString(R.string.app_record_done));
                        recordBtn.setLayoutParams(buttonLayoutParams);
                        recordBtn.setBackgroundResource(R.drawable.active_button);
                        buttonPlay.setVisibility(View.VISIBLE);

                        fullscreenVideoView.setVisibility(View.VISIBLE);


                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                progressDialog.hide();

                                File file = new File(getVideoFilePath());
                                if (!file.exists() || !file.canRead() || !file.isFile()) {
                                    return;
                                }

                                fullscreenVideoView.videoUrl("file://" + getVideoFilePath()).addOnVideoCompletedListener(new OnVideoCompletedListener() {
                                    @Override
                                    public void onFinished() {

                                        sampleGLView.setVisibility(View.GONE);

                                        buttonPlay.setVisibility(View.VISIBLE);

                                    }
                                });

                            }
                        }, 1000);
                    }

                    @Override
                    public void onRecordStart() {

                    }

                    @Override
                    public void onError(Exception exception) {
                        Log.e("CameraRecorder", exception.toString());
                    }

                    @Override
                    public void onCameraThreadFinish() {
                        if (toggleClick) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setUpCamera();
                                }
                            });
                        }
                        toggleClick = false;
                    }
                })
                .videoSize(videoWidth, videoHeight)
                .cameraSize(cameraWidth, cameraHeight)
                .lensFacing(lensFacing)
                .build();


    }


    private interface BitmapReadyCallbacks {
        void onBitmapReady(Bitmap bitmap);
    }

    private void captureBitmap(final BitmapReadyCallbacks bitmapReadyCallbacks) {
        sampleGLView.queueEvent(new Runnable() {
            @Override
            public void run() {
                EGL10 egl = (EGL10) EGLContext.getEGL();
                GL10 gl = (GL10) egl.eglGetCurrentContext().getGL();
                Bitmap snapshotBitmap = BaseCameraActivity.this.createBitmapFromGLSurface(sampleGLView.getMeasuredWidth(), sampleGLView.getMeasuredHeight(), gl);

                BaseCameraActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bitmapReadyCallbacks.onBitmapReady(snapshotBitmap);
                    }
                });
            }
        });
    }

    private Bitmap createBitmapFromGLSurface(int w, int h, GL10 gl) {

        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(0, 0, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2, texturePixel, blue, red, pixel;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    texturePixel = bitmapBuffer[offset1 + j];
                    blue = (texturePixel >> 16) & 0xff;
                    red = (texturePixel << 16) & 0x00ff0000;
                    pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            Log.e("CreateBitmap", "createBitmapFromGLSurface: " + e.getMessage(), e);
            return null;
        }

        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }

    public void saveAsPngImage(Bitmap bitmap, String filePath) {
        try {
            File file = new File(filePath);
            FileOutputStream outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void exportMp4ToGallery(Context context, String filePath) {
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, filePath);
        context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                values);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + filePath)));
    }


    public static String getVideoFilePath() {

        //return getAndroidMoviesFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "cameraRecorder.mp4";
        String filePath;
        if (Constants.isRecordingVehicleOrMileOrDamagedPart == 1) {

            filePath = getAndroidMoviesFolder().getAbsolutePath() + "/" + Constants.VEHICLE_VIDEO_FILE_NAME;
        } else if (Constants.isRecordingVehicleOrMileOrDamagedPart == 2) {

            filePath = getAndroidMoviesFolder().getAbsolutePath() + "/" + Constants.MILE_VIDEO_FILE_NAME;
        } else {
            filePath = getAndroidMoviesFolder().getAbsolutePath() + "/" + Constants.DAMAGED_VIDEO_FILE_NAME;
        }

        return filePath;
    }

    public static File getAndroidMoviesFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
    }

    private static void exportPngToGallery(Context context, String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static String getImageFilePath() {

        String filePath;
        if (Constants.isRecordingVehicleOrMileOrDamagedPart == 1) {

            filePath = getAndroidImageFolder().getAbsolutePath() + "/" + Constants.VEHICLE_IMAGE_FILE_NAME;
        } else if (Constants.isRecordingVehicleOrMileOrDamagedPart == 2) {

            filePath = getAndroidImageFolder().getAbsolutePath() + "/" + Constants.MILE_IMAGE_FILE_NAME;
        } else {

            filePath = getAndroidImageFolder().getAbsolutePath() + "/" + Constants.DAMAGED_IMAGE_FILE_NAME;
        }
        return filePath;
    }

    public static File getAndroidImageFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    }
}
