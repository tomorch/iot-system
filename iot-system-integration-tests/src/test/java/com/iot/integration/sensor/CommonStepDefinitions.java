package com.iot.integration.sensor;

import io.cucumber.java.en.Given;

public class CommonStepDefinitions {
    @Given("Wait {int} ms")
    public void wait(int milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
    }
}
