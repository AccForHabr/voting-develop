package com.neoquest.voting.ui.fragment.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.neoquest.voting.R;
import com.neoquest.voting.presentation.presenter.main.VotingPresenter;
import com.neoquest.voting.presentation.view.main.VotingView;
import com.neoquest.voting.ui.itemdecoration.CustomItemDecoration;

public class VotingFragment extends MvpAppCompatFragment implements VotingView {
    public static final String TAG = "VotingFragment";
    @InjectPresenter
    VotingPresenter presenter;

    private Button voteButton;
    private RecyclerView reportsRecyclerView;
    private ProgressBar progressBar;

    public static VotingFragment newInstance() {
        VotingFragment fragment = new VotingFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_voting, container, false);

        voteButton = (Button) rootView.findViewById(R.id.vote_button);
        reportsRecyclerView = (RecyclerView) rootView.findViewById(R.id.reports_recycler_view);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

        reportsRecyclerView.setHasFixedSize(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            reportsRecyclerView.addItemDecoration(new CustomItemDecoration());
        }
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reportsRecyclerView.setAdapter(presenter.getReportsAdapter());

        voteButton.setOnClickListener(presenter.getVoteButtonOnClickListener());

        presenter.onCreateView();

        return rootView;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
    public void hideVoteButton() {
        voteButton.setVisibility(View.GONE);
        voteButton.animate().alpha(0.f);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showVoteDialog(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        builder.setMessage(Html.fromHtml(getString(R.string.vote_for_report, message)));
        builder.setPositiveButton(R.string.vote, presenter.onConfirmVoteClickListener());

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(getResources().getColor(android.R.color.white));
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.dialog_background));

        alertDialog.show();
    }

    @Override
    public void showRecyclerView() {
        reportsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRecyclerView() {
        reportsRecyclerView.setVisibility(View.INVISIBLE);
    }
}
