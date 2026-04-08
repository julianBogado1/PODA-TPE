package ar.edu.itba.sensors.measures;

public class Farenheit extends TemperatureMeasurement {

    public Farenheit(Double measurement) {
        super(measurement);
    }
    @Override
    public Double toFahrenheit() {
        return measurement;
    }

    @Override
    public Double toCelsius() {
        return (measurement - 32) * 5/9;
    }
}
