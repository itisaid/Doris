package com.alibaba.doris.dataserver.extratools.help.print;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.doris.dataserver.core.Response;

/**
 * 管理一段文字，每段文字都由不同的行组成，有时段落也可以抽象成一个段落行。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class Section implements Printer {

    public Section(String name) {
        this.name = name;
    }

    public void print(Response response) {
        for (Line line : lines) {
            line.print(response);
        }
    }

    public void addLine(Line line) {
        lines.add(line);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "[" + name + "] line number:" + lines.size();
    }

    private List<Line> lines = new ArrayList<Line>();
    private String     name;
}
