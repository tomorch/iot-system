Feature: Testing integration between Sensor Reading Collector and Prometheus
  Scenario: A sensor reading is published onto the sensor reading topic
    Given a sensor reading with readingId "abc123", sensorId "hrm02", sensorType "HEART_RATE_MONITOR", groupId "lifestyle", value 120 and current timestamp
    When the reading is published onto the sensor reading topic
    Then wait 500 ms
    Then querying Prometheus should return the corresponding sample