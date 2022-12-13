package org.example.services;

import lombok.extern.slf4j.Slf4j;
import org.example.entities.Job;
import org.example.enums.JobStateEnum;
import org.example.enums.PeriodicEnum;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class SchedulingServiceTest {

    @Test
    void scheduleJob() throws InterruptedException {
        SchedulingService schedulingService = new SchedulingService(1);
        CountDownLatch firstLatchMain = new CountDownLatch(1);
        CountDownLatch secondLatchMain = new CountDownLatch(1);
        CountDownLatch latchThread = new CountDownLatch(1);

        Job job = new Job("Type", PeriodicEnum.TWO_HOURS);
        schedulingService.scheduleJob(job, 0, () -> {
            job.setState(JobStateEnum.RUNNING);
            firstLatchMain.countDown();
            try {
                latchThread.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            job.setState(JobStateEnum.COMPLETED);
            secondLatchMain.countDown();
        });

        firstLatchMain.await();
        assertEquals(JobStateEnum.RUNNING, job.getState());
        latchThread.countDown();
        secondLatchMain.await();
        assertEquals(JobStateEnum.COMPLETED, job.getState());
        schedulingService.shutdown();
    }

    @Test
    void scheduleJobs() throws InterruptedException {
        SchedulingService schedulingService = new SchedulingService(2);
        CountDownLatch firstLatchMain = new CountDownLatch(2);
        CountDownLatch secondLatchMain = new CountDownLatch(1);
        CountDownLatch latchThread = new CountDownLatch(1);
        Job researchJob = new Job("Research", PeriodicEnum.ONE_HOUR);
        Job implementJob = new Job("Implement", PeriodicEnum.TWO_HOURS);
        schedulingService.scheduleJob(researchJob, 0, () -> {
            firstLatchMain.countDown();
            researchJob.setState(JobStateEnum.RUNNING);
        });
        schedulingService.scheduleJob(implementJob, 0, () -> {
            firstLatchMain.countDown();
            implementJob.setState(JobStateEnum.RUNNING);
            try {
                latchThread.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            secondLatchMain.countDown();
            implementJob.stop();
        });

        firstLatchMain.await();
        assertEquals(JobStateEnum.RUNNING, researchJob.getState());
        assertEquals(JobStateEnum.RUNNING, implementJob.getState());
        researchJob.stop();
        assertEquals(JobStateEnum.STOPPED, researchJob.getState());
        assertEquals(JobStateEnum.RUNNING, implementJob.getState());
        latchThread.countDown();
        secondLatchMain.await();
        secondLatchMain.await(1, TimeUnit.SECONDS);
        assertEquals(JobStateEnum.STOPPED, implementJob.getState());
    }
}