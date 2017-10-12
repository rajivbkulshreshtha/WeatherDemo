package rajiv.project.com.weatherdemo.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by rohit on 10/15/15.
 */
public class List implements Parcelable {

    public static final Parcelable.Creator<List> CREATOR = new Parcelable.Creator<List>() {
        @Override
        public List createFromParcel(Parcel source) {
            return new List(source);
        }

        @Override
        public List[] newArray(int size) {
            return new List[size];
        }
    };
    private Integer dt;
    private Main main;
    private java.util.List<Weather> weather = new ArrayList<Weather>();
    private Clouds clouds;
    private Wind wind;
    private Rain rain;
    private Sys_ sys;
    private String dt_txt;

    public List() {
    }

    protected List(Parcel in) {
        this.dt = (Integer) in.readValue(Integer.class.getClassLoader());
        this.main = in.readParcelable(Main.class.getClassLoader());
        this.weather = new ArrayList<Weather>();
        in.readList(this.weather, Weather.class.getClassLoader());
        this.clouds = in.readParcelable(Clouds.class.getClassLoader());
        this.wind = in.readParcelable(Wind.class.getClassLoader());
        this.rain = in.readParcelable(Rain.class.getClassLoader());
        this.sys = in.readParcelable(Sys_.class.getClassLoader());
        this.dt_txt = in.readString();
    }

    /**
     * @return The dt
     */
    public Integer getDt() {
        return dt;
    }

    /**
     * @param dt The dt
     */
    public void setDt(Integer dt) {
        this.dt = dt;
    }

    /**
     * @return The main
     */
    public Main getMain() {
        return main;
    }

    /**
     * @param main The main
     */
    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * @return The weather
     */
    public java.util.List<Weather> getWeather() {
        return weather;
    }

    /**
     * @param weather The weather
     */
    public void setWeather(java.util.List<Weather> weather) {
        this.weather = weather;
    }

    /**
     * @return The clouds
     */
    public Clouds getClouds() {
        return clouds;
    }

    /**
     * @param clouds The clouds
     */
    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    /**
     * @return The wind
     */
    public Wind getWind() {
        return wind;
    }

    /**
     * @param wind The wind
     */
    public void setWind(Wind wind) {
        this.wind = wind;
    }

    /**
     * @return The rain
     */
    public Rain getRain() {
        return rain;
    }

    /**
     * @param rain The rain
     */
    public void setRain(Rain rain) {
        this.rain = rain;
    }

    /**
     * @return The sys
     */
    public Sys_ getSys() {
        return sys;
    }

    /**
     * @param sys The sys
     */
    public void setSys(Sys_ sys) {
        this.sys = sys;
    }

    /**
     * @return The dt_txt
     */
    public String getDtTxt() {
        return dt_txt;
    }

    /**
     * @param dt_txt The dt_txt
     */
    public void setDtTxt(String dt_txt) {
        this.dt_txt = dt_txt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.dt);
        dest.writeParcelable((Parcelable) this.main, flags);
        dest.writeList(this.weather);
        dest.writeParcelable((Parcelable) this.clouds, flags);
        dest.writeParcelable((Parcelable) this.wind, flags);
        dest.writeParcelable((Parcelable) this.rain, flags);
        dest.writeParcelable((Parcelable) this.sys, flags);
        dest.writeString(this.dt_txt);
    }
}