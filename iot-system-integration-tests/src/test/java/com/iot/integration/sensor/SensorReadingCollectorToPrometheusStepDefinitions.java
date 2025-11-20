package com.iot.integration.sensor;

import com.iot.model.SensorReading;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;

import static com.iot.integration.sensor.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SensorReadingCollectorToPrometheusStepDefinitions {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private SensorReading sensorReading;

    @Given("Sensor reading with readingId {string}, sensorId {string}, sensorType {string}, groupId {string} and value {double} published onto sensor reading topic")
    public void publishSensorReading(String readingId, String sensorId, String sensorType, String groupId, double value) throws IOException {
        Properties properties = PropertyLoader.loadProperties(TEST_PROPERTIES_FILENAME);

        Properties kafkaProperties = new Properties();
        kafkaProperties.put("bootstrap.servers", properties.getProperty(KAFKA_BOOTSTRAP_SERVERS_PROP));
        kafkaProperties.put("key.serializer", StringSerializer.class.getName());
        kafkaProperties.put("value.serializer", StringSerializer.class.getName());

        sensorReading = new SensorReading(
            readingId,
            sensorId,
            sensorType,
            groupId,
            value,
            System.currentTimeMillis()
        );

        try(Producer<String, String> producer = new KafkaProducer<>(kafkaProperties)) {
            String sensorReadingJsonString = objectMapper.writeValueAsString(sensorReading);

            ProducerRecord<String, String> record = new ProducerRecord<>(properties.getProperty(KAFKA_TOPIC_PROP), sensorId, sensorReadingJsonString);

            producer.send(record);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("Querying Prometheus should return the corresponding sample")
    public void queryPrometheusSample() throws URISyntaxException, IOException, InterruptedException {
        Properties properties = PropertyLoader.loadProperties(TEST_PROPERTIES_FILENAME);

        HttpClient httpClient = HttpClient.newHttpClient();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(properties.getProperty(PROMETHEUS_BASE_URL_PROP) + properties.getProperty(PROMETHEUS_QUERY_PATH_PROP)))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("query=sensor_value{reading_id=\"" + sensorReading.id() + "\"}&time=" + dateFormat.format(Date.from(Instant.ofEpochMilli(sensorReading.timestamp())))))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String body = response.body();
        assertTrue(body.contains("\"reading_id\":\"" + sensorReading.id() + "\""));
        assertTrue(body.contains("\"group_id\":\"" + sensorReading.groupId() + "\""));
        assertTrue(body.contains("\"sensor_id\":\"" + sensorReading.sensorId() + "\""));
        assertTrue(body.contains("\"sensor_type\":\"" + sensorReading.sensorType() + "\""));

        if(sensorReading.value() % 1 == 0) {
            assertTrue(body.contains(Integer.toString((int)Math.floor(sensorReading.value()))));
        } else {
            assertTrue(body.contains(Double.toString(sensorReading.value())));
        }
    }

}
