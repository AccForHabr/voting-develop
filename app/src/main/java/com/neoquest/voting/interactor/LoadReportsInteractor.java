package com.neoquest.voting.interactor;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.neoquest.voting.App;
import com.neoquest.voting.R;
import com.neoquest.voting.eventbus.GetReportsEvent;
import com.neoquest.voting.model.ReportsModel;
import com.neoquest.voting.model.SettingsModel;
import com.neoquest.voting.model.entity.ReportsResponse;
import com.neoquest.voting.service.RetrofitService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.HashMap;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class LoadReportsInteractor {
    private Disposable disposable;

    public void getReports(final String deviceId) {
        final JSONObject object = new JSONObject(new HashMap<String, Object>() {{
            put("deviceId", deviceId);
        }});
        dispose();

        RetrofitService
                .getService()
                .getReports(object.toString())
                .map(new Function<String, ReportsResponse>() {
                    @Override
                    public ReportsResponse apply(@io.reactivex.annotations.NonNull String responseString) throws Exception {
                        Log.d("<==", responseString);
                        return new Gson().fromJson(responseString, ReportsResponse.class);
                    }
                })
                .doOnSuccess(new Consumer<ReportsResponse>() {
                    @Override
                    public void accept(@NonNull ReportsResponse reportsResponse) throws Exception {
                        if (reportsResponse.isSuccess()) {
                            ReportsModel.INSTANCE.setReportsList(reportsResponse.getData().getReports());
                            SettingsModel.INSTANCE.saveK(reportsResponse.getData().getK());
                            SettingsModel.INSTANCE.savePublicKey(reportsResponse.getData().getPublicKey());
                        } else {
                            EventBus.getDefault().post(new GetReportsEvent(false, reportsResponse.getMessage().isEmpty()
                                    ? App.getContext().getString(R.string.error)
                                    : reportsResponse.getMessage()));
                            throw new Exception("");
                        }
                    }
                })
                .subscribe(new SingleObserver<ReportsResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ReportsResponse reportsResponse) {
                        EventBus.getDefault().post(new GetReportsEvent(true));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("LoadReports error: ", e.getMessage());
                        if (e instanceof UnknownHostException) {
                            EventBus.getDefault().post(new GetReportsEvent(false, App.getContext()
                                    .getString(R.string.no_internet)));
                            return;
                        }
                        if (!e.getMessage().isEmpty()) {
                            EventBus.getDefault().post(new GetReportsEvent(false, App.getContext()
                                    .getString(R.string.error)));
                        }
                    }
                });
    }

    public void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
