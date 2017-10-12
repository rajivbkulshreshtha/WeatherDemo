package rajiv.project.com.weatherdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import rajiv.project.com.weatherdemo.R;
import rajiv.project.com.weatherdemo.activity.DetailActivity;
import rajiv.project.com.weatherdemo.pojo.Main;
import rajiv.project.com.weatherdemo.pojo.Weather;
import rajiv.project.com.weatherdemo.pojo.WeatherData;

/**
 * Created by SUJAN on 11-Oct-17.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private Context mContext;
    private LayoutInflater layoutInflater;
    private WeatherData weatherData;
    private List<rajiv.project.com.weatherdemo.pojo.List> dayList = Collections.EMPTY_LIST;

    public MainAdapter(Context mContext, WeatherData weatherData, List<rajiv.project.com.weatherdemo.pojo.List> dayList) {
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(this.mContext);
        this.dayList = dayList;
        this.weatherData = weatherData;

    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.adapter_main, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {


        final Weather weather_ = dayList.get(position).getWeather().get(0);
        final Main temperature = dayList.get(position).getMain();
        holder.placeNameTextView.setText(weatherData.getCity().getName() + " , " + weatherData.getCity().getCountry());
        holder.descriptionTextView.setText(weather_.getDescription().toUpperCase());

        //Convert temp to fahrenheit
        double temp = temperature.getTemp();
        temp = temp * (9.0 / 5) - 459.67;
        String tempConv = String.format("%.0f", temp);
        holder.currentTempTextView.setText(tempConv + "˚");

        String[] dateArray = dayList.get(position).getDtTxt().split(" ");
        final String date = dateArray[0].trim();
        String time = dateArray[1].trim();

        holder.dateTextView.setText(date);
        holder.timeTextView.setText(time);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, DetailActivity.class).putExtra("city", weatherData.getCity().
                        getName()).putExtra("date", date));
            }
        });


        switch (weather_.getIcon()) {
            case "01d":
                holder.iconTextView.setText(R.string.wi_day_sunny);
                break;
            case "02d":
                holder.iconTextView.setText(R.string.wi_cloudy_gusts);
                break;
            case "03d":
                holder.iconTextView.setText(R.string.wi_cloud_down);
                break;
            case "04d":
                holder.iconTextView.setText(R.string.wi_cloudy);
                break;
            case "04n":
                holder.iconTextView.setText(R.string.wi_night_cloudy);
                break;
            case "10d":
                holder.iconTextView.setText(R.string.wi_day_rain_mix);
                break;
            case "11d":
                holder.iconTextView.setText(R.string.wi_day_thunderstorm);
                break;
            case "13d":
                holder.iconTextView.setText(R.string.wi_day_snow);
                break;
            case "01n":
                holder.iconTextView.setText(R.string.wi_night_clear);
                break;
            case "02n":
                holder.iconTextView.setText(R.string.wi_night_cloudy);
                break;
            case "03n":
                holder.iconTextView.setText(R.string.wi_night_cloudy_gusts);
                break;
            case "10n":
                holder.iconTextView.setText(R.string.wi_night_cloudy_gusts);
                break;
            case "11n":
                holder.iconTextView.setText(R.string.wi_night_rain);
                break;
            case "13n":
                holder.iconTextView.setText(R.string.wi_night_snow);
                break;
        }


        // holder.view.setVisibility(View.GONE);


    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }


    public class MainViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView dateTextView, timeTextView, placeNameTextView, descriptionTextView, currentTempTextView, iconTextView;

        public MainViewHolder(View itemView) {
            super(itemView);

            view = itemView;

            dateTextView = (TextView) view.findViewById(R.id.adapter_date);
            timeTextView = (TextView) view.findViewById(R.id.adapter_time);
            placeNameTextView = (TextView) view.findViewById(R.id.adapter_placeName);
            descriptionTextView = (TextView) view.findViewById(R.id.adapter_description);
            currentTempTextView = (TextView) view.findViewById(R.id.adapter_currentTime);
            iconTextView = (TextView) view.findViewById(R.id.adapter_iconTextView);

        }
    }

}
