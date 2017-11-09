package rajiv.project.com.weatherdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import rajiv.project.com.weatherdemo.R;
import rajiv.project.com.weatherdemo.activity.DetailActivity;
import rajiv.project.com.weatherdemo.pojo.Main;
import rajiv.project.com.weatherdemo.pojo.Weather;
import rajiv.project.com.weatherdemo.pojo.WeatherData;
import rajiv.project.com.weatherdemo.util.Constants;

/**
 * Created by SUJAN on 11-Oct-17.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private WeatherData mWeatherData;
    private List<rajiv.project.com.weatherdemo.pojo.List> dayList = Collections.EMPTY_LIST;

    public MainAdapter(Context mContext, WeatherData weatherData, List<rajiv.project.com.weatherdemo.pojo.List> dayList) {
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.dayList = dayList;
        this.mWeatherData = weatherData;

    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.adapter_main, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {


        final Weather finalWeather = dayList.get(position).getWeather().get(0);
        final Main finalTemperature = dayList.get(position).getMain();

        holder.placeNameTextView.setText(mWeatherData.getCity().getName() + " , " + mWeatherData.getCity().getCountry());
        holder.descriptionTextView.setText(finalWeather.getDescription().toUpperCase());

        //Convert temp to fahrenheit
        double currentTemp = Constants.getTemp(mContext, finalTemperature.getTemp());
        double highestTemp = Constants.getTemp(mContext, finalTemperature.getTempMax());
        double lowestTemp = Constants.getTemp(mContext, finalTemperature.getTempMin());

        holder.currentTempTextView.setText(String.format("%.0f˚ ", currentTemp));
        holder.highestTempTextView.setText(String.format("%.0f˚ ", highestTemp));
        holder.lowestTempTextView.setText(String.format("%.0f˚ ", lowestTemp));

        String[] dateArray = dayList.get(position).getDtTxt().split(" ");
        final String date = dateArray[0].trim();
        String time = dateArray[1].trim();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate;
        try {
            startDate = df.parse(date);
            SimpleDateFormat sdf = new SimpleDateFormat("EE, dd/MM/yyyy");
            String newDateString = sdf.format(startDate);
            holder.dateTextView.setText(newDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.imageView.setImageResource(Constants.getImage(finalWeather.getIcon()));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, DetailActivity.class).putExtra("city", mWeatherData.getCity().
                        getName()).putExtra("date", date));
            }
        });

        holder.windTextView.setText(String.format("%.2f km/h", dayList.get(position).getWind().getSpeed()));
        holder.humidTextView.setText(String.format("%d %%", finalTemperature.getHumidity()));


    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }


    public class MainViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imageView;
        TextView placeNameTextView, dateTextView, descriptionTextView, currentTempTextView,
                highestTempTextView, lowestTempTextView, windTextView, humidTextView;

        public MainViewHolder(View itemView) {
            super(itemView);

            view = itemView;

            placeNameTextView = (TextView) view.findViewById(R.id.adapter_main_placeName);
            dateTextView = (TextView) view.findViewById(R.id.adapter_main_date);
            descriptionTextView = (TextView) view.findViewById(R.id.adapter_main_description);
            currentTempTextView = (TextView) view.findViewById(R.id.adapter_main_currentTemp);
            highestTempTextView = (TextView) view.findViewById(R.id.adapter_main_highest_temp);
            lowestTempTextView = (TextView) view.findViewById(R.id.adapter_main_lowest_temp);
            windTextView = (TextView) view.findViewById(R.id.adapter_main_wind_speed);
            humidTextView = (TextView) view.findViewById(R.id.adapter_main_humid);

            imageView = (ImageView) view.findViewById(R.id.adapter_main_imageView);

        }
    }

}
