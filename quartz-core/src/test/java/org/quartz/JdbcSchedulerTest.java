/*
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.quartz;

import org.quartz.job.jdbcjobstore.JdbcQuartzTestUtilities;
import org.quartz.job.jdbcjobstore.JobStoreTX;
import org.quartz.scheduler.DirectSchedulerFactory;
import org.quartz.scheduler.Scheduler;
import org.quartz.scheduler.SchedulerException;
import org.quartz.scheduler.SchedulerRepository;
import org.quartz.simpl.SimpleThreadPool;

import java.sql.SQLException;

public class JdbcSchedulerTest extends AbstractSchedulerTest {

    @Override
    protected Scheduler createScheduler(String name, int threadPoolSize) throws SchedulerException {
        try {
            JdbcQuartzTestUtilities.createDatabase(name + "Database");
        } catch (SQLException e) {
            throw new AssertionError(e);
        }
        JobStoreTX jobStore = new JobStoreTX();
        jobStore.setDataSource(name + "Database");
        jobStore.setTablePrefix("QRTZ_");
        jobStore.setInstanceId("AUTO");
        DirectSchedulerFactory.getInstance()
                .createScheduler(name + "Scheduler",
                        "AUTO",
                        new SimpleThreadPool(threadPoolSize, Thread.NORM_PRIORITY),
                        jobStore);
        return SchedulerRepository.getInstance().lookup(name + "Scheduler");
    }
}
