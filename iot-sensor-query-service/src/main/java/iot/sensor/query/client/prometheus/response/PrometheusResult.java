package iot.sensor.query.client.prometheus.response;

public record PrometheusResult(PrometheusMetric metric, PrometheusValue[] values) { }