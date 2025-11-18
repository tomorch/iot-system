package iot.sensor.query.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdentifierParamValidator {
    public static boolean validate(String input) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input);
        return !m.find();
    }
}
