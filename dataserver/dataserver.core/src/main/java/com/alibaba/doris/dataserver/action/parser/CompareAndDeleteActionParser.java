package com.alibaba.doris.dataserver.action.parser;

import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.data.CompareAndDeleteActionData;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class CompareAndDeleteActionParser extends DeleteActionParser {

    public ActionData readHead(byte[] header, int startPos) {
        CompareAndDeleteActionData ad = new CompareAndDeleteActionData();
        int[] pos = { startPos };
        readKeyBytes(ad, header, pos);
        ad.setTimestamp(Long.valueOf(parseNextField(header, pos)));
        readRouteVersion(ad, header, pos);
        readVnode(ad, header, pos);
        return ad;
    }
}
