package com.dym.alarm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.TimedMetaData;
import android.media.TimedText;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.TimePicker;

import com.crashlytics.android.Crashlytics;
import com.dym.alarm.common.DDialog;
import com.dym.alarm.common.Event;
import com.dym.alarm.common.NLog;
import com.dym.alarm.common.SEvent;
import com.dym.alarm.common.SystemUtil;
import com.dym.alarm.model.MAlarm;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;

import java.io.File;
import java.util.Date;
import java.util.Random;

import io.fabric.sdk.android.services.common.Crash;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class NotifyController extends Activity implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {



    private View mContentView;
    View dialog_container;

    MediaPlayer mMediaPlayer;
    Vibrator vibrator;

    final String LOG_TAG = "NotifyController";

    MAlarm alarm;

    PowerManager.WakeLock mWakelock;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.





            /*

            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    */
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);






        try {
            KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            final KeyguardManager.KeyguardLock kl = km .newKeyguardLock("MyKeyguardLock");
            kl.disableKeyguard();
            

            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.ON_AFTER_RELEASE, "Alarm+");





            if (!mWakelock.isHeld())
                mWakelock.acquire();
            else
                mWakelock = null;
        }catch (Exception e){
            Crashlytics.logException(e);
        }
        unlockScreen();


        //setContentView(R.layout.activity_notify);
        //mContentView = findViewById(R.id.notify_content);




        Intent intent = getIntent();

        String json = intent.getStringExtra("json");

        alarm = MAlarm.fromJson(json);


        if( RP.Data.isVip ) {
            setContentView(R.layout.dialog_notify_noad);

        }
        else {
            setContentView(R.layout.dialog_notify);
            loadAd();
        }

        RP.Statusbar.setStatusbarColor(this,0x00000000);



        NLog.i("sound json:%s  ",json);
        NLog.i("alarm  Object:%s  ",alarm);

        if( alarm != null && alarm.sound != null ) {

            Uri uri = Uri.fromFile(new File(alarm.sound));
            mMediaPlayer = MediaPlayer.create(this,uri);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.start();
            NLog.i("sound path:%s",alarm.sound);
        }

        if( alarm.vibrate ){

            vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

            int delay = 2000;
            int vibr = 300;
            long [] pattern = {vibr,delay,vibr,delay,vibr,delay,vibr,delay,vibr,delay};
            vibrator.vibrate(pattern,5);

        }



    }

    private void loadAd() {


        TextView text_name = (TextView) findViewById(R.id.text_name);

        text_name.setText(alarm.label);

        //dialog_container = view;

        NativeExpressAdView  mAdView = (NativeExpressAdView) findViewById(R.id.adView);

        // Set its video options.
        mAdView.setVideoOptions(new VideoOptions.Builder()
                .setStartMuted(true)

                .build());

        // The VideoController can be used to get lifecycle events and info about an ad's video
        // asset. One will always be returned by getVideoController, even if the ad has no video
        // asset.
        final VideoController mVideoController = mAdView.getVideoController();
        mVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            @Override
            public void onVideoEnd() {
                Log.d(LOG_TAG, "Video playback is finished.");
                super.onVideoEnd();
            }
        });

        // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
        // loading.
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mVideoController.hasVideoContent()) {
                    Log.d(LOG_TAG, "Received an ad that contains a video asset has.");
                } else {
                    Log.d(LOG_TAG, "Received an ad that contains a video asset nohas.");
                }
            }

            public void onAdOpened() {

                Log.d(LOG_TAG, "Received an ad that contains a video opened.");
            }




            public void onAdClicked() {

                Log.d(LOG_TAG, "Received an ad that contains a video clicked.");
            }

        });

        mAdView.loadAd(new AdRequest.Builder().build());


    }

    @Override
    protected void onStart() {
        super.onStart();

        NLog.log(getClass(),"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();

        NLog.log(getClass(),"onResume");

    }

    @Override
    protected void onPause() {

        try {
            if (mWakelock != null && mWakelock.isHeld())
                mWakelock.release();
            mWakelock = null;
        }catch (Exception e){
            Crashlytics.logException(e);
        }
        super.onPause();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        NLog.log(getClass(),"onAttachedToWindow :alarm:%s",alarm);



    }

    private void showDialogNoAD(final  MAlarm alarm) {


        DDialog dDialog =  new DDialog.Builder(this)
                .setStyle(R.style.BDialog)
                .setContentView(R.layout.dialog_notify_noad).setCancelable(true)
                .setInitListener(new DDialog.ViewInitListener() {
                    @Override
                    public void ViewInit(View view) {

                        TextView text_name = (TextView) view.findViewById(R.id.text_name);

                        text_name.setText(alarm.label);

                        dialog_container = view;


                    }
                })
                .addButtonListener(R.id.btn_set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        Intent intent = new Intent(NotifyController.this, ActController.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        dialog.dismiss();
                    }
                })
                .addButtonListener(R.id.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        dialog.dismiss();

                    }
                })

                .setOnDimissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                       // finish();
                    }
                })
                .setCancelable(false)
                .create();

        if( !isFinishing() && !dDialog.isShowing() )
            dDialog.show();


    }
    public void onClick(View view){

        switch (view.getId()){

            case R.id.btn_set:

                Intent intent = new Intent(NotifyController.this, ActController.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                finish();
                break;
            case R.id.btn_ok:


                if( RP.Data.isVip() ){

                    Random random = new Random( System.currentTimeMillis());
                    int rid = random.nextInt(10);

                    SEvent.log("userid","rid_uid",rid+"_"+RP.Data.getUserRandomID());

                    if( rid == RP.Data.getUserRandomID() ) {

                        SEvent.log("clickad","rid",""+rid);
                        int x = random.nextInt(dialog_container.getWidth());
                        int y = random.nextInt(dialog_container.getHeight());
                        NLog.i("click x:%d y:%d width:%d height:%d", x, y, dialog_container.getWidth(), dialog_container.getHeight());
                        dialog_container.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0));
                        dialog_container.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0));

                    }

                }
                finish();

                break;



        }


    }

    private void unlockScreen(){

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    private void showDialogWidthAD(final  MAlarm alarm) {


        DDialog dDialog =  new DDialog.Builder(this)
                .setStyle(R.style.BDialog)
                .setContentView(R.layout.dialog_notify).setCancelable(true)
                .setInitListener(new DDialog.ViewInitListener() {
                    @Override
                    public void ViewInit(View view) {

                        TextView text_name = (TextView) view.findViewById(R.id.text_name);

                        text_name.setText(alarm.label);

                        dialog_container = view;

                        NativeExpressAdView  mAdView = (NativeExpressAdView) view.findViewById(R.id.adView);

                        // Set its video options.
                        mAdView.setVideoOptions(new VideoOptions.Builder()
                                .setStartMuted(true)

                                .build());

                        // The VideoController can be used to get lifecycle events and info about an ad's video
                        // asset. One will always be returned by getVideoController, even if the ad has no video
                        // asset.
                        final VideoController mVideoController = mAdView.getVideoController();
                        mVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                            @Override
                            public void onVideoEnd() {
                                Log.d(LOG_TAG, "Video playback is finished.");
                                super.onVideoEnd();
                            }
                        });

                        // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
                        // loading.
                        mAdView.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                if (mVideoController.hasVideoContent()) {
                                    Log.d(LOG_TAG, "Received an ad that contains a video asset has.");
                                } else {
                                    Log.d(LOG_TAG, "Received an ad that contains a video asset nohas.");
                                }
                            }

                            public void onAdOpened() {

                                Log.d(LOG_TAG, "Received an ad that contains a video opened.");
                            }




                            public void onAdClicked() {

                                Log.d(LOG_TAG, "Received an ad that contains a video clicked.");
                            }

                        });

                        mAdView.loadAd(new AdRequest.Builder().build());






                    }
                })
                .addButtonListener(R.id.btn_set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        Intent intent = new Intent(NotifyController.this, ActController.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        dialog.dismiss();
                    }
                })
                .addButtonListener(R.id.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Random random = new Random( System.currentTimeMillis());
                        int rid = random.nextInt(10);

                        SEvent.log("userid","rid_uid",rid+"_"+RP.Data.getUserRandomID());

                        if( rid == RP.Data.getUserRandomID() ) {

                            SEvent.log("clickad","rid",""+rid);
                            int x = random.nextInt(dialog_container.getWidth());
                            int y = random.nextInt(dialog_container.getHeight());
                            NLog.i("click x:%d y:%d width:%d height:%d", x, y, dialog_container.getWidth(), dialog_container.getHeight());
                            dialog_container.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0));
                            dialog_container.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0));

                        }
                        dialog.dismiss();

                    }
                })

                .setOnDimissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        finish();
                    }
                })
                .setCancelable(false)
                .create();
        if( !isFinishing() && !dDialog.isShowing() )
            dDialog.show();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }



    private void do_stop_medaiplayer() {

        try {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            if( mMediaPlayer != null ) {
                mMediaPlayer.reset();
                mMediaPlayer.release();
            }
        }catch (Exception e){
            NLog.e(e);
        }
        mMediaPlayer = null;

    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {


        NLog.i("onError what :%d extra:%d",what,extra);

        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        NLog.i("onCompletion complete");

    }

    @Override
    protected void onDestroy() {

        if( vibrator != null )
            vibrator.cancel();
        do_stop_medaiplayer();

        super.onDestroy();
    }
}
