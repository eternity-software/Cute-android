package ru.etysoft.cute.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFormatter {

    public static String format(String stirng) {
        String result;
        Pattern p = Pattern.compile("[^&]");
        Matcher m = p.matcher(stirng);
        StringBuilder builder = new StringBuilder();
        while (m.find()) {
            builder.append(stirng.substring(m.start(), m.end()));
        }
        result = builder.toString();
        result = result.trim();
        return result;
    }
}
