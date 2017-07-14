package com.neoquest.voting.presentation.presenter.main;


import android.os.Bundle;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.neoquest.voting.App;
import com.neoquest.voting.eventbus.ErrorEvent;
import com.neoquest.voting.eventbus.MessageEvent;
import com.neoquest.voting.model.SettingsModel;
import com.neoquest.voting.presentation.view.main.MainView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    public void onCreate(Bundle savedInstanceState) {
        if (SettingsModel.INSTANCE.isUserAlreadyVoted()) {
            App.getAppRouter().goToResults(new Bundle());
        } else {
            App.getAppRouter().goToVoting(new Bundle());
        }
    }

    public void onStart() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        getViewState().showMessage(messageEvent.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ErrorEvent errorEvent) {
        getViewState().showError(errorEvent.getError());
    }

    public void onStop() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
