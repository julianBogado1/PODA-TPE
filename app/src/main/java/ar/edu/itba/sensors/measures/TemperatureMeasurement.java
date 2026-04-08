package ar.edu.itba.sensors.measures;

public abstract class TemperatureMeasurement {

    protected Double measurement;

    public TemperatureMeasurement(Double measurement) {
        this.measurement = measurement;
    }

    public abstract Double toCelsius();
    public abstract Double toFahrenheit();
}
