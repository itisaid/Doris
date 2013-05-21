package com.alibaba.doris.dataserver.action;

import junit.framework.TestCase;

import com.alibaba.doris.common.router.virtual.VirtualRouterImpl;
import com.alibaba.doris.dataserver.action.data.BaseActionType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ActionFactoryTest extends TestCase {

    {
        VirtualRouterImpl.setDebug(true);
    }

    public void testBaseCommandAction() {
        ActionFactory.registAction(BaseActionType.SET, new SetAction());
        ActionFactory.registAction(BaseActionType.GET, new GetAction());
        ActionFactory.registAction(BaseActionType.DELETE, new DeleteAction());
        ActionFactory.registAction(BaseActionType.ERROR, new CatchCommandErrorAction());
        ActionFactory.registAction(BaseActionType.EXIT, new ExitServerAction());

        // 测试是否所有的BaseCommand都注册了对应的Action
        BaseActionType[] typeList = new BaseActionType[] { BaseActionType.SET, BaseActionType.GET,
                BaseActionType.DELETE, BaseActionType.ERROR, BaseActionType.EXIT };
        for (BaseActionType type : typeList) {
            assertNotNull(ActionFactory.getAction(type));
        }
    }
}
