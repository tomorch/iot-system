package iot.sensor.query.client.prometheus.response;

public record PrometheusQueryResponse(String status, PrometheusData data) { }