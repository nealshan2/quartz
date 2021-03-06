
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

package org.quartz.scheduler;

import org.quartz.job.JobRunShellFactory;
import org.quartz.spi.JobStore;
import org.quartz.spi.SchedulerPlugin;
import org.quartz.spi.ThreadExecutor;
import org.quartz.spi.ThreadPool;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Contains all of the resources (<code>JobStore</code>,<code>ThreadPool</code>,
 * etc.) necessary to create a <code>{@link QuartzScheduler}</code> instance.
 * </p>
 *
 * @author James House
 * @see QuartzScheduler
 */
public class QuartzSchedulerResources {

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * Data members.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private String name;

    private String instanceId;

    private String threadName;

    private ThreadPool threadPool;

    private JobStore jobStore;

    private JobRunShellFactory jobRunShellFactory;

    private List<SchedulerPlugin> schedulerPlugins = new ArrayList<SchedulerPlugin>(10);

    private boolean makeSchedulerThreadDaemon = false;

    private boolean threadsInheritInitializersClassLoadContext = false;

    private boolean jmxExport;

    private String jmxObjectName;

    private ThreadExecutor threadExecutor;

    private long batchTimeWindow = 0;

    private int maxBatchSize = 1;

    private boolean interruptJobsOnShutdown = false;
    private boolean interruptJobsOnShutdownWithWait = false;
    
    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * Constructors.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * <p>
     * Create an instance with no properties initialized.
     * </p>
     */
    public QuartzSchedulerResources() {
        // do nothing...
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * Interface.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * <p>
     * Get the name for the <code>{@link QuartzScheduler}</code>.
     * </p>
     */
    public String getName() {
        return name;
    }

    /**
     * <p>
     * Set the name for the <code>{@link QuartzScheduler}</code>.
     * </p>
     *
     * @throws IllegalArgumentException if name is null or empty.
     */
    public void setName(String name) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException(
                    "Scheduler name cannot be empty.");
        }

        this.name = name;

