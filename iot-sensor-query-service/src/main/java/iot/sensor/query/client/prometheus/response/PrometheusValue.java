package iot.sensor.query.client.prometheus.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import iot.sensor.query.client.prometheus.PrometheusValueDeserializer;

@JsonDeserialize(using = PrometheusValueDeserializer.class)
public record PrometheusValue(long timestamp, String value) { }