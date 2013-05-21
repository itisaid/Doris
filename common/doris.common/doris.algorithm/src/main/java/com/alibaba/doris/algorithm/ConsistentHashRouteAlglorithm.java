package com.alibaba.doris.algorithm;

import java.util.Collection;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 
 * ConsistentHashRouteAlglorithm
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-10
 */
public class ConsistentHashRouteAlglorithm implements RouteAlgorithm {

	 private HashFunction hashFunction;
	 private int numberOfReplicas;
	 
	 private int nodeCount;
	 
	 private Collection<Integer> nodes;
	 
	 private SortedMap<Integer, Integer> circle = new TreeMap<Integer, Integer>();
	 
	 private Properties configProperties;
	 
	 public static final String _Route_ConsistentHashReplicas = "route.consistenthash.replicas";
	 public static final String _Route_ConsistentHashNodeCount = "route.consistenthash.nodecount";
	 
	 public ConsistentHashRouteAlglorithm() {
		 
	 }
	 
	 public ConsistentHashRouteAlglorithm(HashFunction hashFunction, int numberOfReplicas,
	     Collection<Integer> nodes) {
	   this.hashFunction = hashFunction;
	   this.numberOfReplicas = numberOfReplicas;
	   this.nodes = nodes;
	 }
	 
	 public void setConfigProperties(Properties properties) {
		this.configProperties = properties;		
	 }
	 
	 public void setNumberOfReplicas(int numberOfReplicas) {
		this.numberOfReplicas = numberOfReplicas;
	 }
	 
	 public void setNodeCount(int nodeCount) {
		 this.nodeCount = nodeCount;			
	 }
	 
	 public void addNode(Integer node) {
	   for (int i = 0; i < numberOfReplicas; i++) {
	     circle.put(hashFunction.hash(node.toString() + i), node);
	   }
	 }

	 public void removeNode(Integer node) {
	   for (int i = 0; i < numberOfReplicas; i++) {
	     circle.remove(hashFunction.hash(node.toString() + i));
	   }
	 }

	 public Integer getNodeByKey(String key) {
	   if (circle.isEmpty()) {
	     return null;
	   }
	   int hash = hashFunction.hash(key);
	   if (!circle.containsKey(hash)) {
	     SortedMap<Integer, Integer> tailMap = circle.tailMap(hash);
	     hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
	   }
	   return circle.get(hash);
	 }
	 
	 /**
	 * @param nodes
	 */
	 @SuppressWarnings("unchecked")
	public void initConfig() {
		 
		//_Route_hashfunction_class
		String hashFunctionClassName = configProperties.getProperty( RouteAlgorithm._Route_hashfunction_class );
		Class<? extends HashFunction> hashFunctionClass;
		
		if( hashFunctionClassName == null) {
			hashFunctionClassName = KetamaHashFunction.class.getName();
		}
		
		//hashFunctionClassName
		try {
			hashFunctionClass = (Class<? extends HashFunction>) Thread.currentThread().getContextClassLoader().loadClass( hashFunctionClassName );
			hashFunction = hashFunctionClass.newInstance();
		} catch (Exception e1) {
			throw new IllegalArgumentException("Invalid property '" + RouteAlgorithm._Route_hashfunction_class  +"' " + hashFunctionClassName  +". Cause: " + e1.getMessage());
		}
		
		try {
			numberOfReplicas = Integer.valueOf(configProperties.getProperty( _Route_ConsistentHashReplicas ));
		}catch(Exception e) {
			throw new IllegalArgumentException("Invalid property '" + _Route_ConsistentHashReplicas +"' " + configProperties.getProperty( _Route_ConsistentHashReplicas ) + ". It must be a integer.");
		}
		
		//init nodes.
		init();
		
	 }
	 
	/**
	 * 
	 */
	public void init() {
				
		if( hashFunction == null)
			hashFunction = new KetamaHashFunction();
		
		for (int i = 0; i < nodeCount; i++) {
			Integer node = Integer.valueOf(i);
			addNode(node);
		}
	}

    public Integer getVirtualByKey(String key) {
        // TODO Auto-generated method stub
        return null;
    }
}