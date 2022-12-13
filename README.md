# A basic job system that can schedule and execute jobs of different types.

The users of this system will be another developers and their main objective is to create
different job definitions which they can run asynchronously.

## A basic logic
Job is the main class which contains next properties:
* id
* jobType
* state
* scheduledFuture
* locker
* period

First of all user create a new Job by setting type of job and period of execution(every 1, 2, 6, or 12 hours).
Next step is user call the scheduled executor service which provide execution of job.  
In order to schedule and execute the job user have to call _ScheduledExecutorServices_ method _scheduleJob_ by passing current job and instruction for execution(Runnable object).
This method execute new thread and put it to _ThreadPoolExecutor_. 


### Running Unit Tests

Run JUnit unit tests as follows:

```shell
mvn clean test
```