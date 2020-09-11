/* 
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 * 
 */

package org.quartz.sample;

import org.quartz.calendar.DateBuilder.IntervalUnit;
import org.quartz.scheduler.StdSchedulerFactory;
import org.quartz.job.JobDetail;
import org.quartz.scheduler.Scheduler;
import org.quartz.scheduler.SchedulerFactory;
import org.quartz.scheduler.SchedulerMetaData;
import org.quartz.triggers.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.quartz.calendar.DateBuilder.futureDate;
import static org.quartz.job.JobBuilder.newJob;
import static org.quartz.triggers.TriggerBuilder.newTrigger;

/**
 * This example will spawn a large number of jobs to run
 *
 * @author James House, Bill Kratzer
 */
public class LoadExample {



    public void run() throws Exception {
        Logger log = LoggerFactory.getLogger(LoadExample.class);

        // First we must get a reference to a scheduler
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();

        log.info("------- Initialization Complete -----------");

        // schedule 500 jobs to run
        for (int count = 1; count <= 5; count++) {
            JobDetail job = newJob(SimpleJob.class)
                    .withIdentity("job" + count, "group_1")
                    .requestRecovery() // ask scheduler
                    // to re-execute
                    // this job if it
                    // was in
                    // progress when
                    // the scheduler
                    // went down...
                    .build();

            // tell the job to delay some small amount... to simulate work...
            long timeDelay = (long) (Math.random() * 2500);
            job.getJobDataMap().put(SimpleJob.DELAY_TIME, timeDelay);

            Trigger trigger = newTrigger()
                    .withIdentity("trigger_" + count, "group_1")
                    .startAt(futureDate((count * 100), IntervalUnit.MILLISECOND)) // space fire times a small bit
                    .build();

            sched.scheduleJob(job, trigger);
            log.info("...scheduled " + count + " jobs");
        }

        log.info("------- Starting Scheduler ----------------");

        // start the schedule
        sched.start();

        log.info("------- Started Scheduler -----------------");

        log.info("------- Waiting five minutes... -----------");

        // wait five minutes to give our jobs a chance to run
        try {
            Thread.sleep(5L * 1000L);
        } catch (Exception e) {
            //
        }

        // shut down the scheduler
        log.info("------- Shutting Down ---------------------");
        sched.shutdown(true);
        log.info("------- Shutdown Complete -----------------");

        SchedulerMetaData metaData = sched.getMetaData();
        log.info("Executed " + metaData.getNumberOfJobsExecuted() + " jobs.");
    }

    public static void main(String[] args) throws Exception {
        LoadExample example = new LoadExample();
        example.run();
    }

}
