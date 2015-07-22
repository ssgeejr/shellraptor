/**
 * 
 */
package com.gee5.tools.shellraptor;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author sgee
 *
 */
public class ShellRaptorUnitTest {
	public static Column testItem = null;
	
	private static String columnName = "username";
	private static String columnType = "varchar";
	private static String columnLength = "32";
	private static String decimalLength = "0";
	private static String nullable = "1";
	private static String defaultValue = "DEFAULT-VALUE";
	private static String remarks = "these are my test remarks";
	
	/**
	 * Standard Constructor, called before every test
	 */
	public ShellRaptorUnitTest(){}
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		   * @param cname Column Name
//		   * @param cType Column Type
//		   * @param cLen Column Length
//		   * @param dLen Decimal Length
//		   * @param cNull Nullable
//		   * @param defVal Default Value
//		   * @param rmk Rarks
		testItem = new Column(columnName,columnType,columnLength,decimalLength,nullable,defaultValue,remarks);
	}

	/**
	 * @throws java.lang.Exception
	 */
//	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
//	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
//	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.gee5.tools.shellraptor.Column#Column(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAssertNotNull() {
		System.out.println("assertNotNull [" + testItem + "]");
		assertNotNull("Object was null"  ,testItem);
	}

	/**
	 * Test method for {@link com.gee5.tools.shellraptor.Column#getColumnName()}.
	 */
	@Test
	public void testGetColumnName() {
		System.out.println("testGetColumnName [" + testItem.getColumnName() + "][" + columnName + "]");
		assertEquals("Column Names does not match"  , testItem.getColumnName(), columnName);
	}

	/**
	 * Test method for {@link com.gee5.tools.shellraptor.Column#getColumnType()}.
	 */
	@Test
	public void testGetColumnType() {
		System.out.println("testGetColumnType [" + testItem.getColumnType() + "][" + columnType + "(32,0)]");
		assertEquals("Column Type does not match"  , testItem.getColumnType(), columnType + "(32,0)");
	}

	/**
	 * Test method for {@link com.gee5.tools.shellraptor.Column#getDefaultValue()}.
	 */
	@Test
	public void testGetDefaultValue() {
		System.out.println("testGetDefaultValue [" + testItem.getDefaultValue() + "][" + defaultValue + "]");
		assertEquals("Default Value does not match"  , testItem.getDefaultValue(), defaultValue);
	}

}
