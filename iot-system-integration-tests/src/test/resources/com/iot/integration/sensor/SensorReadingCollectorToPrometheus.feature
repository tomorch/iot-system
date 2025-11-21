Feature: Testing integration between Sensor Reading Collector and Prometheus
  Scenario: A sensor reading is published onto the sensor reading topic
    When Sensor reading with readingId "abc123", sensorId "hrm02", sensorType "HEART_RATE_MONITOR", groupId "lifestyle" and value 120 published onto sensor reading topic
    Then Wait 500 ms
    Then Querying Prometheus should return the corresponding sample