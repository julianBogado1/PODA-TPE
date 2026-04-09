package ar.edu.itba.io;

import ar.edu.itba.business.models.SensorMetrics;

public record StringPrinter(SensorMetrics metrics) implements Printer {
    @Override
    public String print(SensorMetrics metrics) {
        var sb = new StringBuilder();
        sb.append("=== Sensor Metrics Report ===\n");
        sb.append("Total events processed: ").append(metrics.totalEvents()).append('\n');
        sb.append("Average speed: ").append(String.format("%.2f", metrics.avgSpeed())).append(" km/h\n");
        sb.append("Critical events: ").append(metrics.criticalEventCount()).append('\n');
        sb.append("--- Schema Distribution ---\n");
        sb.append("V1.0: ").append(metrics.v1EventCount()).append(" (").append(String.format("%.1f%%", metrics.getV1Proportion() * 100)).append(")\n");
        sb.append("V1.5: ").append(metrics.v1_5EventCount()).append(" (").append(String.format("%.1f%%", metrics.getV1_5Proportion() * 100)).append(")\n");
        sb.append("V2.0: ").append(metrics.v2EventCount()).append(" (").append(String.format("%.1f%%", metrics.getV2Proportion() * 100)).append(")\n");
        return sb.toString();
    }
}
