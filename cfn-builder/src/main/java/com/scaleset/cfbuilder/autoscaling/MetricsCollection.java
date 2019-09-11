package com.scaleset.cfbuilder.autoscaling;

public class MetricsCollection {

    private String granularity;
    private String[] metrics;

    public MetricsCollection(String granularity, String... metrics) {
        this.granularity = granularity;
        this.metrics = metrics;
    }

    public String getGranularity() {
        return granularity;
    }

    public String[] getMetrics() {
        return metrics;
    }

    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }

    public void setMetrics(String[] metrics) {
        this.metrics = metrics;
    }
}
