package com.neoquest.voting.presentation.presenter.main;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.neoquest.voting.App;
import com.neoquest.voting.R;
import com.neoquest.voting.eventbus.ErrorEvent;
import com.neoquest.voting.eventbus.GetReportsEvent;
import com.neoquest.voting.eventbus.MessageEvent;
import com.neoquest.voting.eventbus.VoteEvent;
import com.neoquest.voting.interactor.LoadReportsInteractor;
import com.neoquest.voting.interactor.VoteInteractor;
import com.neoquest.voting.model.ReportsModel;
import com.neoquest.voting.model.SettingsModel;
import com.neoquest.voting.model.entity.Report;
import com.neoquest.voting.presentation.view.main.VotingView;
import com.neoquest.voting.ui.adapter.ReportsAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@InjectViewState
public class VotingPresenter extends MvpPresenter<VotingView> {

    private LoadReportsInteractor loadReportsInteractor;
    private VoteInteractor voteInteractor;

    private ReportsAdapter reportsAdapter;

    private boolean reportsLoaded = false;
    private boolean voteButtobVisible = true;

    private Report selectedReport;

    public View.OnClickListener getVoteButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getViewState().hideVoteButton();
                voteButtobVisible = false;
                if (reportsLoaded) {
                    reportsAdapter.setDataList(ReportsModel.INSTANCE.getReportsList());
                    reportsAdapter.notifyDataSetChanged();
                } else {
                    getViewState().showProgressBar();
                }
            }
        };

    }

    public void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        reportsAdapter = new ReportsAdapter();
        loadReportsInteractor = new LoadReportsInteractor();
        voteInteractor = new VoteInteractor();
        reportsAdapter.setIListener(getReportsAdapterListener());
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
            if (!voteButtobVisible) {
                reportsAdapter.setDataList(ReportsModel.INSTANCE.getReportsList());
                reportsAdapter.notifyDataSetChanged();
            }
        } else {
            EventBus.getDefault().post(new ErrorEvent(getReportsEvent.getErrorMessage()));
        }
        reportsLoaded = true;
        getViewState().hideProgressBar();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VoteEvent voteEvent) {
        if (voteEvent.isSuccess()) {
            if (selectedReport != null) {
                selectedReport.setVotesCount(selectedReport.getVotesCount() + 1);
            }
            EventBus.getDefault().post(new MessageEvent(App.getContext().getString(R.string.success_vote)));
            SettingsModel.INSTANCE.setUserVoted();
            App.getAppRouter().goToResults(new Bundle());
        } else {
            EventBus.getDefault().post(new ErrorEvent(voteEvent.getErrorMessage()));
            if (voteEvent.getErrorMessage().equals(App.getContext().getString(R.string.already_voted))) {
                SettingsModel.INSTANCE.setUserVoted();
                App.getAppRouter().goToResults(new Bundle());
            } else {
                getViewState().showRecyclerView();
            }
        }
        getViewState().hideProgressBar();
    }

    public RecyclerView.Adapter getReportsAdapter() {
        return reportsAdapter;
    }

    public void onCreateView() {
        loadReportsInteractor.getReports(App.getDeviceId());
    }

    private ReportsAdapter.IListener getReportsAdapterListener() {
        return new ReportsAdapter.IListener() {
            @Override
            public void onReportClick(Report report) {
                selectedReport = report;
                getViewState().showVoteDialog(report.getName());
            }
        };
    }

    public DialogInterface.OnClickListener onConfirmVoteClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getViewState().showProgressBar();
                getViewState().hideRecyclerView();
                voteInteractor.vote(App.getDeviceId(),
                        selectedReport.getHashValue(),
                        SettingsModel.INSTANCE.getPublicKey(),
                        SettingsModel.INSTANCE.getK());
            }
        };
    }

    public DialogInterface.OnClickListener onCancelVoteClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedReport = null;
            }
        };
    }
}
