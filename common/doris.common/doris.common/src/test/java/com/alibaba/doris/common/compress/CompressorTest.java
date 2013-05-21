/*
Copyright(C) 2010-2011 Alibaba Group Holding Limited
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.alibaba.doris.common.compress;

import junit.framework.Assert;

import org.junit.Test;

/**
 * CompressorTest
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-6-7
 */
public class CompressorTest {
	
	private String data = "abcdfdfaKJFdilkairjfz/,fgnHJEI2838471-ZAKF00491F,ZLWGTHA;3KDHA";
	
	public byte[] buildTestData(int n) {
		
		byte[] bigData = new byte[n];
		
		for (int i = 0; i < n; i++) {			
			if( i % 5 == 0) bigData[i] = 'a'; 
			else if( i % 5 == 1) bigData[i] = 'b'; 
			else if( i % 5 == 2) bigData[i] = 'c'; 
			else if( i % 5 == 3) bigData[i] = 'd'; 
			else {
				bigData[i] = 'e'; 
			}
			
		}
		return bigData;
	}
	
	/**
	 * Test method for {@link com.alibaba.doris.common.compress.GZipCompressor#compress(byte[])}.
	 */
	@Test
	public void testCompress() {
		
		byte[] testdata = buildTestData( 2024 );
		
		Compressor compressor = new GZipCompressor();
		byte[] outs = compressor.compress(  testdata );
		
		Assert.assertNotNull("compress null ", outs);
		Assert.assertTrue("compress size smaller ", outs.length < testdata.length);
	}

	/**
	 * Test method for {@link com.alibaba.doris.common.compress.GZipCompressor#uncompress(byte[])}.
	 */
	@Test
	public void testUncompress() {
		Compressor compressor = new GZipCompressor();
		byte[] outs = compressor.compress(  data.getBytes() );
		
		byte[] decom = compressor.decompress( outs  );
		
		String result = new String(decom);
		Assert.assertEquals( data, result);
	}
//	
//	@Test
//	public void testCompressFile() {
//		
//		InputStream  is = null;
//		OutputStream os = null;
//		try {
//			is = new FileInputStream("D:/temp/abc.xml");
//			os = new FileOutputStream("D:/temp/abc.xml.gzip");
//			Compressor compressor = new GZipCompressor();
//			compressor.compressStream(is, os);
//			
//		} catch (FileNotFoundException e) {
//		} catch (IOException e) {
//		}finally {
//			if(is!=null) try { is.close(); } catch(IOException e) {} 
//			if(os!=null) try { os.close(); } catch(IOException e) {} 
//		}
//	}
//	
//	@Test
//	public void testDecompressFile() {
//		
//		InputStream  is = null;
//		OutputStream os = null;
//		try {
//			is = new FileInputStream("D:/temp/abc.xml.gzip");
//			os = new FileOutputStream("D:/temp/abc.xml.unzip");
//			Compressor compressor = new GZipCompressor();
//			compressor.decompressStream(is, os);
//			
//		} catch (FileNotFoundException e) {
//		} catch (IOException e) {
//		}finally {
//			if(is!=null) try { is.close(); } catch(IOException e) {} 
//			if(os!=null) try { os.close(); } catch(IOException e) {} 
//		}
//	}
}
