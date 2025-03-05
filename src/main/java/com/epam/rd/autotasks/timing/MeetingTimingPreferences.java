package com.epam.rd.autotasks.timing;

public class MeetingTimingPreferences {

    final PeriodPreference period;
    final InPeriodPreference inPeriod;

    public MeetingTimingPreferences(final PeriodPreference period, final InPeriodPreference inPeriod) {
        this.period = period;
        this.inPeriod = inPeriod;
    }

    public enum PeriodPreference{
        TODAY, TOMORROW, THIS_WEEK
    }

    public enum InPeriodPreference{
        EARLIEST, LATEST
    }

}
