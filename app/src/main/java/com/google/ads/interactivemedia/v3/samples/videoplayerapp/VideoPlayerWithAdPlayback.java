// Copyright 2014 Google Inc. All Rights Reserved.

package com.google.ads.interactivemedia.v3.samples.videoplayerapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider;
import com.google.ads.interactivemedia.v3.api.player.VideoAdPlayer;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.google.ads.interactivemedia.v3.samples.samplevideoplayer.VideoPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Video player that can play content video and ads.
 */
public class VideoPlayerWithAdPlayback extends RelativeLayout {

    /** Interface for alerting caller of video completion. */
    public interface OnContentCompleteListener {
        public void onContentComplete();
    }

    // The wrapped video player.
    private VideoPlayer mVideoPlayer;

    // The SDK will render ad playback UI elements into this ViewGroup.
    private ViewGroup mAdUiContainer;

    // Used to track if the current video is an ad (as opposed to a content video).
    private boolean mIsAdDisplayed;

    // Used to track the current content video URL to resume content playback.
    private String mContentVideoUrl;

    // The saved position in the ad to resume if app is backgrounded during ad playback.
    private int mSavedAdPosition;

    // The saved position in the content to resume to after ad playback or if app is backgrounded
    // during content playback.
    private int mSavedContentPosition;

    // Called when the content is completed.
    private OnContentCompleteListener mOnContentCompleteListener;

    // VideoAdPlayer interface implementation for the SDK to send ad play/pause type events.
    private VideoAdPlayer mVideoAdPlayer;

    // ContentProgressProvider interface implementation for the SDK to check content progress.
    private ContentProgressProvider mContentProgressProvider;

    private final List<VideoAdPlayer.VideoAdPlayerCallback> mAdCallbacks =
            new ArrayList<VideoAdPlayer.VideoAdPlayerCallback>(1);

