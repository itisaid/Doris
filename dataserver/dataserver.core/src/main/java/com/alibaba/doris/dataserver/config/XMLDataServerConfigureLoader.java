package com.alibaba.doris.dataserver.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.doris.dataserver.config.data.FilterConfigure;
import com.alibaba.doris.dataserver.config.data.ModuleConfigure;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class XMLDataServerConfigureLoader {

    public static void main(String[] args) {
        XMLDataServerConfigureLoader loader = new XMLDataServerConfigureLoader("dataserver.xml");
        try {
            loader.load();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public XMLDataServerConfigureLoader(String fileName) {
        this.fileName = fileName;
    }

    public DataServerConfigure load() throws DocumentException {
        DataServerConfigure dsConfig = new DataServerConfigure();
        Document doc = openDocument();
        Element root = doc.getRootElement();
        for (Iterator<?> i = root.elementIterator(); i.hasNext();) {
            Element element = (Element) i.next();
            if (ELEMENT_NAME_MODULES.equalsIgnoreCase(element.getName())) {
                dsConfig.setModuleConfigList(iterateModules(element));
                continue;
            }
        }
        return dsConfig;
    }

    public Document openDocument() throws DocumentException {
        Document document = openDocumentFromAbsFileName();
        if (null == document) {
            document = openDocumentFromRelativeFileName();
        }
        return document;
    }

    private Document openDocumentFromAbsFileName() throws DocumentException {
        File f = new File(fileName);
        if (f.exists()) {
            try {
                InputStream inStream = new FileInputStream(f);
                SAXReader reader = new SAXReader();
                Document document = reader.read(inStream);
                return document;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    private Document openDocumentFromRelativeFileName() throws DocumentException {
        URL url = XMLDataServerConfigureLoader.class.getClassLoader().getResource(fileName);
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(url.getFile()));
        return document;
    }

    public List<ModuleConfigure> iterateModules(Element element) {
        List<ModuleConfigure> modulesList = new ArrayList<ModuleConfigure>();
        for (Iterator<?> i = element.elementIterator(); i.hasNext();) {
            Element module = (Element) i.next();
            modulesList.add(getModuleConfig(module));
        }
        return modulesList;
    }

    public List<FilterConfigure> iterateFilters(Element element) {
        List<FilterConfigure> filterList = new ArrayList<FilterConfigure>();
        for (Iterator<?> i = element.elementIterator(); i.hasNext();) {
            Element module = (Element) i.next();
            if (ELEMENT_NAME_FILTER.equalsIgnoreCase(module.getName())) {
                filterList.add(getFilterConfigure(module));
                continue;
            }
        }
        return filterList;
    }

    private ModuleConfigure getModuleConfig(Element element) {
        if (ELEMENT_NAME_MODULE.endsWith(element.getName())) {
            ModuleConfigure moduleConfig = new ModuleConfigure();
            for (Iterator<?> i = element.elementIterator(); i.hasNext();) {
                Element module = (Element) i.next();
                String name = module.getName();
                if (ELEMENT_NAME_PARAMS.equalsIgnoreCase(name)) {
                    readParameters(moduleConfig, module);
                    continue;
                }

                if (ELEMENT_NAME_CLASS.equalsIgnoreCase(name)) {
                    moduleConfig.setClassName(element.elementText(ELEMENT_NAME_CLASS));
                    continue;
                }

                if (ELEMENT_NAME_DESCRIPTION.equalsIgnoreCase(name)) {
                    moduleConfig.setDescription(element.elementText(ELEMENT_NAME_DESCRIPTION));
                    continue;
                }

                if (ELEMENT_NAME_FILTERS.equalsIgnoreCase(name)) {
                    moduleConfig.setFilterConfigList(iterateFilters(module));
                    continue;
                }

                if (MODULE_ELEMENT_NAME_NAME.equalsIgnoreCase(name)) {
                    moduleConfig.setName(element.elementText(MODULE_ELEMENT_NAME_NAME));
                    continue;
                }
            }
            return moduleConfig;
        }
        return null;
    }

    private void readParameters(ModuleConfigure moduleConfig, Element element) {
        for (Iterator<?> i = element.elementIterator(); i.hasNext();) {
            Element module = (Element) i.next();
            if (ELEMENT_NAME_PARAM.equalsIgnoreCase(module.getName())) {
                getParameter(moduleConfig, module);
                continue;
            }
        }
    }

    private void getParameter(ModuleConfigure moduleConfig, Element element) {
        String paramName = null;
        String paramValue = null;
        for (Iterator<?> i = element.elementIterator(); i.hasNext();) {
            Element module = (Element) i.next();
            String name = module.getName();
            if (ELEMENT_NAME_NAME.equalsIgnoreCase(name)) {
                paramName = element.elementText(ELEMENT_NAME_NAME);
                continue;
            }

            if (ELEMENT_NAME_VALUE.equalsIgnoreCase(name)) {
                paramValue = element.elementText(ELEMENT_NAME_VALUE);
                continue;
            }
        }

        if (paramName != null) {
            moduleConfig.addParam(paramName, paramValue);
        }
    }

    private FilterConfigure getFilterConfigure(Element element) {
        FilterConfigure filterConfigure = new FilterConfigure();
        for (Iterator<?> i = element.elementIterator(); i.hasNext();) {
            Element module = (Element) i.next();
            String name = module.getName();
            if (ELEMENT_NAME_CLASS.equalsIgnoreCase(name)) {
                filterConfigure.setClassName(element.elementText(ELEMENT_NAME_CLASS));
                continue;
            }
        }

        return filterConfigure;
    }

    private String        fileName;
    private static String ELEMENT_NAME_DESCRIPTION = "description";
    private static String ELEMENT_NAME_MODULES     = "modules";
    private static String ELEMENT_NAME_MODULE      = "module";
    private static String ELEMENT_NAME_FILTERS     = "filters";
    private static String ELEMENT_NAME_FILTER      = "filter";
    private static String ELEMENT_NAME_PARAMS      = "params";
    private static String ELEMENT_NAME_PARAM       = "param";
    private static String ELEMENT_NAME_VALUE       = "value";
    private static String ELEMENT_NAME_CLASS       = "class";
    private static String ELEMENT_NAME_NAME        = "name";
    private static String MODULE_ELEMENT_NAME_NAME = "name";
}
