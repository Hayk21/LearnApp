package com.hayk.learnapp.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.hayk.learnapp.R;
import com.hayk.learnapp.activitys.MainActivity;
import com.hayk.learnapp.adapter.MediaItem;
import com.hayk.learnapp.other.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class MediaPlayerService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static boolean isRunning = false;
    private static boolean isPlaying = false;
    public static final String MEDIA_FINISHED = "MediaFinished";
    public static final String MEDIA_ROOT = "MediaRoot";
    public static final String MEDIA_PLAY = "MediaPlay";
    public static final String MEDIA_PAUSE = "MediaPause";
    public static final String MEDIA_CLOSE = "MediaClose";
    public static final String MEDIA_ACTION = "MediaAction";
    public static final String MEDIA_RECEIVER_ACTION = "MediaReceiverAction";
    private MediaPlayer mediaPlayer;
    private static int bindCounts = 0;
    private static String currentFile = "";
    private int pauseMilis;
    private String mediaName;
    private MediaReceiver mediaReceiver;
    private NotificationCompat.Builder builder;
    private Bitmap fileImage = null;
    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaReceiver = new MediaReceiver();
        isRunning = true;
        builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.music_icon)
                .setPriority(Notification.PRIORITY_MAX);
        mediaPlayer = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int i) {
                switch (i) {
                    case AudioManager.AUDIOFOCUS_LOSS:
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            pauseMilis = mediaPlayer.getCurrentPosition();
                            isPlaying = false;
                            showForegroundNotification(MEDIA_PAUSE);
                        }
                        EventBus.getDefault().post(MEDIA_PAUSE);
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            pauseMilis = mediaPlayer.getCurrentPosition();
                            isPlaying = false;
                            showForegroundNotification(MEDIA_PAUSE);
                        }
                        EventBus.getDefault().post(MEDIA_PAUSE);
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN:
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.seekTo(pauseMilis);
                            mediaPlayer.start();
                            isPlaying = true;
                            showForegroundNotification(MEDIA_PLAY);
                            EventBus.getDefault().post(MEDIA_PLAY);
                        } else {
                            mediaPlayer.setVolume(1f, 1f);
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        mediaPlayer.setVolume(0.5f, 0.5f);
                        break;
                }
            }
        };
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        bindCounts++;
        return new MediaBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        bindCounts--;
        if (mediaPlayer == null) {
            stopSelf();
        } else if (bindCounts == 0 && !mediaPlayer.isPlaying()) {
            mediaPlayer.release();
            mediaPlayer = null;
            stopSelf();
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }

    private void createPlayer(final String filePath) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(Utils.getInstance(MediaPlayerService.this).getMediaFolderPath() + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mediaPlayer) {
                currentFile = filePath;
                if (requestAudioFocus()) {
                    mediaPlayer.start();
                }
                isPlaying = true;
                showForegroundNotification(MEDIA_PLAY);
                registerReceiver(mediaReceiver, new IntentFilter(MEDIA_RECEIVER_ACTION));

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                audioManager.abandonAudioFocus(onAudioFocusChangeListener);
                stopForeground(true);
                unregisterReceiver(mediaReceiver);
                mediaPlayer.release();
                isPlaying = false;
                currentFile = "";
                EventBus.getDefault().post(MEDIA_FINISHED);
            }
        });
        mediaPlayer.prepareAsync();
    }

    public void playFile(MediaItem mediaItem) {
        if (mediaPlayer != null) {
            if (mediaItem.getPath().equals(currentFile)) {
                mediaPlayer.seekTo(pauseMilis);
                mediaPlayer.start();
                isPlaying = true;
                if (requestAudioFocus()) {
                    mediaPlayer.start();
                }
                showForegroundNotification(MEDIA_PLAY);
                return;
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaName = mediaItem.getName();
        BitmapFactory.Options bitmapOption = new BitmapFactory.Options();
        if (mediaItem.getImage() != null) {
            fileImage = BitmapFactory.decodeByteArray(mediaItem.getImage(), 0, mediaItem.getImage().length, bitmapOption);
        } else {
            fileImage = null;
        }
        createPlayer(mediaItem.getPath());

    }

    public void pauseFile() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            pauseMilis = mediaPlayer.getCurrentPosition();
            isPlaying = false;
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
            showForegroundNotification(MEDIA_PAUSE);
        }
    }

    public void stopFile() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                unregisterReceiver(mediaReceiver);
            }
            stopForeground(true);
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
            isPlaying = false;
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private boolean requestAudioFocus() {
        int request = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return request == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void showForegroundNotification(String notifAction) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
        remoteViews.setTextViewText(R.id.notif_text, mediaName);
        remoteViews.setOnClickPendingIntent(R.id.notification_root, PendingIntent.getActivity(MediaPlayerService.this, 0, new Intent(MediaPlayerService.this, MainActivity.class).putExtra(MEDIA_ACTION, MEDIA_ROOT), PendingIntent.FLAG_UPDATE_CURRENT));
        remoteViews.setOnClickPendingIntent(R.id.notif_close, PendingIntent.getBroadcast(MediaPlayerService.this, 1, new Intent(MEDIA_RECEIVER_ACTION).putExtra(MEDIA_ACTION, MEDIA_CLOSE), PendingIntent.FLAG_UPDATE_CURRENT));
        if (notifAction.equals(MEDIA_PLAY)) {
            remoteViews.setImageViewResource(R.id.notif_button, R.drawable.pause_file_icon);
            remoteViews.setOnClickPendingIntent(R.id.notif_button, PendingIntent.getBroadcast(MediaPlayerService.this, 2, new Intent(MEDIA_RECEIVER_ACTION).putExtra(MEDIA_ACTION, MEDIA_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
            remoteViews.setImageViewResource(R.id.notif_button, R.drawable.play_file_icon);
            remoteViews.setOnClickPendingIntent(R.id.notif_button, PendingIntent.getBroadcast(MediaPlayerService.this, 3, new Intent(MEDIA_RECEIVER_ACTION).putExtra(MEDIA_ACTION, MEDIA_PLAY), PendingIntent.FLAG_UPDATE_CURRENT));
        }

        RemoteViews bigRemoteViews = new RemoteViews(getPackageName(), R.layout.big_notification_layout);
        bigRemoteViews.setOnClickPendingIntent(R.id.big_notification_root, PendingIntent.getActivity(MediaPlayerService.this, 0, new Intent(MediaPlayerService.this, MainActivity.class).putExtra(MEDIA_ACTION, MEDIA_ROOT), PendingIntent.FLAG_UPDATE_CURRENT));
        bigRemoteViews.setTextViewText(R.id.big_notif_text, mediaName);
        if (fileImage != null) {
            bigRemoteViews.setImageViewBitmap(R.id.file_image, fileImage);
        } else {
            bigRemoteViews.setImageViewResource(R.id.file_image, R.mipmap.default_image);
        }
        bigRemoteViews.setOnClickPendingIntent(R.id.big_notif_close, PendingIntent.getBroadcast(MediaPlayerService.this, 1, new Intent(MEDIA_RECEIVER_ACTION).putExtra(MEDIA_ACTION, MEDIA_CLOSE), PendingIntent.FLAG_UPDATE_CURRENT));
        if (notifAction.equals(MEDIA_PLAY)) {
            bigRemoteViews.setImageViewResource(R.id.big_notif_button, R.drawable.pause_file_icon);
            bigRemoteViews.setOnClickPendingIntent(R.id.big_notif_button, PendingIntent.getBroadcast(MediaPlayerService.this, 2, new Intent(MEDIA_RECEIVER_ACTION).putExtra(MEDIA_ACTION, MEDIA_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
            bigRemoteViews.setImageViewResource(R.id.big_notif_button, R.drawable.play_file_icon);
            bigRemoteViews.setOnClickPendingIntent(R.id.big_notif_button, PendingIntent.getBroadcast(MediaPlayerService.this, 3, new Intent(MEDIA_RECEIVER_ACTION).putExtra(MEDIA_ACTION, MEDIA_PLAY), PendingIntent.FLAG_UPDATE_CURRENT));
        }
        startForeground(NOTIFICATION_ID, builder.setCustomContentView(remoteViews).setCustomBigContentView(bigRemoteViews).build());
    }

    public static String getCurrentFile() {
        return currentFile;
    }


    public static boolean getIsRunning() {
        return isRunning;
    }

    public static boolean getIsPlaying() {
        return isPlaying;
    }

    public class MediaBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    public class MediaReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra(MEDIA_ACTION);
            switch (action) {
                case MEDIA_PLAY:
                    mediaPlayer.seekTo(pauseMilis);
                    mediaPlayer.start();
                    isPlaying = true;
                    showForegroundNotification(MEDIA_PLAY);
                    EventBus.getDefault().post(MEDIA_PLAY);
                    break;
                case MEDIA_PAUSE:
                    mediaPlayer.pause();
                    pauseMilis = mediaPlayer.getCurrentPosition();
                    isPlaying = false;
                    showForegroundNotification(MEDIA_PAUSE);
                    EventBus.getDefault().post(MEDIA_PAUSE);
                    break;
                case MEDIA_CLOSE:
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    isPlaying = false;
                    stopForeground(true);
                    EventBus.getDefault().post(MEDIA_FINISHED);
                    break;
            }
        }
    }
}
