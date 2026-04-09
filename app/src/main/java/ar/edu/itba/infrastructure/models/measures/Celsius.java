package ar.edu.itba.infrastructure.models.measures;

public class Celsius extends TemperatureMeasurement {

    public Celsius(Double measure) {
        super(measure);
    }

    @Override
    public Double toCelsius() {
        return super.measurement;
    }

    @Override
    public Double toFahrenheit() {
        return (measurement*9/5) + 32;
    }
}
