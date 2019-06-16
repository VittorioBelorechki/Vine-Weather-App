package com.vineweather.singleton;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.vineweather.BroadcastActions;
import com.vineweather.Constants;
import com.vineweather.WeatherActivity;
import com.vineweather.model.maindata.Location;

public class AppManager {

    private static AppManager instance;

    private Location currentLocation = new Location();

    private AppManager() {
    }

    public static AppManager getInstance() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void onLocationUpdated(Context context) {
        Intent intent = new Intent(BroadcastActions.ACTION_LOCATION_UPDATED);
        intent.putExtra(Constants.KEY_LOCATION_NAME, currentLocation.getName());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void onUnitSwapped(Context context) {
        Intent intent = new Intent(BroadcastActions.ACTION_UNIT_SWAPPED);
        intent.putExtra(Constants.UNITS_IMPERIAL, WeatherActivity.isImperialUnits);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
