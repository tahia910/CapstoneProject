//package com.example.dailyupdate.di;
//
//import android.content.Context;
//
//import javax.inject.Singleton;
//
//import dagger.Module;
//import dagger.Provides;
//
//@Module
//public class AndroidModule {
//    private DailyUpdateApp application;
//
//    public AndroidModule(DailyUpdateApp application) {
//        this.application = application;
//    }
//
//    @Provides
//    @Singleton
//    public Context provideContext() {
//        return application.getApplicationContext();
//    }
//
//}
