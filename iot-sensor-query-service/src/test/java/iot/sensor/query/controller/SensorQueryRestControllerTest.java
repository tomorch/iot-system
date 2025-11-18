package iot.sensor.query.controller;

import iot.sensor.query.IoTSensorQueryApplication;
import iot.sensor.query.client.ISensorQueryClient;
import iot.sensor.query.model.SensorAggregatedReadingResult;
import iot.sensor.query.model.SensorInfo;
import iot.sensor.query.model.SensorQueryResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = IoTSensorQueryApplication.class
)
@AutoConfigureMockMvc
public class SensorQueryRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ISensorQueryClient sensorQueryClient;

    @Test
    public void testValidSensorQuery() throws Exception {
        SensorQueryResult result = new SensorQueryResult(
            new SensorInfo("hm1", "HEART_RATE_MONITOR", "testgroup"),
            new SensorAggregatedReadingResult(140.0, 145.5, 90.0, 160.0, 10)
        );

        Mockito.when(
            sensorQueryClient.executeSensorQuery(
                "hm1",
                "2025-11-18T00:00:00.000Z",
                "2025-11-18T12:00:00.000Z"
            )
        ).thenReturn(result);

        mvc.perform(get("/api/v1/sensor/hm1")
            .queryParam("start","2025-11-18T00:00:00.000Z")
            .queryParam("end", "2025-11-18T12:00:00.000Z"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.queryResult.sensorInfo.id", is("hm1")))
            .andExpect(jsonPath("$.queryResult.sensorInfo.sensorType", is("HEART_RATE_MONITOR")))
            .andExpect(jsonPath("$.queryResult.sensorInfo.groupId", is("testgroup")))
            .andExpect(jsonPath("$.queryResult.aggregated.mean", is(140.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.min", is(90.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.max", is(160.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.median", is(145.5)))
            .andExpect(jsonPath("$.queryResult.aggregated.count", is(10)));
        
        Mockito.verify(sensorQueryClient, Mockito.atLeastOnce()).executeSensorQuery(
            "hm1",
            "2025-11-18T00:00:00.000Z",
            "2025-11-18T12:00:00.000Z");
    }

    @Test
    public void testEmptySensorQuery() throws Exception {
        SensorQueryResult result = new SensorQueryResult(
            new SensorInfo("hm1", null, null),
            new SensorAggregatedReadingResult(0, 0, 0, 0, 0)
        );

        Mockito.when(
            sensorQueryClient.executeSensorQuery(
                    "hm1",
                    "2025-11-18T00:00:00.000Z",
                    "2025-11-18T12:00:00.000Z"
            )
        ).thenReturn(result);

        mvc.perform(get("/api/v1/sensor/hm1")
            .queryParam("start","2025-11-18T00:00:00.000Z")
            .queryParam("end", "2025-11-18T12:00:00.000Z"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.queryResult.sensorInfo.id", is("hm1")))
            .andExpect(jsonPath("$.queryResult.aggregated.mean", is(0.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.min", is(0.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.max", is(0.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.median", is(0.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.count", is(0)));

        Mockito.verify(sensorQueryClient, Mockito.atLeastOnce()).executeSensorQuery(
            "hm1",
            "2025-11-18T00:00:00.000Z",
            "2025-11-18T12:00:00.000Z");
    }

    @Test
    public void testInvalidStartParamSensorQuery() throws Exception {
        mvc.perform(get("/api/v1/sensor/hm1")
            .queryParam("start","18-11-2025T00:00:00.000Z")
            .queryParam("end", "2025-11-18T12:00:00.000Z"))
            .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(sensorQueryClient);
    }

    @Test
    public void testInvalidEndParamSensorQuery() throws Exception {
        mvc.perform(get("/api/v1/sensor/hm1")
            .queryParam("start","2025-11-18T00:00:00.000Z")
            .queryParam("end", "18-11-2025T12:00:00.000Z"))
            .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(sensorQueryClient);
    }
}
