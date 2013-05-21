package com.alibaba.doris.common.serialize;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.alibaba.doris.common.StoreNode;

public class JavaObjectSerializerTest {

	@Test
	public void testSerializeNull() {
		Serializer serializer = new JavaObjectSerializer();
		
		byte[] bvalue = serializer.serialize(null, null);
		
		char c = (char) Byte.valueOf( (byte)0 ).byteValue();
		
		byte s = "0".getBytes()[0] ;
				
		System.out.println("asc 0: " +  s +",  c: " + c);
//		Assert.fail("Null value failed!");
	}
	
	@Test
	public void testSerialize() {
		Serializer serializer = new JavaObjectSerializer();
		
		String stringObj = "key001";
		byte[] bvalue = serializer.serialize(stringObj, null);
		
		Assert.assertTrue("string serialize result", bvalue != null);
	}

	@Test
	public void testDeserialize() {
		Serializer serializer = new JavaObjectSerializer();
		
		String stringObj = "key001";
		byte[] bvalue = serializer.serialize(stringObj, null);
		
		String result = (String) serializer.deserialize(bvalue, null);
		Assert.assertEquals("string serialize result",stringObj,  result);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSerializeMapObject() {
		Serializer serializer = new JavaObjectSerializer();
		
		Map<String,String>  map = new HashMap<String, String>();
		map.put( "key1", "value1");
		map.put( "key2" , "value2");
		
		byte[] bvalue = serializer.serialize(map, null);

		Map<String,String> result = (Map<String,String>) serializer.deserialize(bvalue, null);
		Assert.assertEquals("string serialize result","value1", result.get("key1"));
		Assert.assertEquals("string serialize result","value2", result.get("key2"));
	}
	
	@Test
	public void testSerializeCompoundObject() {
		Serializer serializer = new JavaObjectSerializer();
		
		Product product = new Product();
		product.setId("101");
		product.setName("Product001");
		
		byte[] bvalue = serializer.serialize(product, null);

		Product  result = (Product) serializer.deserialize(bvalue, null);
		Assert.assertEquals("string serialize result", "101"  ,  result.getId() );
		Assert.assertEquals("string serialize result","Product001" , result.getName() );
	}
}
