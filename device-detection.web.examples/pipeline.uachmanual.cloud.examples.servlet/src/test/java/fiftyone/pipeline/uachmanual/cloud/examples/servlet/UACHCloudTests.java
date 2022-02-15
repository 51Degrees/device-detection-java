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
 
 package fiftyone.pipeline.uachmanual.cloud.examples.servlet;

import fiftyone.pipeline.web.examples.shared.EmbedTomcat;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.servlet.http.HttpServlet;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static fiftyone.devicedetection.shared.testhelpers.ClientHintsExampleTestBase.*;
import static fiftyone.pipeline.uachmanual.cloud.examples.servlet.UAClientHintsManualExample.RESOURCE_KEY_PARAMETER_NAME;
import static org.junit.Assert.assertEquals;

@RunWith(BuilderClassPathTestRunner.class)
public class UACHCloudTests extends EmbedTomcat {

	public String userAgent;
	public String resourceKey;
	public String expectedResult;
	public static HttpServlet servlet;
	public static final String servletName = "UAClientHintsManualExample";
	public static final String urlPattern = "/pipeline.uachmanual.cloud.examples.servlet";

    @BeforeClass
    public static void setUpClass() throws Exception {
        servlet = new UAClientHintsManualExample();
        Map<String, String> initParams = new HashMap<>();
        initParams.put(RESOURCE_KEY_PARAMETER_NAME, SUPER_RESOURCEKEY);
        startTomcat(servletName, urlPattern, servlet, TEST_PORT_NUMBER, initParams);
    }

    @AfterClass
    public static void tearDownClass() {
        stopTomcat();
    }

    // Each parameter should be placed as an argument here
    // Every time runner triggers, it will pass the arguments
    // from parameters we defined in uachTestInput() method	
    public UACHCloudTests(String userAgent, String resourceKey, String expectedResult) {
       this.userAgent = userAgent;
       this.resourceKey = resourceKey;
       this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection<?> uachTestInput() {
        return Arrays.asList(new Object[][] {
            { CHROME_UA, SUPER_RESOURCEKEY, SUPER_ACCEPT_CH },
            //{ CHROME_UA, PLATFORM_RESOURCEKEY, PLATFORM_ACCEPT_CH },
            //{ CHROME_UA, HARDWARE_RESOURCEKEY, HARDWARE_ACCEPT_CH },
            //{ CHROME_UA, BROWSER_RESOURCEKEY, BROWSER_ACCEPT_CH },
            //{ CHROME_UA, NO_ACCEPTCH_KEY, null },
            //{ EDGE_UA, SUPER_RESOURCEKEY, SUPER_ACCEPT_CH },
            //{ EDGE_UA, PLATFORM_RESOURCEKEY, PLATFORM_ACCEPT_CH },
            //{ EDGE_UA, HARDWARE_RESOURCEKEY, HARDWARE_ACCEPT_CH },
            //{ EDGE_UA, BROWSER_RESOURCEKEY, BROWSER_ACCEPT_CH },
            //{ EDGE_UA, NO_ACCEPTCH_KEY, null },
            { FIREFOX_UA, SUPER_RESOURCEKEY, null },
            { FIREFOX_UA, PLATFORM_RESOURCEKEY, null },
            { FIREFOX_UA, HARDWARE_RESOURCEKEY, null },
            { FIREFOX_UA, BROWSER_RESOURCEKEY, null },
            { FIREFOX_UA, NO_ACCEPTCH_KEY, null },
            { SAFARI_UA, SUPER_RESOURCEKEY, null },
            { SAFARI_UA, PLATFORM_RESOURCEKEY, null },
            { SAFARI_UA, HARDWARE_RESOURCEKEY, null },
            { SAFARI_UA, BROWSER_RESOURCEKEY, null },
            { SAFARI_UA, NO_ACCEPTCH_KEY, null },          
            { CURL_UA, SUPER_RESOURCEKEY, null },
            { CURL_UA, PLATFORM_RESOURCEKEY, null },
            { CURL_UA, HARDWARE_RESOURCEKEY, null },
            { CURL_UA, BROWSER_RESOURCEKEY, null },
            { CURL_UA, NO_ACCEPTCH_KEY, null },         
         });
    }
    
    @Test
    public void UACH_Cloud_Test() throws Exception {
    	

    	
        HttpURLConnection connection = (HttpURLConnection) new URL(CLOUD_URL).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("user-agent", userAgent);
        connection.setRequestProperty("resourcekey", resourceKey);
        connection.connect();
        int code = connection.getResponseCode();
        String acceptCHHeader = connection.getHeaderField("Accept-CH");

        assertEquals("Request was unsuccessful.", 200, code);
        assertEquals("The values in Accept-CH does not match the expected values.", 
        		        expectedResult != null ? expectedResult.toLowerCase() : null,
        		        		acceptCHHeader != null ? acceptCHHeader.toLowerCase() : null);   
    }
}