package com.alibaba.doris.dataserver.action;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.doris.dataserver.action.parser.ActionParser;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ActionFactory {

    private ActionFactory() {
    }

    public static Action getAction(ActionType commandType) {
        ActionEntry entry = factory.actionMap.get(commandType.getName());
        if (null != entry) {
            return entry.action;
        }
        return null;
    }

    public static ActionParser getActionParser(String commandName) {
        ActionEntry entry = factory.actionMap.get(commandName);
        if (null != entry) {
            return entry.actionParser;
        }
        return null;
    }

    /**
     * Note:the register function is not thread safe.
     * 
     * @param commandType
     * @param action
     */
    public static void registAction(ActionType commandType, Action action) {
        ActionEntry entry = new ActionEntry();
        entry.action = action;
        entry.actionParser = commandType.getParser();
        factory.actionMap.put(commandType.getName(), entry);
    }

    private Map<String, ActionEntry> actionMap = new HashMap<String, ActionEntry>();
    private static ActionFactory     factory   = new ActionFactory();

    private static class ActionEntry {

        public Action       action;
        public ActionParser actionParser;
    }
}
