package com.epam.rd.autotasks.timing;

import java.time.LocalTime;

public class Developer {
    final String name;
    final String city;
    final LocalTime workDayStartTime;

    public Developer(final String name, final String city, final LocalTime workDayStartTime) {
        this.name = name;
        this.city = city;
        this.workDayStartTime = workDayStartTime;
    }
}
