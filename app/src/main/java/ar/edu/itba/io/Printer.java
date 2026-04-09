package ar.edu.itba.io;

import ar.edu.itba.business.models.SensorMetrics;

public interface Printer {
    String print(SensorMetrics metrics);
}
