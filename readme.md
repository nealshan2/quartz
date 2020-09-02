Quartz Scheduler

Quartz is a richly featured, open source job scheduling library that can be 
integrated within virtually any Java application - from the smallest stand-alone 
application to the largest e-commerce system.

Where should I start if I am new to Quartz?
==============================================================================

There is an FAQ, tutorial and configuration reference that can be found on the 
main Quartz website at http://quartz-scheduler.org/docs/index.html

Most of the Java source files are fairly well documented with JavaDOC -
consider this your "reference manual".  

Start by looking at org.quartz.Scheduler, org.quartz.Job,
org.quartz.JobDetail and org.quartz.Trigger.

Examine and run the examples found in the "examples" directory.

If you're interested in the "behind the scenes" (server-side) code,
you'll want to look at org.quartz.core.QuartzSchedulerThread, which
will make you interested in org.quartz.spi.JobStore.java,
org.quartz.spi.ThreadPool.java and org.quartz.core.JobRunShell.
