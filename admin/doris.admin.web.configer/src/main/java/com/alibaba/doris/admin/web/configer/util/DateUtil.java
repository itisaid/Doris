package com.alibaba.doris.admin.web.configer.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateUtil {

    private static final Log logger = LogFactory.getLog(DateUtil.class);

    public static void setStartTimeAndEndTime(String startTime, String endTime, Calendar start, Calendar end) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

        if (StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime)) {
            // 当前时间向前一天（24小时）内的
            setTime(start, 0, 0, 0, 0);
            setTime(end, 23, 59, 59, 999);
        } else if (!StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime)) {
            try {
                start.setTime(sf.parse(startTime));

                end.setTime(start.getTime());
                setTime(end, 23, 59, 59, 999);
            } catch (ParseException e) {
                logger.error(e);
            }
        } else if (StringUtils.isBlank(startTime) && !StringUtils.isBlank(endTime)) {
            try {
                end.setTime(sf.parse(endTime));

                start.setTime(end.getTime());
                setTime(start, 0, 0, 0, 0);
                setTime(end, 23, 59, 59, 999);
            } catch (ParseException e) {
                logger.error(e);
            }
        } else {
            try {
                end.setTime(sf.parse(endTime));
                start.setTime(sf.parse(startTime));

                // start.setTime(end.getTime());
                setTime(end, 23, 59, 59, 999);
            } catch (ParseException e) {
                logger.error(e);
            }
        }
    }

    private static void setTime(Calendar end, int hour, int minute, int second, int millsecond) {
        end.set(Calendar.HOUR_OF_DAY, hour);
        end.set(Calendar.MINUTE, minute);
        end.set(Calendar.SECOND, second);
        end.set(Calendar.MILLISECOND, millsecond);
    }

}
