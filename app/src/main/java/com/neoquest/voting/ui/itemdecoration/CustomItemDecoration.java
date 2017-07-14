package com.neoquest.voting.ui.itemdecoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;

import com.neoquest.voting.App;
import com.neoquest.voting.utils.ScreenUtils;

public class CustomItemDecoration extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        if (itemPosition == 0) {
            outRect.set(0, ScreenUtils.getStatusBarHeight(App.getContext()), 0, 0);
        }
    }
}
