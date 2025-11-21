package iot.sensor.query.client.prometheus.response;

import java.util.List;

public record PrometheusData(String resultType, List<PrometheusResult> result) { }