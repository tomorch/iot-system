package iot.sensor.query.utils;

import org.junit.jupiter.api.Test;

public class DateParamValidatorTest {
    @Test
    public void testValidParam() {
        assert(DateParamValidator.validate("2025-11-18T03:00:00.000Zs"));
    }

    @Test
    public void testInvalidDateFormatParam() {
        assert(!DateParamValidator.validate("18-11-2025T03:00:00.000Z"));
    }

    @Test
    public void testInvalidTimeFormatParam() {
        assert(!DateParamValidator.validate("2025-11-18T03.00.00:000Z"));
    }

    @Test
    public void testInvalidFormatParam() {
        assert(!DateParamValidator.validate("03:00:00.000ZT2025-11-18"));
    }

    @Test void testGarbageParam() {
        assert!(DateParamValidator.validate("garbage"));
    }
}
