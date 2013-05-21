package com.alibaba.doris.dataserver.event;

import junit.framework.TestCase;

import com.alibaba.doris.dataserver.event.server.DataServerEventListener;
import com.alibaba.doris.dataserver.event.server.MigrateEventListener;
import com.alibaba.doris.dataserver.event.server.ShutdownEvent;
import com.alibaba.doris.dataserver.event.server.StartMigratingEvent;
import com.alibaba.doris.dataserver.event.server.StartupEvent;
import com.alibaba.doris.dataserver.event.server.StopMigratingEvent;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class EventListenerManagerTest extends TestCase {

    public void testRegistEventListener() {
        // 没有注册任何监听器，调用fireEvent系统应该不抛出任何异常。
        try {
            eventListenerManager.fireEvent(new StartupEvent());
        } catch (Throwable e) {
            fail(e.getMessage());
        }

        TestDataServerEventListener testListener = new TestDataServerEventListener();
        eventListenerManager.registEventListener(testListener);
        eventListenerManager.fireEvent(new StartupEvent());

        testListener.assertStartup();
    }

    public void testFireStarupEvent() {
        TestDataServerEventListener testListener = new TestDataServerEventListener();
        eventListenerManager.registEventListener(testListener);
        eventListenerManager.fireEvent(new StartupEvent());

        testListener.assertStartup();
    }

    public void testShutdownEvent() {
        TestDataServerEventListener testListener = new TestDataServerEventListener();
        eventListenerManager.registEventListener(testListener);
        eventListenerManager.fireEvent(new ShutdownEvent());

        testListener.assertShutdown();
    }

    public void testStartMigratingEvent() {
        TestDataServerEventListener testListener = new TestDataServerEventListener();
        eventListenerManager.registEventListener(testListener);
        eventListenerManager.fireEvent(new StartMigratingEvent());

        testListener.assertStartMigrating();
    }

    public void testStopMigratingEvent() {
        TestDataServerEventListener testListener = new TestDataServerEventListener();
        eventListenerManager.registEventListener(testListener);
        eventListenerManager.fireEvent(new StopMigratingEvent());

        testListener.assertStopMigrating();
    }

    private static class TestDataServerEventListener implements DataServerEventListener, MigrateEventListener {

        public void onShutdown() {
            isShutdownEventArrived = true;
        }

        public void assertShutdown() {
            assertTrue(isShutdownEventArrived);
        }

        public void onStartMigrating() {
            isStartMigrating = true;
        }

        public void assertStartMigrating() {
            assertTrue(isStartMigrating);
        }

        public void onStartup() {
            isStartupEventArrived = true;
        }

        public void assertStartup() {
            assertTrue(isStartupEventArrived);
        }

        public void onStopMigrating() {
            isStopMigrating = true;
        }

        public void assertStopMigrating() {
            assertTrue(isStopMigrating);
        }

        private boolean isStartupEventArrived  = false;
        private boolean isShutdownEventArrived = false;
        private boolean isStartMigrating       = false;
        private boolean isStopMigrating        = false;

        public void onInterruptMigrating() {
        }
    }

    private EventListenerManager eventListenerManager = new EventListenerManager();
}
