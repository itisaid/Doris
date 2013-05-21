package com.alibaba.doris.algorithm;

/**
 * 
 * HashFunction
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-7
 */
public interface HashFunction {
	int hash(String o);
}