    public VideoPlayerWithAdPlayback(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VideoPlayerWithAdPlayback(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoPlayerWithAdPlayback(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        mIsAdDisplayed = false;
        mSavedAdPosition = 0;
        mSavedContentPosition = 0;
        mVideoPlayer = (VideoPlayer) this.getRootView().findViewById(R.id.videoPlayer);
        mAdUiContainer = (ViewGroup) this.getRootView().findViewById(R.id.adUiContainer);

        // Define VideoAdPlayer connector.
        mVideoAdPlayer = new VideoAdPlayer() {
            @Override
            public void playAd() {
                mIsAdDisplayed = true;
                mVideoPlayer.play();
            }

            @Override
            public void loadAd(String url) {
                mIsAdDisplayed = true;
                mVideoPlayer.setVideoPath(url);
            }

            @Override
            public void stopAd() {
                mVideoPlayer.stopPlayback();
            }

            @Override
            public void pauseAd() {
                mVideoPlayer.pause();
            }

            @Override
            public void resumeAd() {
                playAd();
            }

            @Override
            public void addCallback(VideoAdPlayerCallback videoAdPlayerCallback) {
                mAdCallbacks.add(videoAdPlayerCallback);
            }

            @Override
            public void removeCallback(VideoAdPlayerCallback videoAdPlayerCallback) {
                mAdCallbacks.remove(videoAdPlayerCallback);
            }

            @Override
            public VideoProgressUpdate getAdProgress() {
                if (!mIsAdDisplayed || mVideoPlayer.getDuration() <= 0) {
                    return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
                }
                return new VideoProgressUpdate(mVideoPlayer.getCurrentPosition(),
                        mVideoPlayer.getDuration());
            }
        };

        mContentProgressProvider = new ContentProgressProvider() {
            @Override
            public VideoProgressUpdate getContentProgress() {
                if (mIsAdDisplayed || mVideoPlayer.getDuration() <= 0) {
                    return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
                }
                return new VideoProgressUpdate(mVideoPlayer.getCurrentPosition(),
                        mVideoPlayer.getDuration());
            }
        };

        // Set player callbacks for delegating major video events.
        mVideoPlayer.addPlayerCallback(new VideoPlayer.PlayerCallback() {
            @Override
            public void onPlay() {
                if (mIsAdDisplayed) {
                    for (VideoAdPlayer.VideoAdPlayerCallback callback : mAdCallbacks) {
                        callback.onPlay();
                    }
                }
            }

            @Override
            public void onPause() {
                if (mIsAdDisplayed) {
                    for (VideoAdPlayer.VideoAdPlayerCallback callback : mAdCallbacks) {
                        callback.onPause();
                    }
                }
            }

            @Override
            public void onResume() {
                if (mIsAdDisplayed) {
                    for (VideoAdPlayer.VideoAdPlayerCallback callback : mAdCallbacks) {
                        callback.onResume();
                    }
                }
            }

            @Override
            public void onError() {
                if (mIsAdDisplayed) {
                    for (VideoAdPlayer.VideoAdPlayerCallback callback : mAdCallbacks) {
                        callback.onError();
                    }
                }
            }

            @Override
            public void onCompleted() {
                if (mIsAdDisplayed) {
                    for (VideoAdPlayer.VideoAdPlayerCallback callback : mAdCallbacks) {
                        callback.onEnded();
                    }
                } else {
                    // Alert an external listener that our content video is complete.
                    if (mOnContentCompleteListener != null) {
                        mOnContentCompleteListener.onContentComplete();
                    }
                }
            }
        });
    }

    /**
     * Set a listener to be triggered when the content (non-ad) video completes.
     */
    public void setOnContentCompleteListener(OnContentCompleteListener listener) {
        mOnContentCompleteListener = listener;
    }

    /**
     * Set the path of the video to be played as content.
     */
    public void setContentVideoPath(String contentVideoUrl) {
        mContentVideoUrl = contentVideoUrl;
    }

    /**
     * Save the playback progress state of the currently playing video. This is called when content
     * is paused to prepare for ad playback or when app is backgrounded.
     */
    public void savePosition() {
        if (mIsAdDisplayed) {
            mSavedAdPosition = mVideoPlayer.getCurrentPosition();
        } else {
            mSavedContentPosition = mVideoPlayer.getCurrentPosition();
        }
    }

    /**
     * Restore the currently loaded video to its previously saved playback progress state. This is
     * called when content is resumed after ad playback or when focus has returned to the app.
     */
    public void restorePosition() {
        if (mIsAdDisplayed) {
            mVideoPlayer.seekTo(mSavedAdPosition);
        } else {
            mVideoPlayer.seekTo(mSavedContentPosition);
        }
    }

    /**
     * Pauses the content video.
     */
    public void pause() {
        mVideoPlayer.pause();
    }

    /**
     * Plays the content video.
     */
    public void play() {
        mVideoPlayer.play();
    }

    /**
     * Seeks the content video.
     */
    public void seek(int time) {
        if (mIsAdDisplayed) {
            // When ad is playing, set the content video position to seek to when ad finishes.
            mSavedContentPosition = time;
        } else {
            mVideoPlayer.seekTo(time);
        }
    }

    /**
     * Returns current content video play time.
     */
    public int getCurrentContentTime() {
        if (mIsAdDisplayed) {
            return mSavedContentPosition;
        } else {
            return mVideoPlayer.getCurrentPosition();
        }
    }

    /**
     * Pause the currently playing content video in preparation for an ad to play, and disables
     * the media controller.
     */
    public void pauseContentForAdPlayback() {
        mVideoPlayer.disablePlaybackControls();
        savePosition();
        mVideoPlayer.stopPlayback();
    }

    /**
     * Resume the content video from its previous playback progress position after
     * an ad finishes playing. Re-enables the media controller.
     */
    public void resumeContentAfterAdPlayback() {
        if (mContentVideoUrl == null || mContentVideoUrl.isEmpty()) {
            Log.w("ImaExample", "No content URL specified.");
            return;
        }
        mIsAdDisplayed = false;
        mVideoPlayer.setVideoPath(mContentVideoUrl);
        mVideoPlayer.enablePlaybackControls();
        mVideoPlayer.seekTo(mSavedContentPosition);
        mVideoPlayer.play();
    }

    /**
     * Returns the UI element for rendering video ad elements.
     */
    public ViewGroup getAdUiContainer() {
        return mAdUiContainer;
    }

    /**
     * Returns an implementation of the SDK's VideoAdPlayer interface.
     */
    public VideoAdPlayer getVideoAdPlayer() {
        return mVideoAdPlayer;
    }

    /**
     * Returns if an ad is displayed.
     */
    public boolean getIsAdDisplayed() {
        return mIsAdDisplayed;
    }

    public ContentProgressProvider getContentProgressProvider() {
        return mContentProgressProvider;
    }
}