        if (threadName == null) {
            // thread name not already set, use default thread name
            setThreadName(name + "_QuartzSchedulerThread");
        }
    }

    /**
     * <p>
     * Get the instance Id for the <code>{@link QuartzScheduler}</code>.
     * </p>
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * <p>
     * Set the name for the <code>{@link QuartzScheduler}</code>.
     * </p>
     *
     * @throws IllegalArgumentException if name is null or empty.
     */
    public void setInstanceId(String instanceId) {
        if (instanceId == null || instanceId.trim().length() == 0) {
            throw new IllegalArgumentException(
                    "Scheduler instanceId cannot be empty.");
        }

        this.instanceId = instanceId;
    }

    public static String getUniqueIdentifier(String schedName,
                                             String schedInstId) {
        return schedName + "_$_" + schedInstId;
    }

    public String getUniqueIdentifier() {
        return getUniqueIdentifier(name, instanceId);
    }


    /**
     * <p>
     * Get the name for the <code>{@link QuartzSchedulerThread}</code>.
     * </p>
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * <p>
     * Set the name for the <code>{@link QuartzSchedulerThread}</code>.
     * </p>
     *
     * @throws IllegalArgumentException if name is null or empty.
     */
    public void setThreadName(String threadName) {
        if (threadName == null || threadName.trim().length() == 0) {
            throw new IllegalArgumentException(
                    "Scheduler thread name cannot be empty.");
        }

        this.threadName = threadName;
    }

    /**
     * <p>
     * Get the <code>{@link ThreadPool}</code> for the <code>{@link QuartzScheduler}</code>
     * to use.
     * </p>
     */
    public ThreadPool getThreadPool() {
        return threadPool;
    }

    /**
     * <p>
     * Set the <code>{@link ThreadPool}</code> for the <code>{@link QuartzScheduler}</code>
     * to use.
     * </p>
     *
     * @throws IllegalArgumentException if threadPool is null.
     */
    public void setThreadPool(ThreadPool threadPool) {
        if (threadPool == null) {
            throw new IllegalArgumentException("ThreadPool cannot be null.");
        }

        this.threadPool = threadPool;
    }

    /**
     * <p>
     * Get the <code>{@link JobStore}</code> for the <code>{@link QuartzScheduler}</code>
     * to use.
     * </p>
     */
    public JobStore getJobStore() {
        return jobStore;
    }

    /**
     * <p>
     * Set the <code>{@link JobStore}</code> for the <code>{@link QuartzScheduler}</code>
     * to use.
     * </p>
     *
     * @throws IllegalArgumentException if jobStore is null.
     */
    public void setJobStore(JobStore jobStore) {
        if (jobStore == null) {
            throw new IllegalArgumentException("JobStore cannot be null.");
        }

        this.jobStore = jobStore;
    }

    /**
     * <p>
     * Get the <code>{@link JobRunShellFactory}</code> for the <code>{@link QuartzScheduler}</code>
     * to use.
     * </p>
     */
    public JobRunShellFactory getJobRunShellFactory() {
        return jobRunShellFactory;
    }

    /**
     * <p>
     * Set the <code>{@link JobRunShellFactory}</code> for the <code>{@link QuartzScheduler}</code>
     * to use.
     * </p>
     *
     * @throws IllegalArgumentException if jobRunShellFactory is null.
     */
    public void setJobRunShellFactory(JobRunShellFactory jobRunShellFactory) {
        if (jobRunShellFactory == null) {
            throw new IllegalArgumentException(
                    "JobRunShellFactory cannot be null.");
        }

        this.jobRunShellFactory = jobRunShellFactory;
    }

    /**
     * <p>
     * Add the given <code>{@link org.quartz.spi.SchedulerPlugin}</code> for the
     * <code>{@link QuartzScheduler}</code> to use. This method expects the plugin's
     * "initialize" method to be invoked externally (either before or after
     * this method is called).
     * </p>
     */
    public void addSchedulerPlugin(SchedulerPlugin plugin) {
        schedulerPlugins.add(plugin);
    }

    /**
     * <p>
     * Get the <code>List</code> of all
     * <code>{@link org.quartz.spi.SchedulerPlugin}</code>s for the
     * <code>{@link QuartzScheduler}</code> to use.
     * </p>
     */
    public List<SchedulerPlugin> getSchedulerPlugins() {
        return schedulerPlugins;
    }

    /**
     * Get whether to mark the Quartz scheduling thread as daemon.
     *
     * @see Thread#setDaemon(boolean)
     */
    public boolean getMakeSchedulerThreadDaemon() {
        return makeSchedulerThreadDaemon;
    }

    /**
     * Set whether to mark the Quartz scheduling thread as daemon.
     *
     * @see Thread#setDaemon(boolean)
     */
    public void setMakeSchedulerThreadDaemon(boolean makeSchedulerThreadDaemon) {
        this.makeSchedulerThreadDaemon = makeSchedulerThreadDaemon;
    }

    /**
     * Get whether to set the class load context of spawned threads to that
     * of the initializing thread.
     */
    public boolean isThreadsInheritInitializersClassLoadContext() {
        return threadsInheritInitializersClassLoadContext;
    }

    /**
     * Set whether to set the class load context of spawned threads to that
     * of the initializing thread.
     */
    public void setThreadsInheritInitializersClassLoadContext(
            boolean threadsInheritInitializersClassLoadContext) {
        this.threadsInheritInitializersClassLoadContext = threadsInheritInitializersClassLoadContext;
    }

    /**
     * Get whether the QuartzScheduler should be registered with the local
     * MBeanServer.
     */
    public boolean getJMXExport() {
        return jmxExport;
    }

    /**
     * Set whether the QuartzScheduler should be registered with the local
     * MBeanServer.
     */
    public void setJMXExport(boolean jmxExport) {
        this.jmxExport = jmxExport;
    }

    /**
     * Get the name under which the QuartzScheduler should be registered with
     * the local MBeanServer.  If unset, defaults to the value calculated by
     * <code>generateJMXObjectName<code>.
     *
     * @see #generateJMXObjectName(String, String)
     */
    public String getJMXObjectName() {
        return (jmxObjectName == null) ? generateJMXObjectName(name, instanceId) : jmxObjectName;
    }

    /**
     * Set the name under which the QuartzScheduler should be registered with
     * the local MBeanServer.  If unset, defaults to the value calculated by
     * <code>generateJMXObjectName<code>.
     *
     * @see #generateJMXObjectName(String, String)
     */
    public void setJMXObjectName(String jmxObjectName) {
        this.jmxObjectName = jmxObjectName;
    }

    /**
     * Get the ThreadExecutor which runs the QuartzSchedulerThread
     */
    public ThreadExecutor getThreadExecutor() {
        return threadExecutor;
    }

    /**
     * Set the ThreadExecutor which runs the QuartzSchedulerThread
     */
    public void setThreadExecutor(ThreadExecutor threadExecutor) {
        this.threadExecutor = threadExecutor;
    }

    /**
     * Create the name under which this scheduler should be registered in JMX.
     * <p>
     * The name is composed as:
     * quartz:type=QuartzScheduler,name=<i>[schedName]</i>,instance=<i>[schedInstId]</i>
     * </p>
     */
    public static String generateJMXObjectName(String schedName, String schedInstId) {
        return "quartz:type=QuartzScheduler" + ",name="
                + schedName.replaceAll(":|=|\n", ".")
                + ",instance=" + schedInstId;
    }

    public long getBatchTimeWindow() {
        return batchTimeWindow;
    }

    public void setBatchTimeWindow(long batchTimeWindow) {
        this.batchTimeWindow = batchTimeWindow;
    }

    public int getMaxBatchSize() {
        return maxBatchSize;
    }

    public void setMaxBatchSize(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
    }

    public boolean isInterruptJobsOnShutdown() {
        return interruptJobsOnShutdown;
    }

    public void setInterruptJobsOnShutdown(boolean interruptJobsOnShutdown) {
        this.interruptJobsOnShutdown = interruptJobsOnShutdown;
    }

    public boolean isInterruptJobsOnShutdownWithWait() {
        return interruptJobsOnShutdownWithWait;
    }

    public void setInterruptJobsOnShutdownWithWait(
            boolean interruptJobsOnShutdownWithWait) {
        this.interruptJobsOnShutdownWithWait = interruptJobsOnShutdownWithWait;
    }

}
