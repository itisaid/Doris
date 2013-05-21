/**
 * 
 */
package com.alibaba.doris.dataserver.migrator.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author raymond
 *
 */
public class DateUtil {
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
	
	public static String formatDate(long time ) {
		return formatDate( new Date(time));
	}
	
	public static String formatDate(Date date ) {
		return dateFormat.format( date );
	}
	
	public static void main(String[] args) {
		System.out.println("date: " + formatDate(System.currentTimeMillis()));
		System.out.println("date: " + formatDate( 0 ));
		System.out.println("date: " + formatDate( -1 ));
	}
}
