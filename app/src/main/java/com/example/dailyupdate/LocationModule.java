package com.example.dailyupdate;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import dagger.Module;
import dagger.Provides;

@Module
public class LocationModule implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LocationModule.class.getSimpleName();

    GoogleApiClient googleApiClient;
    GoogleApiClient.ConnectionCallbacks callback;
    GoogleApiClient.OnConnectionFailedListener listener;

//    @Inject
    public LocationModule(GoogleApiClient.ConnectionCallbacks callback,
                          GoogleApiClient.OnConnectionFailedListener listener) {
        this.callback = callback;
        this.listener = listener;
        Log.e(TAG, "LocationModule: Got instance");
    }

    @Provides
    @Singleton
    GoogleApiClient providesGoogleApi(Context context) {
        return new GoogleApiClient.Builder(context)
                .addOnConnectionFailedListener(listener)
                .addConnectionCallbacks(callback)
                .addApi(LocationServices.API)
                .build();
    }

//    @Provides
//    GoogleApiClient.ConnectionCallbacks providesCallbacks() {
//        return new GoogleApiClient.ConnectionCallbacks() {
//            @Override
//            public void onConnected(@Nullable Bundle bundle) {
//
//            }
//
//            @Override
//            public void onConnectionSuspended(int i) {
//
//            }
//        };
//    }

//    @Provides
//    GoogleApiClient.OnConnectionFailedListener providesListener() {
//        return new GoogleApiClient.OnConnectionFailedListener() {
//            @Override
//            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//            }
//        };
//    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        googleApiClient.connect();
        Log.i(TAG, "API Client Connection Successful.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "API Client Connection Suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "API Client Connection Failed.");
    }
}
