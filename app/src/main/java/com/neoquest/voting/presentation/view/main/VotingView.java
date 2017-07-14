package com.neoquest.voting.presentation.view.main;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface VotingView extends MvpView {

    void hideVoteButton();

    void showProgressBar();

    void hideProgressBar();

    void showVoteDialog(String message);

    void showRecyclerView();

    void hideRecyclerView();
}
