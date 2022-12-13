package org.example.services;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.entities.Job;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SchedulingService {
    ScheduledExecutorService scheduledExecutorService;

    public SchedulingService(int size) {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(size);
    }

    public void scheduleJob(Job job, int delay, Runnable task) {
        ScheduledFuture<?> scheduledFuture;
        if (job.getPeriod().getPeriod() <= 0) {
            scheduledFuture = scheduledExecutorService.schedule(task, delay, TimeUnit.HOURS);
        } else {
            scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(task, delay, job.getPeriod().getPeriod(), TimeUnit.HOURS);
        }
        job.setScheduledFuture(scheduledFuture);
        log.info("Job with id={} scheduled on period={}, in thread={}", job.getId(), job.getPeriod(), Thread.currentThread().getName());
    }

    public void shutdown() {
        scheduledExecutorService.shutdown();
        log.info("Scheduling service is disabled in thread={}", Thread.currentThread().getName());
    }
}