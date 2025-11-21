package com.iot.sensor.integration;

import io.cucumber.java.en.Given;

public class CommonStepDefinitions {
    @Given("wait {int} ms")
    public void wait(int milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
    }
}