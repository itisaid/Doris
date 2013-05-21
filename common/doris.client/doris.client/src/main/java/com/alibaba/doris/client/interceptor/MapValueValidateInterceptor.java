/**
 * 
 */
package com.alibaba.doris.client.interceptor;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.doris.client.operation.OperationData;
import com.alibaba.doris.client.validate.Validator;
import com.alibaba.doris.client.validate.ValueValidator;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.dproxy.AbstractInterceptor;
import com.alibaba.doris.dproxy.InvokeInfo;

/**
 * 
 * @author frank
 *
 */
public class MapValueValidateInterceptor extends AbstractInterceptor {
	
	private Validator valueValidator = new ValueValidator();
	
	@Override
	public void before(InvokeInfo info) throws Throwable {
		OperationData operationData = (OperationData) info.getArgs()[0];

		
		List<Object> args = operationData.getArgs();

        Map<Object, Object> map = (Map<Object, Object>) args.get(0);

        for (Entry<Object, Object> e : map.entrySet()) {
            valueValidator.validate(e.getValue());
        }
		
	
	}
}
