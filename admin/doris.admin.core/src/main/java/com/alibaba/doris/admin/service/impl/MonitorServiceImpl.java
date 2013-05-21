package com.alibaba.doris.admin.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.alibaba.doris.admin.dao.NamespaceDao;
import com.alibaba.doris.admin.dao.PrefLogDao;
import com.alibaba.doris.admin.dataobject.NamespaceDO;
import com.alibaba.doris.admin.dataobject.PrefLogDO;
import com.alibaba.doris.admin.service.MonitorService;
import com.alibaba.doris.admin.support.PrefQuery;
import com.alibaba.doris.admin.support.PrefStatObject;

public class MonitorServiceImpl implements MonitorService {

    private PrefLogDao   prefLogDao;
    private NamespaceDao namespaceDao;

    public void setPrefLogDao(PrefLogDao prefLogDao) {
        this.prefLogDao = prefLogDao;
    }

    public void setNamespaceDao(NamespaceDao namespaceDao) {
        this.namespaceDao = namespaceDao;
    }

    public void info(String subject, String detail) {
        // TODO Auto-generated method stub

    }

    public void error(String subject, String detail) {
        // TODO Auto-generated method stub

    }

    public void savePrefReports(List<PrefLogDO> reports) {
        if (reports == null || reports.size() == 0) {
            return;
        }

        prefLogDao.batchInsert(reports);
    }

    public List<PrefStatObject> statByQuery(PrefQuery query) {
        if (query == null || query.getGmtStart() == null) {
            throw new IllegalArgumentException("invalid param");
        }

        return convert(prefLogDao.statByQuery(query));
    }

    public List<PrefStatObject> statWithNameSpace(PrefQuery query) {
        if (query == null || query.getGmtStart() == null) {
            throw new IllegalArgumentException("invalid param");
        }

        return convert(prefLogDao.statWithNameSpace(query));
    }

    public List<PrefStatObject> statWithPhysicalId(PrefQuery query) {
        if (query == null || query.getGmtStart() == null) {
            throw new IllegalArgumentException("invalid param");
        }

        return convert(prefLogDao.statWithPhysicalId(query));
    }

    private List<PrefStatObject> convert(List<PrefLogDO> tmplist) {
        if (tmplist == null || tmplist.size() == 0) {
            return Collections.emptyList();
        }

        List<PrefStatObject> result = new ArrayList<PrefStatObject>();
        long current = System.currentTimeMillis();

        for (PrefLogDO prefLog : tmplist) {
            if (prefLog == null) {
                continue;
            }
            PrefStatObject prefStatObject = new PrefStatObject();

            BeanUtils.copyProperties(prefLog, prefStatObject);

            //get namespace Name by namespace id

            try {
                int namespaceId = Integer.parseInt(prefStatObject.getNameSpace());

                NamespaceDO ns = namespaceDao.queryNamespaceById(namespaceId);
                if (ns != null) {
                    prefStatObject.setNameSpace(ns.getName());
                }
            } catch (Exception e) {
                //ignore it
            }

            long op = prefLog.getTotalOperations();
            long bytes = prefLog.getTotalBytes();
            long latency = prefLog.getTotalLatency();
            long timeUsed = (current - prefLog.getTimeStart().getTime()) / 1000;

            prefStatObject.setOps((double) op / timeUsed);
            prefStatObject.setBps((double) bytes / timeUsed);
            prefStatObject.setAvgLatency((double) latency / op);

            result.add(prefStatObject);
        }
        return result;
    }

    public void archiveWithTx(int hourBefore) {
        int i = 10;//循环次数控制

        Calendar before = Calendar.getInstance();
        before.add(Calendar.HOUR_OF_DAY, 0 - hourBefore);
        before.set(Calendar.MINUTE, 0);
        before.set(Calendar.SECOND, 0);

        while (--i > 0) {
            Date minStartDate = prefLogDao.getMinTimeStart(before.getTime());

            if (minStartDate == null) {
                return;
            }

            Date timeStart = minStartDate;

            Calendar c = Calendar.getInstance();
            c.setLenient(false);
            c.setTime(timeStart);
            c.add(Calendar.DATE, 1);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);

            Date timeEnd = c.getTime();

            List<PrefLogDO> tmplist = prefLogDao.queryNeedArchive(timeStart, timeEnd);
            if (tmplist != null) {
                prefLogDao.batchInsertToArchived(tmplist);
                prefLogDao.deleteRecoredByTime(timeStart, timeEnd);
            }
        }
    }

    public void deletePrefLogArchive(int dayBefore) {
        Calendar before = Calendar.getInstance();
        before.add(Calendar.DATE, -1);
        before.set(Calendar.HOUR_OF_DAY, 0);
        before.set(Calendar.MINUTE, 0);
        before.set(Calendar.SECOND, 0);

        prefLogDao.deleteArchivedRecoredByTime(before.getTime());

    }

}
