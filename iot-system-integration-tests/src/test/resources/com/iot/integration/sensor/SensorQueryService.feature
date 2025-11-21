Feature: Testing integration between Sensor Query Service and Prometheus
  Scenario: Multiple sensor reading time series are written to Prometheus
    Given Sensor reading sample with readingId "dec456", sensorId "hrm01", sensorType "HEART_RATE_MONITOR", groupId "health" and value 110 written to Prometheus
    And Wait 1000 ms
    And Sensor reading sample with readingId "dec789", sensorId "hrm01", sensorType "HEART_RATE_MONITOR", groupId "health" and value 135 written to Prometheus
    And Wait 50 ms
    Then Sending a sensor query request should return an appropriate response
    And Sending a sensor group query request should return an appropriate response