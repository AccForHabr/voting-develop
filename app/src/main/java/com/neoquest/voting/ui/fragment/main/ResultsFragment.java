package com.neoquest.voting.ui.fragment.main;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.neoquest.voting.R;
import com.neoquest.voting.presentation.presenter.main.ResultsPresenter;
import com.neoquest.voting.presentation.view.main.ResultsView;
import com.neoquest.voting.ui.itemdecoration.CustomItemDecoration;

public class ResultsFragment extends MvpAppCompatFragment implements ResultsView {
    public static final String TAG = "ResultsFragment";
    @InjectPresenter
    ResultsPresenter presenter;

    private RecyclerView resultsRecyclerView;
    private ProgressBar progressBar;


    public static ResultsFragment newInstance() {
        ResultsFragment fragment = new ResultsFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_results, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

        resultsRecyclerView = (RecyclerView) rootView.findViewById(R.id.results_recycler_view);
        resultsRecyclerView.setHasFixedSize(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            resultsRecyclerView.addItemDecoration(new CustomItemDecoration());
        }
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        resultsRecyclerView.setAdapter(presenter.getResultsAdapter());

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void showLoader() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        progressBar.setVisibility(View.GONE);
    }
}
