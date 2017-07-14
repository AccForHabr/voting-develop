package com.neoquest.voting.utils;

import android.content.Context;
import android.util.TypedValue;

public class UnitUtils {
    public static int dpToPx(int dp, Context context) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()));
    }
}
