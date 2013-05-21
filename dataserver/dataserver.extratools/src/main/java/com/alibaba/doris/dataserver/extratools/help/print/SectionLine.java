package com.alibaba.doris.dataserver.extratools.help.print;

import com.alibaba.doris.dataserver.core.Response;
import com.alibaba.doris.dataserver.extratools.help.HelperActionData;

/**
 * 段落行，这是一个特殊的文字行，其内容由一段（多行）内容组成。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class SectionLine extends Line {

    public SectionLine(Section section) {
        super(null);
        this.section = section;
    }

    @Override
    public void print(Response response) {
        section.print(response);
        HelperActionData md = new HelperActionData();
        md.setMessage("\r\n");
        response.write(md);
        response.flush();
    }

    private Section section;
}
