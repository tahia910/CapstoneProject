package com.example.dailyupdate;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AndroidModule.class, LocationModule.class})
public interface AppComponent {

    LocationModule locationProvider();
}
