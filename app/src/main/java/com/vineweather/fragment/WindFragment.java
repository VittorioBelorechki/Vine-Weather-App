package com.vineweather.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vineweather.BroadcastActions;
import com.vineweather.R;
import com.vineweather.WeatherActivity;
import com.vineweather.adapter.HourlyWindAdapter;
import com.vineweather.helper.Helper;
import com.vineweather.model.HourForecast;
import com.vineweather.model.maindata.CurrentWeather;
import com.vineweather.model.maindata.Forecast;
import com.vineweather.singleton.AppManager;

import static com.vineweather.Constants.COLOR_DARK_BLUE;
import static com.vineweather.Constants.COLOR_GREEN;
import static com.vineweather.Constants.COLOR_LIGHT_BLUE;
import static com.vineweather.Constants.COLOR_RED;
import static com.vineweather.Constants.KM_H;
import static com.vineweather.Constants.MPH;

public class WindFragment extends WeatherFragment {

    private RecyclerView rvWind;
    private TextView condition, speed, unit;
    private ImageView conditionImage;
    private TextView currentWindSpeed, windDirection;
    protected Forecast forecast;
    private boolean isTomorrow;
    protected CurrentWeather currentWeather;

    public static WindFragment newInstance(boolean isTomorrow) {
        WindFragment fragment = new WindFragment();
        Bundle args = new Bundle();
        args.putBoolean("isTomorrow", isTomorrow);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        isTomorrow = getArguments().getBoolean("isTomorrow");
        if (isTomorrow) {
            view = inflater.inflate(R.layout.fragment_wind_tomorrow, container, false);
            rvWind = (RecyclerView) view.findViewById(R.id.tomorrow_hourly_wind);
            condition = (TextView) view.findViewById(R.id.tomorrow_wind_condition);
            speed = (TextView) view.findViewById(R.id.tomorrow_wind_speed);
        } else {
            view = inflater.inflate(R.layout.fragment_today_wind, container, false);
            rvWind = (RecyclerView) view.findViewById(R.id.rv_wind_today);
            condition = (TextView) view.findViewById(R.id.today_wind_condition);
            conditionImage = (ImageView) view.findViewById(R.id.today_wind_direction);
            currentWindSpeed = (TextView) view.findViewById(R.id.today_wind_speed);
            windDirection = (TextView) view.findViewById(R.id.today_wind_direction_string);
            unit = (TextView) view.findViewById(R.id.wind_today_speed_unit);
        }
        return view;
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

    private void bindData() {
        forecast = AppManager.getInstance().getCurrentLocation().getForecast();
        currentWeather = AppManager.getInstance().getCurrentLocation().getCurrentWeather();

        double maxWind = 0;
        double minWind = Integer.MAX_VALUE;
        double average;
        for (int i = 0; i < forecast.getDayForecasts().get(1).getHourForecasts().size(); i++) {
            HourForecast currentHour = forecast.getDayForecasts().get(1).getHourForecasts().get(i);
            if (WeatherActivity.isImperialUnits) {
                if (currentHour.getWindKph() > maxWind) {
                    maxWind = currentHour.getWindMph();
                }
                if (currentHour.getWindKph() < minWind) {
                    minWind = currentHour.getWindMph();
                }
            } else {
                if (currentHour.getWindKph() > maxWind) {
                    maxWind = currentHour.getWindKph();
                }
                if (currentHour.getWindKph() < minWind) {
                    minWind = currentHour.getWindKph();
                }
            }
        }
        rvWind.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        if (isTomorrow) {
            rvWind.setAdapter(new HourlyWindAdapter(forecast.getDayForecasts().get(1).getHourForecasts()));
            if (WeatherActivity.isImperialUnits) {
                speed.setText(Helper.decimalFormat(minWind).concat("-").concat(Helper.decimalFormat(maxWind).concat(" " + MPH)));
            } else {
                speed.setText(Helper.decimalFormat(minWind).concat("-").concat(Helper.decimalFormat(maxWind).concat(" " + KM_H)));
            }
            average = (minWind + maxWind) / 2;
            condition.setText(getCondition(average));
        } else {
            rvWind.setAdapter(new HourlyWindAdapter(forecast.getDayForecasts().get(0).getHourForecasts()));
            conditionImage.setRotation(currentWeather.getWindDegree());
            if (WeatherActivity.isImperialUnits) {
                condition.setText(getCondition(currentWeather.getWindMph()));
                currentWindSpeed.setText(Helper.decimalFormat(currentWeather.getWindMph()));
                unit.setText(MPH);
            } else {
                condition.setText(getCondition(currentWeather.getWindKph()));
                currentWindSpeed.setText(Helper.decimalFormat(currentWeather.getWindKph()));
                unit.setText(KM_H);
            }
            windDirection.setText(currentWeather.getWindDir());
            int windSpeed = (int) currentWeather.getWindKph();
            if (windSpeed <= 5) {
                currentWindSpeed.setTextColor(Color.parseColor(COLOR_LIGHT_BLUE));
            }
            if (windSpeed > 5 && windSpeed <= 20) {
                currentWindSpeed.setTextColor(Color.parseColor(COLOR_DARK_BLUE));
            }
            if (windSpeed > 20 && windSpeed <= 50) {
                currentWindSpeed.setTextColor(Color.parseColor(COLOR_GREEN));
            }
            if (windSpeed > 50 && windSpeed <= 250) {
                currentWindSpeed.setTextColor(Color.parseColor(COLOR_RED));
            }
        }
    }

    private String getCondition(double average) {
        if (average < 2) {
            return "Calm";
        }
        if (average > 1 && average < 7) {
            return "Light air";
        }
        if (average > 6 && average < 12) {
            return "Light breeze";
        }
        if (average > 11 && average < 21) {
            return "Gentle breeze";
        }
        if (average > 20 && average < 31) {
            return "Moderate breeze";
        }
        if (average > 30 && average < 41) {
            return "Fresh breeze";
        }
        if (average > 40 && average < 51) {
            return "Strong breeze";
        }
        if (average > 50 && average < 62) {
            return "Moderate gale";
        }
        if (average > 61 && average < 75) {
            return "Fresh gale";
        }
        if (average > 74 && average < 88) {
            return "Strong gale";
        }
        if (average > 87 && average < 103) {
            return "Whole gale";
        }
        if (average > 102 && average < 118) {
            return "Storm";
        }
        return "Hurricane";
    }
}
