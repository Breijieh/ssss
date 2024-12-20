package com.example.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    // Define accepted date formats
    private static final String[] DATE_PATTERNS = {
            "dd-MM-yyyy",
            "dd MM yyyy",
            "dd/MM/yyyy",
            "dd.MM.yyyy",
            "yyyy-MM-dd" // ISO format
            // Add more patterns if needed
    };

    public static Date parseDateFlexible(String dateStr) throws ParseException {
        for (String pattern : DATE_PATTERNS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                sdf.setLenient(false); // Strict parsing
                Date date = sdf.parse(dateStr);

                // Zero out the time components
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                return cal.getTime();
            } catch (ParseException e) {
                // Continue to the next pattern
            }
        }
        // If none of the patterns match, throw ParseException
        throw new ParseException("Invalid date format: " + dateStr, 0);
    }

}
