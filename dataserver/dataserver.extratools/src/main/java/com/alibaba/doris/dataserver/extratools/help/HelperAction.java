package com.alibaba.doris.dataserver.extratools.help;

import com.alibaba.doris.dataserver.action.Action;
import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.Response;
import com.alibaba.doris.dataserver.extratools.help.print.DocumentParser;
import com.alibaba.doris.dataserver.extratools.help.print.Section;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class HelperAction implements Action {

    public void execute(Request request, Response response) {
        if (null == parser) {
            parser = new DocumentParser("help.txt");
        }

        HelperActionData actionData = (HelperActionData) request.getActionData();
        Section section = parser.getSection(actionData.getSubCommand());
        if (null != section) {
            section.print(response);
            response.write(actionData);
            return;
        }

        response.write(actionData);
        throw new RuntimeException("Unknown sub command:" + actionData.getSubCommand());
    }

    private DocumentParser parser;
}
