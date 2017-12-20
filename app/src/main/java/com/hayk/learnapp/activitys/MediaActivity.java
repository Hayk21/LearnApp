package com.hayk.learnapp.activitys;

import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.hayk.learnapp.R;
import com.hayk.learnapp.services.MediaPlayerService;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class MediaActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String SAVED_CONFIG = "SavedConfig";
    private static final String IS_PLAYED = "IsPlayed";
    private SurfaceView videoContainer;
    private LinearLayout mediaIndicators;
    private RelativeLayout container;
    private MediaPlayer mediaPlayer;
    private SeekBar progressBar;
    private ImageView playOrPause, fullScreen;
    private Runnable progresRun, indicatorsRun;
    private Handler handlerForProgress, handlerForIndicators;
    private int savedPosition = 0;
    private boolean isPlayed = true;
    private Animation indicatorOpenAnimation, indicatorCloseAnimation;
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        if (savedInstanceState != null) {
            savedPosition = savedInstanceState.getInt(SAVED_CONFIG);
            isPlayed = savedInstanceState.getBoolean(IS_PLAYED);
        }
        init();
        setListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        createMediaPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeMediaPlayer();
    }

    private void init() {
        indicatorOpenAnimation = AnimationUtils.loadAnimation(MediaActivity.this, R.anim.media_indicator_open);
        indicatorCloseAnimation = AnimationUtils.loadAnimation(MediaActivity.this, R.anim.media_indicator_close);
        container = (RelativeLayout) findViewById(R.id.media_container);
        mediaIndicators = (LinearLayout) findViewById(R.id.media_indicators);
        videoContainer = (SurfaceView) findViewById(R.id.video_container);
        progressBar = (SeekBar) findViewById(R.id.progressBar);
        playOrPause = (ImageView) findViewById(R.id.play_video);
        fullScreen = (ImageView) findViewById(R.id.full_screen);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int i) {
                switch (i){
                    case AudioManager.AUDIOFOCUS_LOSS:
                        mediaPlayer.pause();
                        playOrPause.setImageDrawable(getResources().getDrawable(R.drawable.play_file_icon));
                        playOrPause.setColorFilter(getResources().getColor(R.color.white));
                        break;
                    case  AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        mediaPlayer.pause();
                        playOrPause.setImageDrawable(getResources().getDrawable(R.drawable.play_file_icon));
                        playOrPause.setColorFilter(getResources().getColor(R.color.white));
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN:
                        if(!mediaPlayer.isPlaying()){
                            mediaPlayer.start();
                            playOrPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_file_icon));
                        }
                        else {
                            mediaPlayer.setVolume(1f,1f);
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        mediaPlayer.setVolume(0.5f,0.5f);
                        break;
                }
            }
        };

        if (MediaActivity.this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                || MediaActivity.this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            fullScreen.setImageDrawable(getResources().getDrawable(R.drawable.full_screen));
        } else {
            fullScreen.setImageDrawable(getResources().getDrawable(R.drawable.full_screen_exit));
        }

        handlerForProgress = new Handler();
        playOrPause.setColorFilter(getResources().getColor(R.color.white));
        videoContainer.getHolder().addCallback(MediaActivity.this);

        progresRun = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    progressBar.setProgress(mediaPlayer.getCurrentPosition() / 1000);
                    handlerForProgress.postDelayed(this, 1000);
                }
            }
        };

        indicatorsRun = new Runnable() {
            @Override
            public void run() {
                mediaIndicators.startAnimation(indicatorCloseAnimation);
            }
        };
    }

    private void setListeners() {
        indicatorCloseAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mediaIndicators.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setIndicatorsVisibility();
            }
        });

        playOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlerForIndicators.removeCallbacks(indicatorsRun);
                handlerForIndicators.postDelayed(indicatorsRun, 4000);
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    audioManager.abandonAudioFocus(onAudioFocusChangeListener);
                    playOrPause.setImageDrawable(getResources().getDrawable(R.drawable.play_file_icon));
                    playOrPause.setColorFilter(getResources().getColor(R.color.white));
                } else {
                    if(requestAudioFocus()) {
                        mediaPlayer.start();
                    }
                    MediaActivity.this.runOnUiThread(progresRun);
                    playOrPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_file_icon));
                    playOrPause.setColorFilter(getResources().getColor(R.color.white));
                }
            }
        });

        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlerForIndicators.removeCallbacks(indicatorsRun);
                handlerForIndicators.postDelayed(indicatorsRun, 4000);
                if (MediaActivity.this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        || MediaActivity.this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handlerForIndicators.removeCallbacks(indicatorsRun);
                handlerForProgress.removeCallbacks(progresRun);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(seekBar.getProgress() * 1000);
                        mediaPlayer.start();
                        MediaActivity.this.runOnUiThread(progresRun);
                        handlerForIndicators.postDelayed(indicatorsRun, 4000);
                    }else {
                        mediaPlayer.seekTo(seekBar.getProgress() * 1000);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        EventBus.getDefault().post(MediaPlayerService.MEDIA_FINISHED);
        this.finish();
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mediaPlayer.isPlaying()) {
            outState.putBoolean(IS_PLAYED, true);
        }
        outState.putInt(SAVED_CONFIG, mediaPlayer.getCurrentPosition());
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setDisplay(surfaceHolder);
        setIndicatorsVisibility();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        setVideoSize();
        progressBar.setMax(mediaPlayer.getDuration());
        if (savedPosition != 0) {
            mediaPlayer.seekTo(savedPosition);
        }
        if (isPlayed) {
            if(requestAudioFocus()) {
                mediaPlayer.start();
            }
        }
        isPlayed = true;
        progressBar.setMax(mediaPlayer.getDuration() / 1000);
        MediaActivity.this.runOnUiThread(progresRun);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    private boolean requestAudioFocus(){
        int request = audioManager.requestAudioFocus(onAudioFocusChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        return request == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void setIndicatorsVisibility() {
        if (mediaIndicators.getVisibility() == View.INVISIBLE) {
            mediaIndicators.setVisibility(View.VISIBLE);
            mediaIndicators.startAnimation(indicatorOpenAnimation);
            handlerForIndicators = new Handler();
            handlerForIndicators.postDelayed(indicatorsRun, 4000);
        } else {
            mediaIndicators.startAnimation(indicatorCloseAnimation);
            handlerForIndicators.removeCallbacks(indicatorsRun);
        }
    }

    private void setVideoSize() {
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;

        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;

        android.view.ViewGroup.LayoutParams lp = videoContainer.getLayoutParams();
        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        videoContainer.setLayoutParams(lp);
    }

    private void createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(getFilesDir().toString() + "/practice media/" + getIntent().getStringExtra(MainActivity.VIDEO_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                EventBus.getDefault().post(MediaPlayerService.MEDIA_FINISHED);
                playOrPause.setImageDrawable(getResources().getDrawable(R.drawable.play_file_icon));
                playOrPause.setColorFilter(getResources().getColor(R.color.white));
            }
        });
    }

    private void removeMediaPlayer() {
        isPlayed = mediaPlayer.isPlaying();
        mediaPlayer.pause();
        savedPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.stop();
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
