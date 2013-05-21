/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.dataserver.migrator.task;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MigrationReportTimer
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-7-28
 */
public class MigrationReportTimer extends Thread {

    private static final Logger logger   = LoggerFactory.getLogger(MigrationReportTimer.class);

    private BaseMigrationTask   migrationTask;

    private long                firtTime = 10000l;                                             // after 10s
                                                                                                // private long period =
                                                                                                // 3 * 60 * 1000;
                                                                                                // //Every 3minites
    private long                period   = 3 * 60 * 1000;                                      // Every 3minites

    private Timer               timer;

    public void setMigrationTask(BaseMigrationTask migrationTask) {
        this.migrationTask = migrationTask;
    }

    public BaseMigrationTask getMigrationTask() {
        return migrationTask;
    }

    public void setFirtTime(long firtTime) {
        this.firtTime = firtTime;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public void run() {
        TimerTask reportTimerTask = new MigrationReportTimerTask(migrationTask);

        timer = new Timer();
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Start MigrationReportTimerTask........ After %d s,  Every %d s", firtTime,
                                      period));
        }
        timer.schedule(reportTimerTask, firtTime, period);
    }

    public void cancel() {
        if (logger.isInfoEnabled()) {
            logger.info(String.format("MigrationReportTimer cancel. %s", this.toString()));
        }
        timer.cancel();
    }

    /**
     * MigrationReportTimerTask
     * 
     * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
     * @since 1.0 2011-7-28
     */
    public static class MigrationReportTimerTask extends TimerTask {

        private volatile BaseMigrationTask migrationTask;

        public MigrationReportTimerTask(BaseMigrationTask migrationTask) {
            this.migrationTask = migrationTask;
        }

        @Override
        public void run() {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Periodically MigrationReportTimerTask. As below:  "));
            }
            migrationTask.notifyMigrateOrDataCleanProgress();
        }
    }

    public static void main(String[] args) {
        MigrationReportTimer reportThread = new MigrationReportTimer();
        reportThread.start();
    }
}
