package com.alibaba.doris.admin.web.monitor.support;

import java.text.DecimalFormat;

public class PrefMathTool {
    private static final Number NaN = Double.NaN;

    public Object div(Number a, Number b) {
        if (a == null || b == null) {
            return NaN;
        }
        DecimalFormat myformat = new DecimalFormat("#0.000");
        return myformat.format(a.doubleValue() / b.doubleValue());
    }

    public Object format(Number a) {
        if (a == null) {
            return NaN;
        }
        DecimalFormat myformat = new DecimalFormat("#0.000");
        return myformat.format(a.doubleValue());
    }
}
