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
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vineweather.BroadcastActions;
import com.vineweather.R;
import com.vineweather.WeatherActivity;
import com.vineweather.adapter.HourlyTempAdapter;
import com.vineweather.helper.Helper;
import com.vineweather.model.DayDetails;
import com.vineweather.model.HourForecast;
import com.vineweather.singleton.AppManager;

import java.util.ArrayList;

import static com.vineweather.Constants.ARROW_DOWN;
import static com.vineweather.Constants.ARROW_UP;
import static com.vineweather.Constants.CELSIUS_SYMBOL;
import static com.vineweather.Constants.DAY;
import static com.vineweather.Constants.INTERPUNKT;
import static com.vineweather.Constants.NIGHT;

public class FragmentTomorrow extends WeatherFragment {

    protected RelativeLayout mainLayout;
    private RecyclerView hourlyTempForecast;
    protected DayDetails tomorrow;
    private TextView date, condition, temp;
    private ImageView conditionImage;
    protected ArrayList<HourForecast> tomorrowHourly;
    protected FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tomorrow, null, false);
        hourlyTempForecast = (RecyclerView) view.findViewById(R.id.tomorrow_hourly_temp);
        date = (TextView) view.findViewById(R.id.tomorrow_date);
        condition = (TextView) view.findViewById(R.id.tomorrow_condition);
        temp = (TextView) view.findViewById(R.id.tomorrow_max_min);
        conditionImage = (ImageView) view.findViewById(R.id.tomorrow_image);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int screenHeight = size.y;
        mainLayout = (RelativeLayout) getActivity().findViewById(R.id.main_layout_tomorrow);
        LayoutParams params = (LayoutParams) mainLayout.getLayoutParams();
        AppBarLayout appbar = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
        int appbarHeight = appbar.getHeight();
        params.height = screenHeight - getStatusBarHeight() - appbarHeight;

        fragmentManager = getChildFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.tomorrow_details_container, new FragmentTomorrowDetails());
        fragmentTransaction.add(R.id.tomorrow_wind_container, WindFragment.newInstance(true));
        fragmentTransaction.add(R.id.tomorrow_precipitation_container, PrecipitationFragment.newInstance(true));
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
        tomorrowHourly = AppManager.getInstance().getCurrentLocation().getForecast().getDayForecasts().get(1).getHourForecasts();
        tomorrow = AppManager.getInstance().getCurrentLocation().getForecast().getDayForecasts().get(1).getDayDetails();
        int unixTS = AppManager.getInstance().getCurrentLocation().getForecast().getDayForecasts().get(1).getDateEpoch();
        date.setText(Helper.getWeekDay(Helper.getUnixDate(unixTS)).concat(Helper.getUnixCustomDate(unixTS)));
        condition.setText(tomorrow.getCondition().getText());
        if (WeatherActivity.isImperialUnits) {
            temp.setText(DAY
                    .concat(String.valueOf(Helper.decimalFormat(tomorrow.getMaxtempF())
                            .concat(CELSIUS_SYMBOL).concat(ARROW_UP).concat(INTERPUNKT).concat(NIGHT)
                            .concat(Helper.decimalFormat(tomorrow.getMintempF()).concat(CELSIUS_SYMBOL).concat(ARROW_DOWN)))));
        } else {
            temp.setText(DAY
                    .concat(String.valueOf(Helper.decimalFormat(tomorrow.getMaxtempC())
                            .concat(CELSIUS_SYMBOL).concat(ARROW_UP).concat(INTERPUNKT).concat(NIGHT)
                            .concat(Helper.decimalFormat(tomorrow.getMintempC()).concat(CELSIUS_SYMBOL).concat(ARROW_DOWN)))));
        }
        conditionImage.setImageDrawable(Helper.chooseConditionIcon(conditionImage.getContext(), true, false, tomorrow.getCondition().getText()));
        hourlyTempForecast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        hourlyTempForecast.setAdapter(new HourlyTempAdapter(tomorrowHourly, 1));
        mainLayout.setBackground(Helper.chooseFragmentBackground(mainLayout.getContext(), tomorrow.getCondition().getText(), true));
    }
}
