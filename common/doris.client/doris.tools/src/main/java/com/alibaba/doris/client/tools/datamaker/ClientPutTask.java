/**
 * 
 */
package com.alibaba.doris.client.tools.datamaker;

import org.apache.commons.lang.StringUtils;

/**
 * @author raymond
 */
public class ClientPutTask extends ClentBaseTask {

    public ClientPutTask() {

    }

    @Override
    public void doRun(long index) {
        String key = kp + index;
        String value = vp + index;

        if (vl > 0) {
            value = StringUtils.repeat(vp, vl);
            value = vp + index + "_" + value;
        }

        if ("get".equals(operation)) {

            Object retVal = dataStore.get(key);

            if (isNeedPofiling() && !value.equals(retVal)) {
                System.err.println("value not got, ns=" + dataStore.getNamespace().getName() + ",id="
                                   + dataStore.getNamespace().getId() + ",  key=" + key);
            }

        } else if ("put".equals(operation)) {

            dataStore.put(key, value);
            // System.err.println("put: key=" + key + ", value=" + value);
        } else if ("delete".equals(operation)) {
            dataStore.delete(key);
        } else {
            // get
            dataStore.get(key);
        }
    }
}
