package com.alibaba.doris.dataserver.core;

import junit.framework.TestCase;

import com.alibaba.doris.dataserver.BaseModule;
import com.alibaba.doris.dataserver.config.data.ModuleConfigure;

/*
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DefaultApplicationContextTest extends TestCase {

    public void testGetModuleByName() {
        // List<Module> moduleList = new ArrayList<Module>();
        // moduleList.add(new MockModule("module1"));
        // moduleList.add(new MockModule("module2"));
        // ApplicationContext context = new DefaultApplicationContext(moduleList);
        // assertNotNull(context.getModuleByName("module1"));
        // assertNotNull(context.getModuleByName("module2"));
        // assertNull(context.getModuleByName("module"));
        //
        // moduleList = new ArrayList<Module>();
        // context = new DefaultApplicationContext(moduleList);
        // assertNull(context.getModuleByName("module"));
    }

    public void testGetModule() {
        // List<Module> moduleList = new ArrayList<Module>();
        // moduleList.add(new MockModule("module1"));
        // moduleList.add(new MockModule("module2"));
        // ApplicationContext context = new DefaultApplicationContext(moduleList);
        // assertNotNull(context.getModule(MockModule.class));
        // assertNull(context.getModule(Module.class));
        //
        // moduleList = new ArrayList<Module>();
        // context = new DefaultApplicationContext(moduleList);
        // assertNull(context.getModule(Module.class));
    }

    private static class MockModule extends BaseModule {

        public MockModule(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void load(ModuleConfigure conf) {

        }

        public void unload() {

        }

        private String name;
    }
}
