package org.example.entities;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.JobStateEnum;
import org.example.enums.PeriodicEnum;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;

@Data
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Job {
    final UUID id;
    final String jobType;
    JobStateEnum state;
    ScheduledFuture<?> scheduledFuture;
    final ReentrantLock locker;
    final PeriodicEnum period;

    public Job(String jobType, PeriodicEnum period) {
        this.id = UUID.randomUUID();
        this.jobType = jobType;
        this.period = period;
        this.locker = new ReentrantLock();
        setState(JobStateEnum.CREATED);
    }

    public void setState(JobStateEnum state) {
        locker.lock();
        try {
            this.state = state;
            log.info("Job with id={} is {} in thread={}", this.id, this.state, Thread.currentThread().getName());
        } finally {
            locker.unlock();
        }
    }

    public void stop() {
        locker.lock();
        try {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
                if (scheduledFuture.isDone()) {
                    setState(JobStateEnum.STOPPED);
                } else {
                    log.warn("Job with id={} could not be stopped, state={}", this.id, this.state);
                }
            }
        } finally {
            locker.unlock();
        }
    }
}