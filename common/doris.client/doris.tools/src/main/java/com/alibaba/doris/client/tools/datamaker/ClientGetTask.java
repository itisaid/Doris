package com.alibaba.doris.client.tools.datamaker;

import org.apache.commons.lang.StringUtils;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ClientGetTask extends ClentBaseTask {

    @Override
    public void doRun(long index) {
        String key = kp + index;
        String value = vp + index;

        if (vl > 0) {
            value = StringUtils.repeat(vp, vl);
            value = vp + index + "_" + value;
        }

        Object retVal = dataStore.get(key);

        if (isNeedPofiling() && !value.equals(retVal)) {
            System.err.println("value not got, ns=" + dataStore.getNamespace().getName() + ",id="
                               + dataStore.getNamespace().getId() + ",  key=" + key);
        }
    }
}
