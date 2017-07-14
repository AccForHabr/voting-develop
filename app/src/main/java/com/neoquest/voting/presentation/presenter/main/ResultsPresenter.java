package com.neoquest.voting.presentation.presenter.main;


import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.neoquest.voting.App;
import com.neoquest.voting.eventbus.ErrorEvent;
import com.neoquest.voting.eventbus.GetReportsEvent;
import com.neoquest.voting.interactor.LoadReportsInteractor;
import com.neoquest.voting.model.ReportsModel;
import com.neoquest.voting.presentation.view.main.ResultsView;
import com.neoquest.voting.ui.adapter.ResultsAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@InjectViewState
public class ResultsPresenter extends MvpPresenter<ResultsView> {

    private ResultsAdapter resultsAdapter;
    private LoadReportsInteractor loadReportsInteractor;

    public void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        resultsAdapter = new ResultsAdapter(ReportsModel.INSTANCE.getReportsList(),
                ReportsModel.INSTANCE.getVotesCommonCount());
        loadReportsInteractor = new LoadReportsInteractor();
        if (ReportsModel.INSTANCE.getReportsList().size() == 0) {
            getViewState().showLoader();
        }
        loadReportsInteractor.getReports(App.getDeviceId());
    }

    public void onStart() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void onStop() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetReportsEvent getReportsEvent) {
        if (getReportsEvent.isSuccess()) {
            resultsAdapter.notifyDataSetChanged();
        } else {
            EventBus.getDefault().post(new ErrorEvent(getReportsEvent.getErrorMessage()));
        }
        getViewState().hideLoader();
    }

    public RecyclerView.Adapter getResultsAdapter() {
        return resultsAdapter;
    }
}
