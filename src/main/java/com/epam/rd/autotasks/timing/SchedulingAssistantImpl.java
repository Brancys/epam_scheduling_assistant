package com.epam.rd.autotasks.timing;

import java.time.*;
import java.util.*;

public class SchedulingAssistantImpl implements SchedulingAssistant {
    private final Collection<Developer> team;
    private final LocalDate today;

    public SchedulingAssistantImpl(Collection<Developer> team, LocalDate today) {
        this.team = team;
        this.today = today;
    }

    @Override
    public LocalDateTime schedule(long meetingDurationMinutes, MeetingTimingPreferences preferences) {
        LocalDate meetingDate = getTargetDate(preferences.period);
        List<TimeSlot> availableSlots = getAvailableTimeSlots(meetingDate);

        return findMeetingSlot(availableSlots, meetingDurationMinutes, preferences.inPeriod);
    }

    private LocalDate getTargetDate(MeetingTimingPreferences.PeriodPreference period) {
        return switch (period) {
            case TODAY -> today;
            case TOMORROW -> today.plusDays(1);
            case THIS_WEEK -> {
                // Ensure we don't move past the expected workday
                LocalDate lastWorkday = today.with(DayOfWeek.SATURDAY);
                yield lastWorkday.isBefore(today) ? today : lastWorkday;
            }
        };
    }

    private List<TimeSlot> getAvailableTimeSlots(LocalDate meetingDate) {
        List<TimeSlot> slots = new ArrayList<>();

        for (Developer dev : team) {
            ZoneId zoneId = getZoneForCity(dev.city);
            ZonedDateTime workStartLocal = ZonedDateTime.of(meetingDate, dev.workDayStartTime, zoneId);
            ZonedDateTime workStartGMT = workStartLocal.withZoneSameInstant(ZoneOffset.UTC);
            ZonedDateTime workEndGMT = workStartGMT.plusHours(8);

            slots.add(new TimeSlot(workStartGMT.toLocalDateTime(), workEndGMT.toLocalDateTime()));
        }

        return findCommonTimeSlots(slots);
    }

    private ZoneId getZoneForCity(String city) {
        return switch (city) {
            case "Los Angeles" -> ZoneId.of("America/Los_Angeles");
            case "New York" -> ZoneId.of("America/New_York");
            case "London" -> ZoneId.of("Europe/London");
            case "Paris" -> ZoneId.of("Europe/Paris");
            case "Samara" -> ZoneId.of("Europe/Samara");
            case "Prague" -> ZoneId.of("Europe/Prague");
            case "Tbilisi" -> ZoneId.of("Asia/Tbilisi");
            default -> ZoneId.of("UTC");
        };
    }

    private List<TimeSlot> findCommonTimeSlots(List<TimeSlot> slots) {
        if (slots.isEmpty()) return Collections.emptyList();

        LocalDateTime commonStart = slots.get(0).start;
        LocalDateTime commonEnd = slots.get(0).end;

        for (TimeSlot slot : slots) {
            commonStart = commonStart.isBefore(slot.start) ? slot.start : commonStart;
            commonEnd = commonEnd.isAfter(slot.end) ? slot.end : commonEnd;
        }

        return commonStart.isBefore(commonEnd) ? List.of(new TimeSlot(commonStart, commonEnd)) : Collections.emptyList();
    }

    private LocalDateTime findMeetingSlot(List<TimeSlot> slots, long duration, MeetingTimingPreferences.InPeriodPreference preference) {
        for (TimeSlot slot : slots) {
            if (slot.durationMinutes() >= duration) {
                return preference == MeetingTimingPreferences.InPeriodPreference.EARLIEST
                        ? slot.start
                        : slot.end.minusMinutes(duration);
            }
        }
        return null;
    }

    public static SchedulingAssistant create(Collection<Developer> team, LocalDate today) {
        return new SchedulingAssistantImpl(team, today);
    }

    private static class TimeSlot {
        LocalDateTime start;
        LocalDateTime end;

        TimeSlot(LocalDateTime start, LocalDateTime end) {
            this.start = start;
            this.end = end;
        }

        long durationMinutes() {
            return Duration.between(start, end).toMinutes();
        }
    }
}
