package com.alibaba.doris.client.tools.datamaker;

import com.alibaba.doris.client.DataStore;
import com.alibaba.doris.client.tools.concurrent.ParralelTaskImpl;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ClentBaseTask extends ParralelTaskImpl {

    protected DataStore dataStore;
    protected String    kp;
    protected String    operation;
    protected String    p;
    protected String    vp;
    protected int       vl;

    public String getVp() {
        return vp;
    }

    public void setVp(String vp) {
        this.vp = vp;
    }

    public void setVl(int vl) {
        this.vl = vl;
    }

    public int getVl() {
        return vl;
    }

    public String getP() {
        return p;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public DataStore getDataStore() {
        return dataStore;
    }

    public void setDataStore(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public String getKp() {
        return kp;
    }

    public void setKp(String kp) {
        this.kp = kp;
    }
}
