/* *********************************************************************
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2019 51 Degrees Mobile Experts Limited, 5 Charlotte Close,
 * Caversham, Reading, Berkshire, United Kingdom RG4 7BY.
 *
 * This Original Work is licensed under the European Union Public Licence (EUPL) 
 * v.1.2 and is subject to its terms as set out below.
 *
 * If a copy of the EUPL was not distributed with this file, You can obtain
 * one at https://opensource.org/licenses/EUPL-1.2.
 *
 * The 'Compatible Licences' set out in the Appendix to the EUPL (as may be
 * amended by the European Commission) shall be deemed incompatible for
 * the purposes of the Work and the provisions of the compatibility
 * clause in Article 5 of the EUPL shall not apply.
 * 
 * If using the Work as, or as part of, a network application, by 
 * including the attribution notice(s) required under Article 5 of the EUPL
 * in the end user terms of the application under an appropriate heading, 
 * such notice(s) shall fulfill the requirements of that article.
 * ********************************************************************* */
 
package fiftyone.devicedetection.shared.testhelpers;

import static fiftyone.devicedetection.shared.testhelpers.Constants.*;
import static org.junit.Assert.fail;

public class ClientHintsExampleTestBase {
	
	public static String SUPER_RESOURCEKEY = getEnvVar(RESOURCE_KEY_ENV_VAR, "Super Resource Key");

	public static String ALL_PROPERTIES = null;
	public static String BASE_PROPERTIES = "HardwareVendor,HardwareName,DeviceType,PlatformVendor,PlatformName,PlatformVersion,BrowserVendor,BrowserName,BrowserVersion";
    public static String PLATFORM_PROPERTIES = BASE_PROPERTIES + ",SetHeaderPlatformAccept-CH";
	public static String HARDWARE_PROPERTIES = BASE_PROPERTIES + ",SetHeaderHardwareAccept-CH";
	public static String BROWSER_PROPERTIES = BASE_PROPERTIES + ",SetHeaderBrowserAccept-CH";
	
	public static String CHROME_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
			+ "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36";
	public static String EDGE_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
			+ "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36 "
			+ "Edg/95.0.1020.44";
	public static String FIREFOX_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64;"
			+ " rv:94.0) Gecko/20100101 Firefox/94.0";
	public static String SAFARI_UA = "Mozilla/5.0 (iPhone; CPU iPhone OS 15_1 "
			+ "like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 "
			+ "Mobile/15E148 Safari/604.1";
	public static String CURL_UA = "curl/7.80.0";

	public static String SUPER_ACCEPT_CH = "SEC-CH-UA-Platform,SEC-CH-UA-Platform-Version,SEC-CH-UA-Model,"
			+ "SEC-CH-UA-Mobile,SEC-CH-UA-Arch,SEC-CH-UA,SEC-CH-UA-Full-Version";
	public static String PLATFORM_ACCEPT_CH = "SEC-CH-UA-Platform,SEC-CH-UA-Platform-Version";
	public static String HARDWARE_ACCEPT_CH = "SEC-CH-UA-Model,SEC-CH-UA-Mobile,SEC-CH-UA-Arch";
	public static String BROWSER_ACCEPT_CH = "SEC-CH-UA,SEC-CH-UA-Full-Version";

	public static int TEST_PORT_NUMBER = 8080;
	public static String BASE_URL = "http://localhost:"+ TEST_PORT_NUMBER + "/";
	public static String CLOUD_URL = BASE_URL + "pipeline.uachmanual.cloud.examples.servlet";
	public static String HASH_URL = BASE_URL + "pipeline.uachmanual.examples.servlet";
	

    /*
     *  Function to get environment variable value.
     */
    private static String getEnvVar(String name, String description) {
    	String resourceKey = null;
    	String envResourceKey =  System.getenv(name);
    	String propertyResourceKey = System.getProperty(name);
    	    	
        if(envResourceKey == null || envResourceKey.isEmpty()) {
        	if (propertyResourceKey == null || propertyResourceKey.isEmpty())
        		fail(description + " needs to be set in " + name + " environment variable to run Cloud tests.");
        	else {
        		   resourceKey = propertyResourceKey;
        	}
        }
        else {
            	resourceKey = envResourceKey;
        }       
        return resourceKey;
    }
}
