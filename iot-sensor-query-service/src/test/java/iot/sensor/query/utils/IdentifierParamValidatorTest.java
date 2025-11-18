package iot.sensor.query.utils;

import org.junit.jupiter.api.Test;

public class IdentifierParamValidatorTest {
    @Test
    void testValidIdentifierParam() {
        assert(IdentifierParamValidator.validate("id1"));
    }

    @Test
    void testSpecialCharacterIdentifierParams() {
        String specialCharacters = "!@$%^&*()[]{},.<>?/ \\¬`:;#~\"'=-_";
        for(char c : specialCharacters.toCharArray()) {
            assert(!IdentifierParamValidator.validate("id1" + c));
        }
    }
}
