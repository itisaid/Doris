package com.alibaba.doris.dataserver.action.parser;

import com.alibaba.doris.dataserver.action.data.CommonActionData;
import com.alibaba.doris.dataserver.action.data.CompareAndSetActionData;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class CompareAndSetActionParser extends SetActionParser {

    @Override
    protected CommonActionData generateActionData() {
        return new CompareAndSetActionData();
    }
}
