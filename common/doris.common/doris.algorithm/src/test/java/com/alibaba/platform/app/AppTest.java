package com.alibaba.platform.app;

import com.alibaba.doris.algorithm.util.MD5Util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        String s = "fssddsdfsdf";
        String t = s+"1";
        System.out.println(MD5Util.md5(s));
        System.out.println(MD5Util.md5HashCode(s));
        
        System.out.println(MD5Util.md5(t));
        System.out.println(MD5Util.md5HashCode(t));
    }
}
