package com.chimngu.drag.mvp.home;

import com.chimngu.drag.data.model.Video;
import com.chimngu.drag.data.network.ApiHelper;
import com.chimngu.drag.mvp.BasePresenter;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class HomePresenterImpl extends BasePresenter<HomeContact.HomeView> implements HomeContact.HomePresenter {

    private ApiHelper apiHelper;

    public HomePresenterImpl(HomeContact.HomeView view, ApiHelper apiHelper) {
        super(view);
        this.apiHelper = apiHelper;
    }

    @Override
    public void getHomeVideo() {
        compositeDisposable.add(getHomeVideoObservable().subscribe(new Consumer<ArrayList<Video>>() {
            @Override
            public void accept(ArrayList<Video> videos) throws Exception {
                view.bindHomeVideo(videos);
            }
        }, throwable -> {
        }, () -> {
        }, disposable -> {
        }));
    }

    private Observable<ArrayList<Video>> getHomeVideoObservable() {
        return Observable.just("").map(s -> apiHelper.getHomeVideo());
    }
}
