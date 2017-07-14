package com.neoquest.voting.interactor;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.neoquest.voting.App;
import com.neoquest.voting.R;
import com.neoquest.voting.crypt.Encrypter;
import com.neoquest.voting.eventbus.VoteEvent;
import com.neoquest.voting.model.entity.Response;
import com.neoquest.voting.model.entity.VoteRequest;
import com.neoquest.voting.service.RetrofitService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.HashMap;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class VoteInteractor {
    private Disposable disposable;

    public void vote(final String deviceId, final String hashValue, final String publicKey, final String k) {
        Single.create(new SingleOnSubscribe<VoteRequest>() {
            @Override
            public void subscribe(SingleEmitter<VoteRequest> e) throws Exception {
                try {
                    final VoteRequest voteREquest = new Encrypter().encrypt(hashValue, publicKey, k);
                    voteREquest.setDeviceId(deviceId);
                    e.onSuccess(voteREquest);
                } catch (Exception ex) {
                    e.onError(ex);
                }
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<VoteRequest>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull VoteRequest voteRequest) {
                        vote(voteRequest);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e("Encrypt error: ", e.getMessage());
                        EventBus.getDefault().post(new VoteEvent(false, App.getContext()
                                .getString(R.string.error)));

                    }
                });
    }

    private void vote(final VoteRequest voteRequest) {
        final JSONObject object = new JSONObject(new HashMap<String, Object>() {{
            put("cipher", voteRequest.getCipher());
            put("image", voteRequest.getImage());
            put("deviceId", voteRequest.getDeviceId());
        }});
        dispose();

        Log.d("==>", object.toString());

        RetrofitService
                .getService()
                .vote(object.toString())
                .map(new Function<String, Response>() {
                    @Override
                    public Response apply(@io.reactivex.annotations.NonNull String responseString) throws Exception {
                        Log.d("<==", responseString);
                        return new Gson().fromJson(responseString, Response.class);
                    }
                })
                .doOnSuccess(new Consumer<Response>() {
                    @Override
                    public void accept(@NonNull Response response) throws Exception {
                        if (!response.isSuccess()) {
                            EventBus.getDefault().post(new VoteEvent(false, response.getMessage().isEmpty()
                                    ? App.getContext().getString(R.string.error)
                                    : response.getMessage()));
                            throw new Exception("");
                        }
                    }
                })
                .subscribe(new SingleObserver<Response>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Response response) {
                        EventBus.getDefault().post(new VoteEvent(true));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Vote error: ", e.getMessage());
                        if (e instanceof UnknownHostException) {
                            EventBus.getDefault().post(new VoteEvent(false, App.getContext()
                                    .getString(R.string.no_internet)));
                            return;
                        }
                        if (!e.getMessage().isEmpty()) {
                            EventBus.getDefault().post(new VoteEvent(false, App.getContext()
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
