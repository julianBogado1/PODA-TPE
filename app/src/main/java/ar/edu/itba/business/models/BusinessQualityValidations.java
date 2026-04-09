package ar.edu.itba.business.models;

public class BusinessQualityValidations {
    private static final Double MIN_SPEED = 0.0;
    private static final Double MAX_SPEED = 300.0;
    private static final Double MAX_TEMP = 100.0;
    private static final Double MIN_TEMP = -30.0;
    private static final Double MIN_HUMIDITY = 0.0;
    private static final Double MAX_HUMIDITY = 100.0;
    private static final Double CRITICAL_MAX_TEMP = 50.0;
    private static final Double CRITICAL_MIN_TEMP = -10.0;
    private static final Double CRITICAL_MAX_SPEED = 200.0;





    public boolean isCriticalSpeed(Double speed){
        return speed!=null && valueBetween(speed, MIN_SPEED, CRITICAL_MAX_SPEED);
    }
    public boolean isCriticalTemp(Double temp){
        return temp!=null && valueBetween(temp, CRITICAL_MIN_TEMP, CRITICAL_MAX_TEMP);
    }
    /*
    * Business logic dictates all report type measures are critical
    * */
    public boolean isCriticalReport(){
        return true;
    }
    public boolean isTemperatureValid(Double temp){
        return temp!=null && valueBetween(temp, MIN_TEMP, MAX_TEMP);
    }
    public boolean isSpeedValid(Double speed){
        return speed!=null && valueBetween(speed, MIN_SPEED, MAX_SPEED);
    }
    public boolean isHumidityValid(Double humidity){
        return humidity!= null && valueBetween(humidity, MIN_HUMIDITY, MAX_HUMIDITY);
    }
    public boolean valueBetween(Double val, Double min, Double max){
        return min <= val &&  val <= max;
    }

}
