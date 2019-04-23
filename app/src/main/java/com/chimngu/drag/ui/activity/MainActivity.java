package com.chimngu.drag.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.chimngu.drag.App;
import com.chimngu.drag.R;
import com.chimngu.drag.data.model.Video;
import com.chimngu.drag.mvp.home.HomeContact;
import com.chimngu.drag.mvp.home.HomePresenterImpl;
import com.chimngu.drag.mvp.trending.TrendingContact;
import com.chimngu.drag.mvp.trending.TrendingPresenterImpl;
import com.chimngu.drag.ui.adapter.BaseAdapter;
import com.chimngu.drag.ui.adapter.HomeAdapter;
import com.chimngu.drag.ui.fragment.DetailFragment;
import com.chimngu.drag.ui.fragment.PlayFragment;
import com.chimngu.drag.ui.widget.drag.DragFrame;
import com.chimngu.drag.utils.Utils;
import com.chimngu.drag.utils.images.ImageUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements DragFrame.OnDragListener, SwipeRefreshLayout.OnRefreshListener, HomeContact.HomeView, TrendingContact.TrendingView {

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.rv_home)
    RecyclerView rvHome;
    @BindView(R.id.sr_home)
    SwipeRefreshLayout srHome;
    @BindView(R.id.v_background)
    View vBackground;
    @BindView(R.id.drag_frame)
    DragFrame dragFrame;

    private ArrayList<Object> feeds;
    private ArrayList<Object> trending;

    private HomeAdapter homeAdapter;

    private float ratio;

    private HomeContact.HomePresenter homePresenter;
    private TrendingContact.TrendingPresenter trendingPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homePresenter = new HomePresenterImpl(this, App.self().getApiHelper());
        trendingPresenter = new TrendingPresenterImpl(this, App.self().getApiHelper());

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ImageUtils.showImage(App.self().getApplicationContext(), ivAvatar, R.drawable.test_0, new CenterCrop(), new CircleCrop());

        feeds = new ArrayList<>();
        trending = new ArrayList<>();
        homeAdapter = new HomeAdapter(this);

        rvHome.setLayoutManager(new LinearLayoutManager(this));
        rvHome.setAdapter(homeAdapter);

        dragFrame.setBottomFragment(getSupportFragmentManager(), DetailFragment.newInstance());
        dragFrame.setTopFragment(getSupportFragmentManager(), PlayFragment.newInstance());
        dragFrame.setOnDragListener(this);
        dragFrame.close();

        srHome.setOnRefreshListener(this::onClosed);
        srHome.post(() -> {
            srHome.setRefreshing(true);
            fetchData();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoEvent(Video video) {
        play(video.getRatio());
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        homePresenter.dispose();
        trendingPresenter.dispose();
    }

    @Override
    public void onRefresh() {
        srHome.postDelayed(() -> srHome.setRefreshing(false), 1000);
    }

    @Override
    public void bindTrendingVideo(ArrayList<Video> results) {
        trending.addAll(results);
        feeds.add(1, trending);
        homeAdapter.bindData(feeds);
        srHome.setRefreshing(false);
    }

    @Override
    public void bindHomeVideo(ArrayList<Video> results) {
        feeds.addAll(results);
        feeds.add(BaseAdapter.END_TYPE);
        homeAdapter.bindData(feeds);
        trendingPresenter.getTrendingVideo();
    }

    @Override
    public void onDragProcess(float percent) {
        vBackground.setAlpha(1 - percent);
    }

    @Override
    public void onMaximized() {
        play();
    }

    @Override
    public void onMinimized() {
    }

    @Override
    public void onClosed() {

    }

    @OnClick(R.id.iv_pause)
    public void onIvPauseClicked() {
    }

    @OnClick(R.id.iv_play)
    public void onIvPlayClicked() {
    }

    @OnClick(R.id.iv_close)
    public void onIvCloseClicked() {
        dragFrame.close();
    }

    private void fetchData() {
        homePresenter.getHomeVideo();
    }

    public void play(float r) {
        ratio = r;
        if (dragFrame.isMaximized()) {
            play();
        } else {
            dragFrame.maximize();
        }
    }

    private void play() {
        dragFrame.postDelayed(delayRunnable, 200L);
    }

    private Runnable delayRunnable = new Runnable() {
        @Override
        public void run() {
            if (dragFrame == null) return;
            int heightNew = (int) Math.min(Utils.getScreenWidth() / ratio, Utils.getScreenHeight() - Utils.getScreenHeight() / 3f);
            if (dragFrame.isMaximized()) {
                dragFrame.setHeight(heightNew);
            } else {
                dragFrame.setHeightWaiting(heightNew);
            }
        }
    };
}
