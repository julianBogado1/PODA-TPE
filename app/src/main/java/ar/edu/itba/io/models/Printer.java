package ar.edu.itba.io.models;

import ar.edu.itba.business.models.SensorMetrics;

public interface Printer {
    String print(SensorMetrics metrics);
}
