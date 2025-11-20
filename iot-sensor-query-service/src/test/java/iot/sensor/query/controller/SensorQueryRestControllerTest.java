package iot.sensor.query.controller;

import iot.sensor.query.IoTSensorQueryApplication;
import iot.sensor.query.client.ISensorQueryClient;
import iot.sensor.query.response.SensorAggregatedReadingResult;
import iot.sensor.query.response.SensorGroupQueryResult;
import iot.sensor.query.response.SensorInfo;
import iot.sensor.query.response.SensorQueryResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = IoTSensorQueryApplication.class
)
@AutoConfigureMockMvc(addFilters = false)
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

        when(
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
            .andExpect(jsonPath("$.status.success", is(true)))
            .andExpect(jsonPath("$.queryResult.sensorInfo.id", is("hm1")))
            .andExpect(jsonPath("$.queryResult.sensorInfo.sensorType", is("HEART_RATE_MONITOR")))
            .andExpect(jsonPath("$.queryResult.sensorInfo.groupId", is("testgroup")))
            .andExpect(jsonPath("$.queryResult.aggregated.mean", is(140.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.min", is(90.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.max", is(160.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.median", is(145.5)))
            .andExpect(jsonPath("$.queryResult.aggregated.count", is(10)));
        
        verify(sensorQueryClient, atLeastOnce()).executeSensorQuery(
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

        when(
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
            .andExpect(jsonPath("$.status.success", is(true)))
            .andExpect(jsonPath("$.queryResult.sensorInfo.id", is("hm1")))
            .andExpect(jsonPath("$.queryResult.aggregated.mean", is(0.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.min", is(0.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.max", is(0.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.median", is(0.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.count", is(0)));

        verify(sensorQueryClient, atLeastOnce()).executeSensorQuery(
            "hm1",
            "2025-11-18T00:00:00.000Z",
            "2025-11-18T12:00:00.000Z");
    }

    @Test
    public void testInvalidSensorIdParamSensorQuery() throws Exception {
        mvc.perform(get("/api/v1/sensor/hm*1")
            .queryParam("start","2025-11-18T00:00:00.000Z")
            .queryParam("end", "2025-11-18T12:00:00.000Z"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status.success", is(false)))
            .andExpect(jsonPath("$.status.reason", is("sensorId is invalid - must contain no special characters")));

        verifyNoInteractions(sensorQueryClient);
    }

    @Test
    public void testInvalidStartParamSensorQuery() throws Exception {
        mvc.perform(get("/api/v1/sensor/hm1")
            .queryParam("start","18-11-2025T00:00:00.000Z")
            .queryParam("end", "2025-11-18T12:00:00.000Z"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status.success", is(false)))
            .andExpect(jsonPath("$.status.reason", is("start date is invalid - must conform to ISO 8601")));

        verifyNoInteractions(sensorQueryClient);
    }

    @Test
    public void testInvalidEndParamSensorQuery() throws Exception {
        mvc.perform(get("/api/v1/sensor/hm1")
            .queryParam("start","2025-11-18T00:00:00.000Z")
            .queryParam("end", "18-11-2025T12:00:00.000Z"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status.success", is(false)))
            .andExpect(jsonPath("$.status.reason", is("end date is invalid - must conform to ISO 8601")));

        verifyNoInteractions(sensorQueryClient);
    }

    @Test
    public void testValidSensorGroupQuery() throws Exception {
        SensorGroupQueryResult result = new SensorGroupQueryResult(
            "testgroup",
            new SensorAggregatedReadingResult(140.0, 145.5, 90.0, 160.0, 10)
        );

        when(
            sensorQueryClient.executeSensorGroupQuery(
                "testgroup",
                "2025-11-18T00:00:00.000Z",
                "2025-11-18T12:00:00.000Z"
            )
        ).thenReturn(result);

        mvc.perform(get("/api/v1/sensor/group/testgroup")
            .queryParam("start","2025-11-18T00:00:00.000Z")
            .queryParam("end", "2025-11-18T12:00:00.000Z"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status.success", is(true)))
            .andExpect(jsonPath("$.queryResult.groupId", is("testgroup")))
            .andExpect(jsonPath("$.queryResult.aggregated.mean", is(140.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.min", is(90.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.max", is(160.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.median", is(145.5)))
            .andExpect(jsonPath("$.queryResult.aggregated.count", is(10)));

        verify(sensorQueryClient, atLeastOnce()).executeSensorGroupQuery(
                "testgroup",
                "2025-11-18T00:00:00.000Z",
                "2025-11-18T12:00:00.000Z");
    }

    @Test
    public void testEmptySensorGroupQuery() throws Exception {
        SensorGroupQueryResult result = new SensorGroupQueryResult(
            "testgroup",
            new SensorAggregatedReadingResult(0, 0, 0, 0, 0)
        );

        when(
            sensorQueryClient.executeSensorGroupQuery(
                "testgroup",
                "2025-11-18T00:00:00.000Z",
                "2025-11-18T12:00:00.000Z"
            )
        ).thenReturn(result);

        mvc.perform(get("/api/v1/sensor/group/testgroup")
            .queryParam("start","2025-11-18T00:00:00.000Z")
            .queryParam("end", "2025-11-18T12:00:00.000Z"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status.success", is(true)))
            .andExpect(jsonPath("$.queryResult.groupId", is("testgroup")))
            .andExpect(jsonPath("$.queryResult.aggregated.mean", is(0.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.min", is(0.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.max", is(0.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.median", is(0.0)))
            .andExpect(jsonPath("$.queryResult.aggregated.count", is(0)));

        verify(sensorQueryClient, atLeastOnce()).executeSensorGroupQuery(
            "testgroup",
            "2025-11-18T00:00:00.000Z",
            "2025-11-18T12:00:00.000Z");
    }

    @Test
    public void testInvalidGroupIdParamSensorGroupQuery() throws Exception {
        mvc.perform(get("/api/v1/sensor/group/test*group")
            .queryParam("start","2025-11-18T00:00:00.000Z")
            .queryParam("end", "2025-11-18T12:00:00.000Z"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status.success", is(false)))
            .andExpect(jsonPath("$.status.reason", is("groupId is invalid - must contain no special characters")));

        verifyNoInteractions(sensorQueryClient);
    }

    @Test
    public void testInvalidStartParamSensorGroupQuery() throws Exception {
        mvc.perform(get("/api/v1/sensor/group/testgroup")
            .queryParam("start","18-11-2025T00:00:00.000Z")
            .queryParam("end", "2025-11-18T12:00:00.000Z"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status.success", is(false)))
            .andExpect(jsonPath("$.status.reason", is("start date is invalid - must conform to ISO 8601")));

        verifyNoInteractions(sensorQueryClient);
    }

    @Test
    public void testInvalidEndParamSensorGroupQuery() throws Exception {
        mvc.perform(get("/api/v1/sensor/group/testgroup")
                        .queryParam("start","2025-11-18T00:00:00.000Z")
                        .queryParam("end", "18-11-2025T12:00:00.000Z"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status.success", is(false)))
                .andExpect(jsonPath("$.status.reason", is("end date is invalid - must conform to ISO 8601")));

        verifyNoInteractions(sensorQueryClient);
    }
}
