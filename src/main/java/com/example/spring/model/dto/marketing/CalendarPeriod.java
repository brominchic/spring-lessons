package com.example.spring.model.dto.marketing;

public final class CalendarPeriod {
    public static final String MONTH = "month";
    public static final String QUARTER = "quarter";

    private CalendarPeriod() {
    }

    public static String normalize(String value) {
        if (value == null || value.isBlank() || MONTH.equalsIgnoreCase(value)) {
            return MONTH;
        }
        if (QUARTER.equalsIgnoreCase(value)) {
            return QUARTER;
        }
        throw new IllegalArgumentException("Unsupported calendar period: " + value);
    }

    public static boolean isQuarter(String value) {
        return QUARTER.equals(normalize(value));
    }
}
