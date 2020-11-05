package eu.opertusmundi.admin.web.model.dto;

import java.time.ZonedDateTime;

public class DashboardDto {

    private final ZonedDateTime updatedOn = ZonedDateTime.now();

    private final StatisticsCollection statistics = new DashboardDto.StatisticsCollection();

    public ZonedDateTime getUpdatedOn() {
        return this.updatedOn;
    }

    public StatisticsCollection getStatistics() {
        return this.statistics;
    }

    public static class StatisticsCollection {

        public EventStatistics events;

    }

    public static abstract class Statistics {

        public ZonedDateTime updatedOn = ZonedDateTime.now();

    }

    public static class EventStatistics extends Statistics {

        public EventStatistics(long error, long warning, long information) {
            super();
            this.error       = error;
            this.warning     = warning;
            this.information = information;
        }

        public long error;

        public long warning;

        public long information;
    }

}
