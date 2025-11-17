package com.iot.simulator.service;

import com.iot.model.SensorReading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void publishReading(SensorReading reading, String topic) {
        sendToTopic(topic, reading);
    }

    private void sendToTopic(String topic, SensorReading reading) {
        try {
            Message<SensorReading> message = MessageBuilder
                    .withPayload(reading)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(KafkaHeaders.KEY, reading.deviceId())
                    .build();

            kafkaTemplate.send(message);
            logger.debug("Published {} from device {} to topic {}",
                    reading, reading.deviceId(), topic);
        } catch (Exception e) {
            logger.error("Failed to publish reading to topic {}: {}", topic, e.getMessage(), e);
        }
    }

}
