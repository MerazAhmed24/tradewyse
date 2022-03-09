package com.info.tradewyse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.info.commons.Constants;
import com.info.sendbird.utils.ImageUtils;
import com.jsibbold.zoomage.ZoomageView;

/**
 * Created by Amit Gupta on 28,January,2021
 */
public class FullScreenImagePinchZoomActivity extends BaseActivity implements ExoPlayer.EventListener {
    ZoomageView zoomageView;
    private String type = "";
    SimpleExoPlayerView exoPlayerView;
    //creating a variable for exoplayer
    SimpleExoPlayer exoPlayer;
    private ProgressBar progress;

    public static void startFullScreenImagePinchZoomActivity(Context context, String imageUrl, String type) {
        Intent intent = new Intent(context, FullScreenImagePinchZoomActivity.class);
        intent.putExtra(Constants.IMAGE_URL, imageUrl);
        intent.putExtra("TYPE", type);
        ((Activity) context).startActivity(intent);
    }
    public static void startFullScreenImagePinchZoomActivityFromTips(Context context, String imageUrl, String type) {
        Intent intent = new Intent(context, FullScreenImagePinchZoomActivity.class);
        intent.putExtra(Constants.IMAGE_URL, imageUrl);
        intent.putExtra("TYPE", type);
        ((Activity) context).startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen_image_pinch_zoom);
        zoomageView = findViewById(R.id.zoomageView);
        exoPlayerView = findViewById(R.id.videoView);
        progress = findViewById(R.id.progress);
        findViewById(R.id.menuTab).setVisibility(View.GONE);
        findViewById(R.id.tvRoom).setVisibility(View.GONE);
        String imageUrl = getIntent().getStringExtra(Constants.IMAGE_URL);
        type = getIntent().getStringExtra("TYPE");
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
        exoPlayer= ExoPlayerFactory.newSimpleInstance(this,trackSelector);

        if (type.equalsIgnoreCase(Constants.VideoMessageType)) {
            progress.setVisibility(View.VISIBLE);
            zoomageView.setVisibility(View.GONE);
            exoPlayerView.setVisibility(View.VISIBLE);
            Uri videouri= Uri.parse(imageUrl);
            DefaultHttpDataSourceFactory dataSourceFactory=new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videouri,dataSourceFactory,extractorsFactory,null,null);
            exoPlayerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            //we are setting our exoplayer when it is ready.
            exoPlayer.setPlayWhenReady(true);
            progress.setVisibility(View.GONE);

        } else if (type.equalsIgnoreCase(Constants.ImageMessageType)) {
            zoomageView.setVisibility(View.VISIBLE);
            exoPlayerView.setVisibility(View.GONE);
            ImageUtils.displayRoundCornerImageFromUrl(this, imageUrl, zoomageView);
        }

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_BUFFERING || playbackState == ExoPlayer.STATE_ENDED ||!playWhenReady){

            progress.setVisibility(View.VISIBLE);
            zoomageView.setVisibility(View.GONE);
            exoPlayerView.setKeepScreenOn(false);
        } else {
            progress.setVisibility(View.INVISIBLE);
            exoPlayerView.setKeepScreenOn(true);
            zoomageView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        pausePlayer();

    }
    private void pausePlayer() {
        if (exoPlayerView != null) {
            exoPlayer.setPlayWhenReady(false);
        }
    }
}
