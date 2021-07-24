package com.baekgol.reactnativealarmmanager.util;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class AlarmService extends Service {
    private String packageName;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;
    private final String channelId = "alarm";

    private Class getMainActivity(){
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        String className = intent.getComponent().getClassName();

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        packageName = getPackageName();

        if(mediaPlayer!=null) {
            vibrator.cancel();
            mediaPlayer.stop();
        }

        Intent notiIntent = new Intent(this, getMainActivity());
        notiIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notiIntent.putExtra("id", intent.getIntExtra("id", 0));
        notiIntent.putExtra("name", intent.getStringExtra("name"));
        notiIntent.putExtra("hour", intent.getIntExtra("hour", 0));
        notiIntent.putExtra("minute", intent.getIntExtra("minute", 0));
        notiIntent.putExtra("isActivate", true);

        PendingIntent notiPendingIntent = PendingIntent.getActivity(this, 0, notiIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //          Resources res = reactContext.getResources();
//          String packageName = reactContext.getPackageName();
//          int smallIconResId = res.getIdentifier("ic_launcher", "mipmap", packageName);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
//                    .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("일어나")
                .setContentText("일어날 시간입니다.")
                .setContentIntent(notiPendingIntent)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        this.startForeground(1, builder.build());

        if(intent.getBooleanExtra("vibration", true)){
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VibrationEffect.createWaveform(new long[]{1000, 500}, new int[]{0, 50}, 0));
        }

        int resId = this.getResources().getIdentifier(intent.getStringExtra("sound"), "raw", packageName);
        mediaPlayer = MediaPlayer.create(this, resId);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mediaPlayer!=null) {
            if(vibrator!=null) vibrator.cancel();
            mediaPlayer.release();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
