package cc.blynk.server.application.handlers.main.logic.reporting;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType;
import cc.blynk.server.core.model.widgets.ui.reporting.Report;
import cc.blynk.server.core.model.widgets.ui.reporting.source.ReportDataStream;
import cc.blynk.server.core.model.widgets.ui.reporting.source.ReportSource;
import cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource;
import cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport;
import cc.blynk.server.core.model.widgets.ui.reporting.type.DayOfMonth;
import cc.blynk.server.core.model.widgets.ui.reporting.type.MonthlyReport;
import cc.blynk.server.core.model.widgets.ui.reporting.type.ReportDurationType;
import cc.blynk.server.core.model.widgets.ui.reporting.type.WeeklyReport;
import cc.blynk.server.core.protocol.exceptions.IllegalCommandException;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

import static cc.blynk.server.core.model.widgets.ui.reporting.ReportOutput.CSV_FILE_PER_DEVICE_PER_PIN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateReportTestTimingTest {

    @Test
    public void testDailyReportStartTimeInThePast()  {
        ReportDataStream reportDataStream = new ReportDataStream((byte) 1, PinType.VIRTUAL, "Temperature", true);

        ReportSource reportSource2 = new TileTemplateReportSource(
                new ReportDataStream[] {reportDataStream},
                1,
                new int[] {0, 1}
        );

        //this time is behind a bit, so expected trigger time in the next day.
        Instant nowInstant = Instant.now()
                .with(ChronoField.MILLI_OF_SECOND, 0);
        long now = nowInstant.toEpochMilli();

        Report report = new Report(1, "Daily Report",
                new ReportSource[] {reportSource2},
                new DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com",
                GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, ZoneId.of("Europe/Kiev"), 0, 0, null);

        long expectedDelayInSeconds = report.calculateDelayInSeconds();

        assertEquals(expectedDelayInSeconds, nowInstant.plus(1, ChronoUnit.DAYS).minusMillis(now).getEpochSecond(), 1000);
    }

    @Test
    public void testDailyReportStartTimeInTheFuture() {
        ReportDataStream reportDataStream = new ReportDataStream((byte) 1, PinType.VIRTUAL, "Temperature", true);

        ReportSource reportSource2 = new TileTemplateReportSource(
                new ReportDataStream[] {reportDataStream},
                1,
                new int[] {0, 1}
        );

        //this time is upfront a bit
        Instant nowInstant = Instant.now()
                .with(ChronoField.MILLI_OF_SECOND, 0);
        long now = nowInstant.plusSeconds(60).toEpochMilli();

        Report report = new Report(1, "Daily Report",
                new ReportSource[] {reportSource2},
                new DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com",
                GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, ZoneId.of("Europe/Kiev"), 0, 0, null);

        long expectedDelayInSeconds = report.calculateDelayInSeconds();

        assertEquals(expectedDelayInSeconds, 60, 2);
    }

    @Test
    public void testDailyReportStartTimeInTheFutureAnotherTimezone() {
        ReportDataStream reportDataStream = new ReportDataStream((byte) 1, PinType.VIRTUAL, "Temperature", true);

        ReportSource reportSource2 = new TileTemplateReportSource(
                new ReportDataStream[] {reportDataStream},
                1,
                new int[] {0, 1}
        );

        //this time is upfront a bit
        long now = ZonedDateTime.now(ZoneId.of("UTC")).plusSeconds(60).toEpochSecond() * 1000;

        Report report = new Report(1, "Daily Report",
                new ReportSource[] {reportSource2},
                new DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com",
                GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, ZoneId.of("UTC"), 0, 0, null);

        long expectedDelayInSeconds = report.calculateDelayInSeconds();

        assertEquals(expectedDelayInSeconds, 60, 2);
    }

    @Test
    public void testDailyReportStartTimeInTheFutureAnotherTimezone2() {
        ReportDataStream reportDataStream = new ReportDataStream((byte) 1, PinType.VIRTUAL, "Temperature", true);

        ReportSource reportSource2 = new TileTemplateReportSource(
                new ReportDataStream[] {reportDataStream},
                1,
                new int[] {0, 1}
        );

        //this time is upfront a bit
        long now = ZonedDateTime.now(ZoneId.of("UTC")).plusSeconds(60).toEpochSecond() * 1000;

        Report report = new Report(1, "Daily Report",
                new ReportSource[] {reportSource2},
                new DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com",
                GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, ZoneId.of("Europe/Kiev"), 0, 0, null);

        long expectedDelayInSeconds = report.calculateDelayInSeconds();

        assertEquals(expectedDelayInSeconds, 60, 2);
    }

    @Test
    public void testWeeklyReportStartTimeInThePast() {
        ReportDataStream reportDataStream = new ReportDataStream((byte) 1, PinType.VIRTUAL, "Temperature", true);

        ReportSource reportSource2 = new TileTemplateReportSource(
                new ReportDataStream[] {reportDataStream},
                1,
                new int[] {0, 1}
        );

        //this time is behind a bit, so expected trigger time in the next day.
        ZonedDateTime zonedNow = ZonedDateTime.now(ZoneId.of("UTC"));
        long now = zonedNow.toEpochSecond() * 1000;

        Report report = new Report(1, "Report",
                new ReportSource[] {reportSource2},
                new WeeklyReport(now, ReportDurationType.INFINITE, 0, 0, 1), "test@gmail.com",
                GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, ZoneId.of("Europe/Kiev"), 0, 0, null);

        long expectedDelayInSeconds = report.calculateDelayInSeconds();

        assertTrue(expectedDelayInSeconds > 0);
    }

    @Test
    public void testStartEndDateIsSameAsNow() {
        ReportDataStream reportDataStream = new ReportDataStream((byte) 1, PinType.VIRTUAL, "Temperature", true);

        ReportSource reportSource2 = new TileTemplateReportSource(
                new ReportDataStream[] {reportDataStream},
                1,
                new int[] {0, 1}
        );

        //this time is upfront a bit, so expected trigger time today.
        ZonedDateTime zonedNow = ZonedDateTime.now(ZoneId.of("UTC")).plusSeconds(1);
        long now = (zonedNow.toEpochSecond() + 1) * 1000;

        Report report = new Report(1, "Daily Report",
                new ReportSource[] {reportSource2},
                new DailyReport(now, ReportDurationType.CUSTOM, now, now), "test@gmail.com",
                GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, ZoneId.of("Europe/Kiev"), 0, 0, null);

        long expectedDelayInSeconds = report.calculateDelayInSeconds();

        assertEquals(1, expectedDelayInSeconds, 1);
    }

    @Test
    public void testStartDateInFuture() {
        ReportDataStream reportDataStream = new ReportDataStream((byte) 1, PinType.VIRTUAL, "Temperature", true);

        ReportSource reportSource2 = new TileTemplateReportSource(
                new ReportDataStream[] {reportDataStream},
                1,
                new int[] {0, 1}
        );

        //this time is behind a bit, so expected trigger time in the next day.
        Instant nowInstant = Instant.now()
                .with(ChronoField.MILLI_OF_SECOND, 0);
        long now = nowInstant.toEpochMilli();

        Report report = new Report(1, "Daily Report",
                new ReportSource[] {reportSource2},
                new DailyReport(now, ReportDurationType.CUSTOM, now + 86400_000, now + 86400_000), "test@gmail.com",
                GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, ZoneId.of("Europe/Kiev"), 0, 0, null);

        long expectedDelayInSeconds = report.calculateDelayInSeconds();

        assertEquals(expectedDelayInSeconds, nowInstant.plus(1, ChronoUnit.DAYS).minusMillis(now).getEpochSecond(), 1000);
    }

    @Test
    public void testStartDateInFuture2() {
        ReportDataStream reportDataStream = new ReportDataStream((byte) 1, PinType.VIRTUAL, "Temperature", true);

        ReportSource reportSource2 = new TileTemplateReportSource(
                new ReportDataStream[] {reportDataStream},
                1,
                new int[] {0, 1}
        );

        //this time is behind a bit, so expected trigger time in the next day.
        Instant nowInstant = Instant.now()
                .with(ChronoField.MILLI_OF_SECOND, 0);
        long now = nowInstant.toEpochMilli();

        Report report = new Report(1, "Daily Report",
                new ReportSource[] {reportSource2},
                new DailyReport(now, ReportDurationType.CUSTOM, now + 2 * 86400_000, now + 2 * 86400_000), "test@gmail.com",
                GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, ZoneId.of("Europe/Kiev"), 0, 0, null);

        long expectedDelayInSeconds = report.calculateDelayInSeconds();

        assertEquals(expectedDelayInSeconds, nowInstant.plus(2, ChronoUnit.DAYS).minusMillis(now).getEpochSecond(), 1000);
    }

    @Test(expected = IllegalCommandException.class)
    public void testEndDateInPastDailyReport() {
        ReportDataStream reportDataStream = new ReportDataStream((byte) 1, PinType.VIRTUAL, "Temperature", true);

        ReportSource reportSource2 = new TileTemplateReportSource(
                new ReportDataStream[] {reportDataStream},
                1,
                new int[] {0, 1}
        );

        //this time is behind a bit, so expected trigger time in the next day.
        Instant nowInstant = Instant.now()
                .with(ChronoField.MILLI_OF_SECOND, 0);
        long now = nowInstant.toEpochMilli();

        Report report = new Report(1, "Daily Report",
                new ReportSource[] {reportSource2},
                new DailyReport(now, ReportDurationType.CUSTOM, now, now - 86400_000), "test@gmail.com",
                GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, ZoneId.of("Europe/Kiev"), 0, 0, null);

        report.calculateDelayInSeconds();
    }

    @Test(expected = IllegalCommandException.class)
    public void testEndDateInPastMonthlyReport() {
        ReportDataStream reportDataStream = new ReportDataStream((byte) 1, PinType.VIRTUAL, "Temperature", true);

        ReportSource reportSource2 = new TileTemplateReportSource(
                new ReportDataStream[] {reportDataStream},
                1,
                new int[] {0, 1}
        );

        ZonedDateTime zonedNow = ZonedDateTime.now(ZoneId.of("UTC"));
        long now = zonedNow.toEpochSecond() * 1000;

        if (zonedNow.getDayOfMonth() == 1) {
            now += 86400_000;
        }

        Report report = new Report(1, "Daily Report",
                new ReportSource[] {reportSource2},
                new MonthlyReport(now, ReportDurationType.CUSTOM, now, now, DayOfMonth.FIRST), "test@gmail.com",
                GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, ZoneId.of("Europe/Kiev"), 0, 0, null);

        report.calculateDelayInSeconds();
    }


}
