package com.chimngu.drag.data.network;

import com.chimngu.drag.data.model.Video;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.AnyThread;

public interface ApiHelper {

    @AnyThread
    ArrayList<Video> getHomeVideo() throws IOException;

    @AnyThread
    ArrayList<Video> getTrendingVideo() throws IOException;

    @AnyThread
    ArrayList<Video> getRelationVideo() throws IOException;

}
