Feature: Testing integration between Sensor Query Service and Prometheus
  Scenario: Multiple sensor reading time series are written to Prometheus
    Given a reading with readingId "dec456", sensorId "hrm01", sensorType "HEART_RATE_MONITOR", groupId "health", value 110 and current timestamp
    And wait 1000 ms
    Given a reading with readingId "dec789", sensorId "hrm01", sensorType "HEART_RATE_MONITOR", groupId "health", value 135 and current timestamp
    When corresponding samples are written to Prometheus
    And wait 50 ms
    Then sending a sensor query request should return an appropriate response
    And sending a sensor group query request should return an appropriate response