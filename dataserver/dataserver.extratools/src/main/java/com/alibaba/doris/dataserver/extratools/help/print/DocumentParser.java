package com.alibaba.doris.dataserver.extratools.help.print;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 文档解析器。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DocumentParser {

    public static void main(String[] args) {
        DocumentParser parser = new DocumentParser("help.txt");
        System.out.println(parser.sectionMap);
    }

    public DocumentParser(String helpFileName) {
        try {
            ClassLoader classLoader = DocumentParser.class.getClassLoader();
            InputStream in = classLoader.getResourceAsStream(helpFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            Section currentSection = null;

            while ((line = reader.readLine()) != null) {
                Section section = parseSection(line);
                if (null != section) {
                    sectionMap.put(section.getName(), section);
                    currentSection = section;
                    continue;
                }

                Line l = parseLine(line);
                if (null != l) {
                    currentSection.addLine(l);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Section getSection(String sectionName) {
        return sectionMap.get(sectionName);
    }

    private Line parseLine(String line) {
        line.trim();
        if (line.length() > 0) {
            if (line.charAt(0) == '-') {
                String command = line.substring(0, 2);
                String value = line.substring(3);
                if ("-S".equals(command)) {
                    return new SectionLine(sectionMap.get(value));
                } else if ("-L".equals(command)) {
                    return new LiterallyLine(value);
                } else if ("-T".equals(command)) {
                    return new TypeLine(value);
                }
            }
        }

        return new Line(line);
    }

    private Section parseSection(String line) {
        line.trim();
        if (line.length() > 0) {
            if (line.charAt(0) == '[') {
                int pos = line.lastIndexOf(']');
                return new Section(line.substring(1, pos));
            }
        }
        return null;
    }

    private Map<String, Section> sectionMap = new HashMap<String, Section>();
}
