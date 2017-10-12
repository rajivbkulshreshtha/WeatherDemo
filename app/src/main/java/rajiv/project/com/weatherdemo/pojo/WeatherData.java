package rajiv.project.com.weatherdemo.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by rohit on 10/15/15.
 */

public class WeatherData {

    public static final Parcelable.Creator<WeatherData> CREATOR = new Parcelable.Creator<WeatherData>() {
        @Override
        public WeatherData createFromParcel(Parcel source) {
            return new WeatherData(source);
        }

        @Override
        public WeatherData[] newArray(int size) {
            return new WeatherData[size];
        }
    };
    private City city;
    private String cod;
    private String message;
    private Integer cnt;
    private java.util.List<List> list = new ArrayList<List>();

    public WeatherData() {
    }

    protected WeatherData(Parcel in) {
        this.city = in.readParcelable(City.class.getClassLoader());
        this.cod = in.readString();
        this.message = in.readString();
        this.cnt = (Integer) in.readValue(Integer.class.getClassLoader());
        this.list = new ArrayList<List>();
        in.readList(this.list, List.class.getClassLoader());
    }

    /**
     * @return The city
     */
    public City getCity() {
        return city;
    }

    /**
     * @param city The city
     */
    public void setCity(City city) {
        this.city = city;
    }

    /**
     * @return The cod
     */
    public String getCod() {
        return cod;
    }

    /**
     * @param cod The cod
     */
    public void setCod(String cod) {
        this.cod = cod;
    }

    /**
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return The cnt
     */
    public Integer getCnt() {
        return cnt;
    }

    /**
     * @param cnt The cnt
     */
    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    /**
     * @return The list
     */
    public java.util.List<List> getList() {
        return list;
    }

    /**
     * @param list The list
     */
    public void setList(java.util.List<List> list) {
        this.list = list;
    }

}