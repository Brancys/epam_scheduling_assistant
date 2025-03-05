package com.epam.rd.autotasks.timing;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

public interface SchedulingAssistant {

    LocalDateTime schedule(long meetingDurationMinutes, MeetingTimingPreferences preferences);

    static SchedulingAssistant create(Collection<Developer> team, LocalDate today) {
        return new SchedulingAssistantImpl(team, today); // ✅ Returns the implementation
    }
}
