package com.neoquest.voting;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;

import com.neoquest.voting.router.Router;

public class App extends Application {
    private static App instance;
    private static Router appRouter;
    private static String deviceId;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static Router getAppRouter() {
        return appRouter;
    }

    public static void setAppRouter(Router router) {
        appRouter = router;
    }

    public static String getDeviceId() {
        if (deviceId == null) {
            deviceId = Settings.Secure.getString(App.getContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        return deviceId;
    }
}
