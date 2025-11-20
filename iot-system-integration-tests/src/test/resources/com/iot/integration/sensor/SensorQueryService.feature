Feature: Testing integration between Sensor Query Service and Prometheus
  Scenario: Multiple sensor reading time series are written to Prometheus
    Given Sensor reading sample with readingId "dec456", sensorId "hm2", sensorType "HEART_RATE_MONITOR", groupId "medical" and value 110 written to Prometheus
    And Wait 1000 ms
    And Sensor reading sample with readingId "dec789", sensorId "hm2", sensorType "HEART_RATE_MONITOR", groupId "medical" and value 135 written to Prometheus
    And Wait 50 ms
    Then Sending a sensor query request should return an appropriate response