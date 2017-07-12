package com.google.ads.interactivemedia.v3.samples.videoplayerapp;

import java.util.Arrays;
import java.util.List;

/**
 * An enumeration of video metadata.
 */
public enum VideoMetadata {

    PRE_ROLL_SKIP(
            "http://rmcdn.2mdn.net/MotifFiles/html/1248596/android_1330378998288.mp4",
            "pre-roll inline Skip seconds: 10sec and Ad duration: 30sec",
            "http://182.72.85.2/djaxtesting/revive_vastvpaid/www/delivery/vmap_preroll.xml",
            R.drawable.thumbnail1,
            false),
    MID_ROLL_SKIP(
            "http://rmcdn.2mdn.net/MotifFiles/html/1248596/android_1330378998288.mp4",
            "mid-roll inline Skip seconds: 10 seconds , Mid roll offset: 15sec and Ad duration: 30sec",
            "http://182.72.85.2/djaxtesting/revive_vastvpaid/www/delivery/vmap_midroll.xml",
            R.drawable.thumbnail1,
            false),
    POST_ROLL_SKIP(
            "http://rmcdn.2mdn.net/MotifFiles/html/1248596/android_1330378998288.mp4",
            "Post Roll inline Ad durtaion: 30sec and position: post ",
            "http://182.72.85.2/djaxtesting/revive_vastvpaid/www/delivery/vmap_postroll.xml",
            R.drawable.thumbnail1,
            false),

    PRE_ROLL_SKIP_OV(
            "http://rmcdn.2mdn.net/MotifFiles/html/1248596/android_1330378998288.mp4",
            "Preroll for start overlay:",
            "http://182.72.85.2/djaxtesting/revive_vastvpaid/www/delivery/vmapoverlay1.xml",
            R.drawable.thumbnail1,
            false),
    MID_ROLL_OVERLAY(
            "http://rmcdn.2mdn.net/MotifFiles/html/1248596/android_1330378998288.mp4",
            "Mid roll for overlay: Offset time:  15sec",
            "http://182.72.85.2/djaxtesting/revive_vastvpaid/www/delivery/vmapoverlay2.xml",
            R.drawable.thumbnail1,
            false),
    POST_ROLL_OVERLAY(
            "http://rmcdn.2mdn.net/MotifFiles/html/1248596/android_1330378998288.mp4",
            "Post roll for overlay: Offset time:  End",
            "http://182.72.85.2/djaxtesting/revive_vastvpaid/www/delivery/vmapoverlay3.xml",
            R.drawable.thumbnail1,
            true),
    ALL_ADS("http://rmcdn.2mdn.net/MotifFiles/html/1248596/android_1330378998288.mp4",
            "Multiple ads",
            "http://182.72.85.2/djaxtesting/revive_vastvpaid/www/delivery/vmap_multiple.xml",
            R.drawable.thumbnail1,
            true);

    public static final List<VideoMetadata> APP_VIDEOS =
            Arrays.asList(PRE_ROLL_SKIP, MID_ROLL_SKIP, POST_ROLL_SKIP, PRE_ROLL_SKIP_OV, MID_ROLL_OVERLAY,
                    POST_ROLL_OVERLAY, ALL_ADS);

    /** The thumbnail image for the video. **/
    public final int thumbnail;

    /** The title of the video. **/
    public final String title;

    /** The URL for the video. **/
    public final String videoUrl;

    /** The ad tag for the video **/
    public final String adTagUrl;

    /** If the ad is VMAP. **/
    public final boolean isVmap;

    private VideoMetadata(String videoUrl, String title, String adTagUrl, int thumbnail,
                          boolean isVmap) {
        this.videoUrl = videoUrl;
        this.title = title;
        this.adTagUrl = adTagUrl;
        this.thumbnail = thumbnail;
        this.isVmap = isVmap;
    }
}
