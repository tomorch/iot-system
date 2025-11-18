package iot.sensor.query.client.prometheus.response;

public record PrometheusMetric(String __name__, String sensor_id, String sensor_type, String group_id, String reading_id) { }