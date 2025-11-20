Feature: Testing integration between Sensor Reading Collector and Prometheus
  Scenario: A sensor reading is published onto the sensor reading topic
    When Sensor reading published onto sensor reading topic
    Then Querying Prometheus should return the corresponding sample