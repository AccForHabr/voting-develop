package com.neoquest.voting.ui.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.widget.FrameLayout;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.neoquest.voting.App;
import com.neoquest.voting.R;
import com.neoquest.voting.presentation.presenter.main.MainPresenter;
import com.neoquest.voting.presentation.view.main.MainView;
import com.neoquest.voting.router.Router;
import com.neoquest.voting.ui.fragment.main.ResultsFragment;
import com.neoquest.voting.ui.fragment.main.VotingFragment;

public class MainActivity extends MvpAppCompatActivity implements MainView, Router {
    public static final String TAG = "MainActivity";
    @InjectPresenter
    MainPresenter mMainPresenter;

    private FrameLayout containerFrameLayout;


    public static Intent getIntent(final Context context) {
        Intent intent = new Intent(context, MainActivity.class);

        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        containerFrameLayout = (FrameLayout) findViewById(R.id.container_frame_layout);
        App.setAppRouter(this);
        mMainPresenter.onCreate(savedInstanceState);
    }

    @Override
    public void goToVoting(Bundle bundle) {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.container_frame_layout, VotingFragment.newInstance())
                .commit();
    }

    @Override
    public void goToResults(Bundle bundle) {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.container_frame_layout, ResultsFragment.newInstance())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMainPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMainPresenter.onStop();
    }

    @Override
    public void showMessage(String message) {
        final Snackbar snackbar = Snackbar.make(containerFrameLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    @Override
    public void showError(String error) {
        final Snackbar snackbar = Snackbar.make(containerFrameLayout, error, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.red));
        snackbar.show();
    }
}
