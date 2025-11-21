package iot.sensor.query.client.prometheus;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import iot.sensor.query.client.prometheus.response.PrometheusValue;

import java.io.IOException;

public class PrometheusValueDeserializer extends StdDeserializer<PrometheusValue> {
    public PrometheusValueDeserializer() {
        super(PrometheusValue.class);
    }

    protected PrometheusValueDeserializer(Class<?> vc) {
        super(vc);
    }

    protected PrometheusValueDeserializer(JavaType valueType) {
        super(valueType);
    }

    protected PrometheusValueDeserializer(StdDeserializer<?> src) {
        super(src);
    }

    @Override
    public PrometheusValue deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        if (node.isArray() && node.size() == 2) {
            Double timestamp = node.get(0).asDouble();
            String value = node.get(1).asText();
            return new PrometheusValue(timestamp.longValue(), value);
        } else {
            throw new RuntimeException("Invalid PrometheusValue format");
        }
    }
}
