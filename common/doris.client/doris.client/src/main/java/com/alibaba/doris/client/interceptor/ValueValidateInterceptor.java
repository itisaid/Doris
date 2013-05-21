/**
 * 
 */
package com.alibaba.doris.client.interceptor;

import com.alibaba.doris.client.operation.OperationData;
import com.alibaba.doris.client.validate.Validator;
import com.alibaba.doris.client.validate.ValueValidator;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.dproxy.AbstractInterceptor;
import com.alibaba.doris.dproxy.InvokeInfo;

/**
 * ValueValidateInterceptor for key and value.
 * @author Raymond He ( He Kun), raymond.he.kk@gmail.com
 * @since 1.0
 * 2011-6-23
 */
public class ValueValidateInterceptor extends AbstractInterceptor {
	
	private Validator valueValidator = new ValueValidator();
	
	@Override
	public void before(InvokeInfo info) throws Throwable {
		OperationData operationData = (OperationData) info.getArgs()[0];

		Value value = (Value)operationData.getArgs().get( 1 );
		
		if( value != null)
			 valueValidator.validate( value );
	}
}
