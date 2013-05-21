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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZipCompressor
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-6-7
 */
public class GZipCompressor implements Compressor {

	/**
	 * @see com.alibaba.doris.common.compress.Compressor#compress(byte[])
	 */
	public byte[] compress(byte[] bytes) {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream gos = null;
		try {
			gos = new GZIPOutputStream( bos );
			gos.write( bytes );
			gos.finish();
			
			byte[] outBytes = bos.toByteArray();			
			return outBytes;
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			
			try {	bos.close(); } catch (IOException e) {  }
			
			if( gos!= null)	try {	gos.close(); } catch (IOException e) {  }
		}
		return null;
	}

	/**
	 * @see com.alibaba.doris.common.compress.Compressor#uncompress(byte[])
	 */
	public byte[] decompress(byte[] bytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		GZIPInputStream gis = null;
		try {
			gis = new GZIPInputStream( bis );
			
			int i  = 0;
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while( ( i = gis.read()) !=-1) {
				bos.write(  i );
			}
			
			byte[] outBytes = bos.toByteArray();			
			return outBytes;
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {	bis.close(); } catch (IOException e) {  }
			
			if( gis!= null)	try {	gis.close(); } catch (IOException e) {  }
		}
		return null;
	}
	
	/**
	 * compress stream
	 */
	public void compressStream(InputStream is, OutputStream os) throws IOException {
		
		GZIPOutputStream gos = new GZIPOutputStream( os );
		
		int i = 0;
		while ( ( i= is.read()) != -1) {
			gos.write( i ) ;
		}
		gos.finish();
	}
	
	/**
	 * decompress stream
	 */
	public void decompressStream(InputStream is, OutputStream os) throws IOException {
		
		GZIPInputStream gis = new GZIPInputStream(is);
		
		int i=0;
		while ( ( i = gis.read()) != -1) {
			os.write( i ) ;
		}
		os.flush();
	}
}
