package com.neoquest.voting.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.neoquest.voting.R;
import com.neoquest.voting.model.entity.Report;
import com.neoquest.voting.utils.ScreenUtils;
import com.neoquest.voting.utils.UnitUtils;
import com.neoquest.voting.utils.formatters.PercentFormatter;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int REPORT_ITEM = 0;
    private final int HEADER_ITEM = 1;

    private List<Report> dataList;
    private Integer commonVotesCount = 2;

    private IListener iListener;

    public ResultsAdapter(List<Report> dataList, int commonVotesCount) {
        this.dataList = dataList;
        this.commonVotesCount = commonVotesCount;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_ITEM:
                return new HeaderHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_report_header_result, parent, false));
            default:
                final ReportHolder reportHolder = new ReportHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_report_result, parent, false));
                reportHolder.itemView.startAnimation(AnimationUtils.loadAnimation(parent.getContext(), android.R.anim.fade_in));
                return reportHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int sum = 0;
        for (int i = 0; i < dataList.size(); i++) {
            sum = sum + dataList.get(i).getVotesCount();
        }
        if (getItemViewType(position) != HEADER_ITEM) {
            final Report report = dataList.get(position - 1);
            float per = (float) 0.0;
            per = ((float) report.getVotesCount()) / ((float) sum);
            ((ReportHolder) holder).titleTextView.setText(String.valueOf(position) + ". " + report.getName());
            ((ReportHolder) holder).percentTextView.setText(new PercentFormatter().format(per * 100) + " %");
            final int width = Math.round((ScreenUtils.getWidth(holder.itemView.getContext()) - UnitUtils.dpToPx(16, holder.itemView.getContext())) * per);
            ((ReportHolder) holder).progressFrameLayout.setLayoutParams(
                    new FrameLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT));
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

    class ReportHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView percentTextView;
        private FrameLayout rootFrameLayout;
        private FrameLayout progressFrameLayout;

        public ReportHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title_text_view);
            percentTextView = (TextView) itemView.findViewById(R.id.percent_text_view);
            rootFrameLayout = (FrameLayout) itemView.findViewById(R.id.root_frame_layout);
            progressFrameLayout = (FrameLayout) itemView.findViewById(R.id.progress_frame_layout);
        }
    }

    class HeaderHolder extends RecyclerView.ViewHolder {
        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    public interface IListener {
        void onReportClick(Report report);
    }
}
