package com.vineweather.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vineweather.BroadcastActions;
import com.vineweather.Constants;
import com.vineweather.R;
import com.vineweather.WeatherActivity;
import com.vineweather.adapter.HourlyTempAdapter;
import com.vineweather.helper.Helper;
import com.vineweather.model.DayForecast;
import com.vineweather.model.maindata.Forecast;
import com.vineweather.model.maindata.Location;
import com.vineweather.singleton.AppManager;

import static com.vineweather.Constants.FEELS_LIKE;
import static com.vineweather.Constants.LAST_UPDATED;

public class FragmentToday extends WeatherFragment {

    protected Forecast forecast;
    private RelativeLayout parent;
    protected DayForecast currentDay;
    private Location currentLocation;
    private RecyclerView recyclerView;
    protected ImageView weatherIcon, windDirection;
    protected TextView degrees, condition, windCondition, windSpeed, feelsLike, lastUpdated, deegreType;
    protected FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, null, false);
        windSpeed = (TextView) view.findViewById(R.id.today_wind_speed);
        degrees = (TextView) view.findViewById(R.id.fragment_1_degrees);
        weatherIcon = (ImageView) view.findViewById(R.id.fragment_1_image);
        condition = (TextView) view.findViewById(R.id.fragment_1_condition);
        feelsLike = (TextView) view.findViewById(R.id.fragment_1_feels_like);
        windCondition = (TextView) view.findViewById(R.id.today_wind_condition);
        windDirection = (ImageView) view.findViewById(R.id.today_wind_direction);
        recyclerView = (RecyclerView) view.findViewById(R.id.layout_rv_hours_forecast);
        lastUpdated = (TextView) view.findViewById(R.id.last_updated_tv);
        deegreType = (TextView) view.findViewById(R.id.degree_symbol);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int screenHeight = size.y;
        parent = (RelativeLayout) getActivity().findViewById(R.id.today_parent);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) parent.getLayoutParams();
        AppBarLayout appbar = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
        int appbarHeight = appbar.getHeight();
        params.height = screenHeight - getStatusBarHeight() - appbarHeight;

        fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentTodayDetails fragmentCurrentDetails = new FragmentTodayDetails();

        fragmentTransaction.add(R.id.layout_current_detail, fragmentCurrentDetails);
        fragmentTransaction.add(R.id.layout_wind_detail, WindFragment.newInstance(false));
        fragmentTransaction.add(R.id.layout_precip_detail, PrecipitationFragment.newInstance(false));
        fragmentTransaction.commit();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void bindData() {
        currentLocation = AppManager.getInstance().getCurrentLocation();

        forecast = AppManager.getInstance().getCurrentLocation().getForecast();
        currentDay = forecast.getDayForecasts().get(0);

        lastUpdated.setText(LAST_UPDATED.concat(AppManager.getInstance().getCurrentLocation().getCurrentWeather().getLastUpdated()));
        weatherIcon.setImageDrawable(Helper.chooseConditionIcon(weatherIcon.getContext(),
                currentLocation.getCurrentWeather().getIsDay() == 1, false,
                currentLocation.getCurrentWeather().getCondition().getText()));

        if (WeatherActivity.isImperialUnits) {
            degrees.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getTempF()).concat(Constants.CELSIUS_SYMBOL));
            feelsLike.setText(FEELS_LIKE.concat(Helper.decimalFormat(currentLocation.getCurrentWeather().getFeelslikeF())).concat(Constants.CELSIUS_SYMBOL));
            deegreType.setText(Constants.FAHRENHEIT);
        } else {
            degrees.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getTempC()).concat(Constants.CELSIUS_SYMBOL));
            feelsLike.setText(FEELS_LIKE.concat(Helper.decimalFormat(currentLocation.getCurrentWeather().getFeelslikeC())).concat(Constants.CELSIUS_SYMBOL));
            deegreType.setText(Constants.CELSIUS);
        }

        condition.setText(currentLocation.getCurrentWeather().getCondition().getText());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new HourlyTempAdapter(currentDay.getHourForecasts(), 0));

        parent.setBackgroundDrawable(Helper.chooseFragmentBackground(parent.getContext(),
                currentLocation.getCurrentWeather().getCondition().getText(),
                currentLocation.getCurrentWeather().getIsDay() == 1));
    }

    @Override
    protected BroadcastReceiver getReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (AppManager.getInstance().getCurrentLocation() != null) {
                    if (intent.getAction().equals(BroadcastActions.ACTION_UNIT_SWAPPED) || intent.getAction().equals(BroadcastActions.ACTION_LOCATION_UPDATED)) {
                        bindData();
                    }
                }
            }
        };
    }
}
