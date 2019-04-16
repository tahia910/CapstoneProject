//package com.example.dailyupdate;
//
//import android.app.Application;
//
//public abstract class DailyUpdateApp extends Application {
//
//    private static AppComponent component;
//
//    public static AppComponent getComponent() {
//        return component;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        initializeApplication();
//        component = createComponent();
//    }
//
//    public AppComponent createComponent() {
//        return DaggerAppComponent.builder().androidModule(new AndroidModule(this)).build();
//    }
//
//    public abstract void initializeApplication();
//}
