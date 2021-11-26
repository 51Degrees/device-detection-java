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
 
 package fiftyone.pipeline.uachmanual.examples.servlet;

import static org.junit.Assert.assertEquals;
import static fiftyone.devicedetection.shared.testhelpers.ClientHintsExampleTestBase.*;
import static fiftyone.devicedetection.shared.testhelpers.Constants.*;

import fiftyone.devicedetection.shared.testhelpers.Utils;
import fiftyone.devicedetection.shared.testhelpers.WebExampleTestBase;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.catalina.LifecycleException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runner.RunWith;

@RunWith(BuilderClassPathTestRunner.class)
public class UACHHashTests extends WebExampleTestBase{

	public String userAgent;
	public String properties;
	public String expectedResult;
	public static HttpServlet servlet;
	public static final String servletName = "UAClientHintsManualExample";
	public static final String urlPattern = "/pipeline.uachmanual.examples.servlet";
    private static final String dataFile = Utils.getFilePath(HASH_DATA_FILE_NAME).getAbsolutePath();	
	
    @BeforeClass
    public static void setUpClass() throws LifecycleException, ServletException {
    	servlet = new UAClientHintsManualExample();
        startTomcat(servletName, urlPattern, servlet);    
    }

    // Each parameter should be placed as an argument here
    // Every time runner triggers, it will pass the arguments
    // from parameters we defined in uachTestInput() method	
    public UACHHashTests(String userAgent, String properties, String expectedResult) {
       this.userAgent = userAgent;
       this.properties = properties;
       this.expectedResult = expectedResult;
    }
    
    @Parameterized.Parameters
    public static Collection uachTestInput() {
        return Arrays.asList(new Object[][] {
            { CHROME_UA, ALL_PROPERTIES, SUPER_ACCEPT_CH },
            { CHROME_UA, PLATFORM_PROPERTIES, PLATFORM_ACCEPT_CH },
            { CHROME_UA, HARDWARE_PROPERTIES, HARDWARE_ACCEPT_CH },
            { CHROME_UA, BROWSER_PROPERTIES, BROWSER_ACCEPT_CH },
            { CHROME_UA, BASE_PROPERTIES, null },
            //{ EDGE_UA, ALL_PROPERTIES, SUPER_ACCEPT_CH },
            //{ EDGE_UA, PLATFORM_PROPERTIES, PLATFORM_ACCEPT_CH },
            //{ EDGE_UA, HARDWARE_PROPERTIES, HARDWARE_ACCEPT_CH },
            //{ EDGE_UA, BROWSER_PROPERTIES, BROWSER_ACCEPT_CH },
            //{ EDGE_UA, BASE_PROPERTIES, null },
            { FIREFOX_UA, ALL_PROPERTIES, null },
            { FIREFOX_UA, PLATFORM_PROPERTIES, null },
            { FIREFOX_UA, HARDWARE_PROPERTIES, null },
            { FIREFOX_UA, BROWSER_PROPERTIES, null },
            { FIREFOX_UA, BASE_PROPERTIES, null },
            { SAFARI_UA, ALL_PROPERTIES, null },
            { SAFARI_UA, PLATFORM_PROPERTIES, null },
            { SAFARI_UA, HARDWARE_PROPERTIES, null },
            { SAFARI_UA, BROWSER_PROPERTIES, null },
            { SAFARI_UA, BASE_PROPERTIES, null },          
            { CURL_UA, ALL_PROPERTIES, null },
            { CURL_UA, PLATFORM_PROPERTIES, null },
            { CURL_UA, HARDWARE_PROPERTIES, null },
            { CURL_UA, BROWSER_PROPERTIES, null },
            { CURL_UA, BASE_PROPERTIES, null },         
         });
    }
    
    @Test
    public void UACH_Hash_Test() throws Exception {
    	
        HttpURLConnection connection = null;
        URL url = new URL(HASH_URL);
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("user-agent", userAgent);
        connection.setRequestProperty("properties", properties);
        Path path = Paths.get(dataFile);
        if (Files.exists(path)) {
            connection.setRequestProperty("datafile", dataFile);        	
        }
        connection.connect();
        
        int code = connection.getResponseCode();
        String acceptCHHeader = connection.getHeaderField("Accept-CH");

        assertEquals("Request was unsuccessful.", 200, code);
        assertEquals("The values in Accept-CH does not match the expected values.", 
        		        expectedResult != null ? expectedResult.toLowerCase() : null,
        		        		acceptCHHeader != null ? acceptCHHeader.toLowerCase() : null);        
    }
}