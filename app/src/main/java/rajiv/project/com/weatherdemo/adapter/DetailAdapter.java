package rajiv.project.com.weatherdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;

import rajiv.project.com.weatherdemo.R;
import rajiv.project.com.weatherdemo.pojo.List;
import rajiv.project.com.weatherdemo.pojo.Main;
import rajiv.project.com.weatherdemo.pojo.Weather;
import rajiv.project.com.weatherdemo.pojo.WeatherData;
import rajiv.project.com.weatherdemo.util.Constants;

/**
 * Created by SUJAN on 12-Oct-17.
 */

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private WeatherData weatherData;
    private java.util.List<List> dayList = Collections.EMPTY_LIST;

    public DetailAdapter(Context mContext, WeatherData weatherData, java.util.List<List> dayList) {
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.dayList = dayList;
        this.weatherData = weatherData;

    }

    @Override
    public DetailAdapter.DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.adapter_detail, parent, false);
        return new DetailAdapter.DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailAdapter.DetailViewHolder holder, int position) {


        final Weather finalWeather = dayList.get(position).getWeather().get(0);
        final Main finalTemperature = dayList.get(position).getMain();
        holder.placeNameTextView.setText(weatherData.getCity().getName() + " , " + weatherData.getCity().getCountry());

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
        final String time = dateArray[1].trim();
        holder.dtTextView.setText(time.substring(0, 5));
        holder.imageView.setImageResource(Constants.getImage(finalWeather.getIcon()));

        holder.windTextView.setText(String.format("%.2f km/h", dayList.get(position).getWind().getSpeed()));
        holder.humidTextView.setText(String.format("%d %%", finalTemperature.getHumidity()));

    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }


    public class DetailViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imageView;
        TextView placeNameTextView, dtTextView, descriptionTextView, currentTempTextView,
                highestTempTextView, lowestTempTextView, windTextView, humidTextView;


        public DetailViewHolder(View itemView) {
            super(itemView);

            view = itemView;

            placeNameTextView = (TextView) view.findViewById(R.id.adapter_detail_location_textView);
            dtTextView = (TextView) view.findViewById(R.id.adapter_detail_dt_textView);
            descriptionTextView = (TextView) view.findViewById(R.id.adapter_detail_description);
            currentTempTextView = (TextView) view.findViewById(R.id.adapter_detail_currentTemp_textView);
            highestTempTextView = (TextView) view.findViewById(R.id.adapter_detail_highest_temp);
            lowestTempTextView = (TextView) view.findViewById(R.id.adapter_detail_lowest_temp);
            windTextView = (TextView) view.findViewById(R.id.adapter_detail_wind_speed);
            humidTextView = (TextView) view.findViewById(R.id.adapter_detail_humid);

            imageView = (ImageView) view.findViewById(R.id.adapter_detail_imageView);

        }
    }

}