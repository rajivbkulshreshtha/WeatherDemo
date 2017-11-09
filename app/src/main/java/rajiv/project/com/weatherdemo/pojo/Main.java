package rajiv.project.com.weatherdemo.pojo;

/**
 * Created by rohit on 10/15/15.
 */
public class Main {

    private Double temp;
    private Double temp_min;
    private Double temp_max;
    private Double pressure;
    private Double seaLevel;
    private Double grndLevel;
    private Integer humidity;
    private Integer tempKf;

    /**
     *
     * @return
     * The temp
     */
    public Double getTemp() {
        return temp;
    }

    /**
     *
     * @param temp
     * The temp
     */
    public void setTemp(Double temp) {
        this.temp = temp;
    }

    /**
     *
     * @return
     * The temp_min
     */
    public Double getTempMin() {
        return temp_min;
    }

    /**
     *
     * @param temp_min
     * The temp_min
     */
    public void setTempMin(Double temp_min) {
        this.temp_min = temp_min;
    }

    /**
     *
     * @return
     * The temp_max
     */
    public Double getTempMax() {
        return temp_max;
    }

    /**
     *
     * @param temp_max
     * The temp_max
     */
    public void setTempMax(Double temp_max) {
        this.temp_max = temp_max;
    }

    /**
     *
     * @return
     * The pressure
     */
    public Double getPressure() {
        return pressure;
    }

    /**
     *
     * @param pressure
     * The pressure
     */
    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    /**
     *
     * @return
     * The seaLevel
     */
    public Double getSeaLevel() {
        return seaLevel;
    }

    /**
     *
     * @param seaLevel
     * The sea_level
     */
    public void setSeaLevel(Double seaLevel) {
        this.seaLevel = seaLevel;
    }

    /**
     *
     * @return
     * The grndLevel
     */
    public Double getGrndLevel() {
        return grndLevel;
    }

    /**
     *
     * @param grndLevel
     * The grnd_level
     */
    public void setGrndLevel(Double grndLevel) {
        this.grndLevel = grndLevel;
    }

    /**
     *
     * @return
     * The humidity
     */
    public Integer getHumidity() {
        return humidity;
    }

    /**
     *
     * @param humidity
     * The humidity
     */
    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    /**
     *
     * @return
     * The tempKf
     */
    public Integer getTempKf() {
        return tempKf;
    }

    /**
     *
     * @param tempKf
     * The temp_kf
     */
    public void setTempKf(Integer tempKf) {
        this.tempKf = tempKf;
    }

}