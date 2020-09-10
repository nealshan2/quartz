package org.quartz.core.jmx.statistics;

public interface SampledStatistics {
    long getJobsScheduledMostRecentSample();

    long getJobsExecutingMostRecentSample();

    long getJobsCompletedMostRecentSample();

    void shutdown();
}
