package com.neoquest.voting.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.neoquest.voting.R;
import com.neoquest.voting.model.entity.Report;

import java.util.ArrayList;
import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int REPORT_ITEM = 0;
    private final int HEADER_ITEM = 1;

    private List<Report> dataList = new ArrayList<>();

    private IListener iListener;

    public ReportsAdapter() {
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_ITEM:
                return new ReportsAdapter.HeaderHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_report_header, parent, false));
            default:
                final ReportHolder reportHolder = new ReportsAdapter.ReportHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_report, parent, false));
                reportHolder.itemView.startAnimation(AnimationUtils.loadAnimation(parent.getContext(), android.R.anim.fade_in));
                return reportHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == REPORT_ITEM) {
            ((ReportHolder) holder).position = position;

            final Report report = dataList.get(position - 1);

            ((ReportHolder) holder).authorTextView.setText(String.valueOf(position) + ". " + report.getAuthor());
            ((ReportHolder) holder).titleTextView.setText(report.getName());
            ((ReportHolder) holder).descriptionextView.setText(report.getDescription());
        } else {
            ((HeaderHolder) holder).titleTextView.setVisibility(dataList.isEmpty() ? View.INVISIBLE : View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? HEADER_ITEM : REPORT_ITEM;
    }

    public void setIListener(IListener iListener) {
        this.iListener = iListener;
    }

    public void setDataList(List<Report> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
    }

    class ReportHolder extends RecyclerView.ViewHolder {
        private Integer position;

        private TextView authorTextView;
        private TextView titleTextView;
        private TextView descriptionextView;
        private Button voteButton;

        public ReportHolder(View itemView) {
            super(itemView);
            authorTextView = (TextView) itemView.findViewById(R.id.author_text_view);
            titleTextView = (TextView) itemView.findViewById(R.id.title_text_view);
            descriptionextView = (TextView) itemView.findViewById(R.id.description_text_view);
            voteButton = (Button) itemView.findViewById(R.id.vote_button);

            voteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (iListener != null) {
                        iListener.onReportClick(dataList.get(position - 1));
                    }
                }
            });
        }
    }

    class HeaderHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;

        public HeaderHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title_text_view);
        }
    }

    public interface IListener {
        void onReportClick(Report report);
    }
}
