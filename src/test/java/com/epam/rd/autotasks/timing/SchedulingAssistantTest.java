package com.epam.rd.autotasks.timing;

import com.epam.rd.autotasks.timing.MeetingTimingPreferences.InPeriodPreference;
import com.epam.rd.autotasks.timing.MeetingTimingPreferences.PeriodPreference;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SchedulingAssistantTest {

    @ParameterizedTest
    @MethodSource("testCases")
    void test(String team, LocalDate today, LocalDateTime expected, long meetingMinutes, MeetingTimingPreferences prefs) {
        final SchedulingAssistant assistant = SchedulingAssistant.create(getTeam(team), today);
        assertEquals(expected, assistant.schedule(meetingMinutes, prefs));
    }

    private static Stream<Arguments> testCases() throws IOException {
        return Files.lines(Paths.get("src", "test", "resources", "testCases.txt"))
                .map(line -> line.split(";"))
                .map(tokens -> Arguments.of(
                        tokens[0],
                        LocalDate.parse(tokens[1]),
                        tokens[2].equals("null") ? null : LocalDateTime.parse(tokens[2]),
                        Long.valueOf(tokens[3]),
                        prefs(PeriodPreference.valueOf(tokens[4]), InPeriodPreference.valueOf(tokens[5])))
                );
    }

    private static MeetingTimingPreferences prefs(final PeriodPreference today, final InPeriodPreference earliest) {
        return new MeetingTimingPreferences(today, earliest);
    }

    private static Collection<Developer> getTeam(String team) {
        try {
            Path teamPath = Paths.get("src", "test", "resources", team);
            return Files.list(teamPath)
                    .filter(Files::isRegularFile)
                    .map(Utils::devFromFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Utils {
        static Developer devFromFile(final Path file) {
            try (Scanner scanner = new Scanner(Files.newInputStream(file))) {
                return new Developer(scanner.nextLine(), scanner.nextLine(), LocalTime.parse(scanner.nextLine(), DateTimeFormatter.ISO_LOCAL_TIME));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}