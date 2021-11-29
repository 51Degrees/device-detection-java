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

package fiftyone.pipeline.uach.examples.servlet;

import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.cloudrequestengine.flowelements.CloudRequestEngine;
import fiftyone.pipeline.core.data.EvidenceKeyFilter;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.web.services.FlowDataProviderCore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import static fiftyone.pipeline.util.StringManipulation.stringJoin;


/**
 * @example servlet/UAClientHintsExample.java
 * Servlet device detection example.
 * 
 * @include{doc} example-web-integration-client-hints.txt
 * 
 * In this scenario, the standard Pipeline API web integration is used.
 * This means that several jobs that the API usually takes care of automatically
 * For example, setting the HTTP response headers to request user-agent client hints.
 *
 * The source code for this example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/tree/master/web/pipeline.uach.examples.servlet).
 * 
 * This example can be configured to use the 51Degrees cloud service or a local 
 * data file. If you don't already have data file you can obtain one from the 
 * [device-detection-data](https://github.com/51Degrees/device-detection-data) 
 * GitHub repository.
 * 
 * To use the cloud service you will need to create a **resource key**. 
 * The resource key is used as short-hand to store the particular set of 
 * properties you are interested in as well as any associated license keys 
 * that entitle you to increased request limits and/or paid-for properties.
 * 
 * You can create a resource key using the 51Degrees [Configurator](https://configure.51degrees.com).
 *
 * 1. Set up configuration options to add elements to the 51Degrees Pipeline.
 * ```{xml}
 * <PipelineOptions>
 *     <Elements>
 *         <Element>
 *             <BuildParameters>
 *                 <EndPoint>https://cloud.51degrees.com/api/v4</EndPoint>
 *                  <!-- Obtain a resource key for free at
 *                  https://configure.51degrees.com
 *                  Make sure to include the 'BrowserName','BrowserVendor',
 *                  'BrowserVersion','HardwareName','HardeareVendor',
 *                  'PlatformName','PlatformVendor','PlatformVersion'
 *                  properties as they are used by this example. -->
 *                  <ResourceKey>!!YOUR_RESOURCE_KEY!!</ResourceKey>
 *             </BuildParameters>
 *             <BuilderName>CloudRequestEngine</BuilderName>
 *         </Element>
 *         <Element>
 *         <BuilderName>DeviceDetectionCloudEngine</BuilderName>
 *         </Element>
 *     </Elements>
 * </PipelineOptions>
 * ```
 *
 * Alternatively, to use the on-premise API with automatic updates enabled,
 * replace the cloud element in the config with the new configuration.
 * ```{xml}
 * <PipelineOptions>
 *     <Elements>
 *         <Element>
 *             <BuildParameters>
 *                 <AutoUpdate>true</AutoUpdate>
 *                 <DataFileSystemWatcher>false</DataFileSystemWatcher>SS
 *                 <CreateTempDataCopy>true</CreateTempDataCopy>
 *                 <!-- Obtain your own license key and enterprise data file
 *                 from https://51degrees.com. -->
 *                 <DataUpdateLicenseKey>[[Your License Key]]</DataUpdateLicenseKey>
 *                 <DataFile>D:\[[Path to data file]]\51Degrees-EnterpriseV4.1.hash</DataFile>
 *                 <PerformanceProfile>LowMemory</PerformanceProfile>
 *             </BuildParameters>
 *             <BuilderName>DeviceDetectionHashEngineBuilder</BuilderName>
           </Element>
 *     </Elements>
 * </PipelineOptions>
 * ```
 *
 * 2. Configure the filter and map it to be run for all URLs.
 * ```{xml}
 * <web-app version="3.1" xmlns="http://xmlns.jcp.org/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd">
 *     ...
 *     <filter>
 *         <filter-name>Pipeline</filter-name>
 *         <filter-class>fiftyone.pipeline.web.PipelineFilter</filter-class>
 *         <init-param>
 *             <param-name>clientside-properties-enabled</param-name>
 *             <param-value>true</param-value>
 *         </init-param>
 *     </filter>
 *     <filter-mapping>
 *         <filter-name>Pipeline</filter-name>
 *         <url-pattern>/*</url-pattern>
 *     </filter-mapping>
 *     ...
 * ```
 *
 * 3. Add the `FlowDataProvider` to the servlet.
 * ```{java}
 * public class UAClientHintsExample extends HttpServlet {
 *     FlowDataProviderCore flowDataProvider = new FlowDataProviderCore.Default();
 * ...
 * ```
 *
 * 4. User the results contained in the flow data to display something on a page, and
 * optionally add the client-side code to improve detection accuracy on devices like iPhones.
 * {@code
 * public class UAClientHintsExample extends HttpServlet {
 *     ...
 *     protected void processRequest(HttpServletRequest request, HttpServletResponse response)
 *         throws Exception {
 *         FlowData data = flowDataProvider.getFlowData(request);
 *         DeviceData device = data.get(DeviceData.class);
 *         response.setContentType("text/html;charset=UTF-8");
 *         try (PrintWriter out = response.getWriter()) {
 *             out.println("<!DOCTYPE html>");
 *             out.println("<html>");
 *             out.println("<head>");
 *             out.println("<title>Servlet Example</title>");
 *             out.println("</head>");
 *             out.println("<body>");
 *             out.println("<script src=\"/pipeline.web.examples.servlet/51Degrees.core.js\"></script>");
 *             out.println("<p>Browser : " +
 *                 device.getBrowserVendor() + " " + device.getBrowserName() + " " + device.getBrowserVersion() +
 *                 "</p>");
 *             out.println("<p>Device : " +
 *                 device.getHardwareVendor() + " " + device.getHardwareName() +
 *                 "</p>");
 *             out.println("<p>OS : " +
 *                 device.getPlatformVendor() + " " + device.getPlatformName() + " " + device.getPlatformVersion() +
 *                 "</p>");
 *             out.println("</body>");
 *             out.println("</html>");
 *         }
 *     }
 *     ...
 * }
 * }
 *
 * ## Servlet
 */

/**
 * Servlet UAClientHintsExample.
 */
public class UAClientHintsExample extends HttpServlet {

    /**
     * Serializable class version number, which is used during deserialization.
     */
    private static final long serialVersionUID = 1734154705981153540L;
    FlowDataProviderCore flowDataProvider = new FlowDataProviderCore.Default();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws Exception if a servlet-specific error or an I/O error occurs
     * or if a value not available
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
    	FlowData data = flowDataProvider.getFlowData(request);
        DeviceData device = data.get(DeviceData.class);
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
        	out.println("<!DOCTYPE html>");
        	out.println("<html>");
        	out.println("<head>");
        	out.println("<title>User-Agent Client Hints Example</title>");
        	out.println("</head>");
        	out.println("<body>");

        	AspectPropertyValue<String> hardwareVendor = device.getHardwareVendor();
        	AspectPropertyValue<List<String>> hardwareName =  device.getHardwareName();
        	AspectPropertyValue<String> deviceType = device.getDeviceType();
        	AspectPropertyValue<String> platformVendor = device.getPlatformVendor();
        	AspectPropertyValue<String> platformName = device.getPlatformName();
        	AspectPropertyValue<String> platformVersion = device.getPlatformVersion();
        	AspectPropertyValue<String> browserVendor = device.getBrowserVendor();
        	AspectPropertyValue<String> browserName = device.getBrowserName();
        	AspectPropertyValue<String> browserVersion = device.getBrowserVersion();
        	
        	out.println("<h2>User Agent Client Hints Example</h2>\n" +
        	"\n" +
            
			"    <p>" +
			"    By default, the user-agent, sec-ch-ua and sec-ch-ua-mobile HTTP headers" +
			"    are sent." +
			"    <br />" +
			"    This means that on the first request, the server can determine the" +
			"    browser from sec-ch-ua while other details must be derived from the" +
			"    user-agent." +
			"    <br />" +
			"    If the server determines that the browser supports client hints, then" +
			"    it may request additional client hints headers by setting the" +
			"    Accept-CH header in the response." +
			"    <br />" +
			"    Select the <strong>Make second request</strong> button below," +
			"    to use send another request to the server. This time, any" +
			"    additional client hints headers that have been requested" +
			"    will be included." +
			"    </p>" +
			"    \n" +
			
			"    <button type=\"button\" onclick=\"redirect()\">Make second request</button>\n" +

			"    <script>\n" +
			
			"        // This script will run when button will be clicked and device detection request will again\n" + 
			"        // be sent to the server with all additional client hints that was requested in the previous\n" +
			"        // response by the server.\n" +
			"        // Following sequence will be followed.\n" +
			"        // 1. User will send the first request to the web server for detection.\n" +
			"        // 2. Web Server will return the properties in response based on the headers sent in the request. Along \n" +
			"        // with the properties, it will also send a new header field Accept-CH in response indicating the additional\n" +
			"        // evidence it needs. It builds the new response header using SetHeader[Component name]Accept-CH properties\n" + 
			"        // where Component Name is the name of the component for which properties are required.\n" +
			"        // 3. When \"Make second request\" button will be clicked, device detection request will again\n" + 
			"        // be sent to the server with all additional client hints that was requested in the previous\n" +
			"        // response by the server.\n" +
			"        // 4. Web Server will return the properties based on the new User Agent Client Hint headers\n" + 
			"        // being used as evidence.\n" +
			
			"        function redirect() {\n" +
			"            sessionStorage.reloadAfterPageLoad = true;\n" +
			"            window.location.reload(true);\n" +
			"            }\n" +
			
			"        window.onload = function () {\n" + 
			"            if ( sessionStorage.reloadAfterPageLoad ) {\n" +
			"            document.getElementById('description').innerHTML = `</br>The information shown below is determined using <strong>User Agent \n" + 
			"            Client Hints</strong> that was sent in the request to obtain additional evidence. If no additional information appears \n" + 
			"            then it may indicate an external problem such as <strong>User Agent Client Hints</strong> being disabled in your browser.`;\n" +
			"           sessionStorage.reloadAfterPageLoad = false;\n" +
			"            }\n" +
			"            else{\n" +
			"            document.getElementById('description').innerHTML = \"<p>The following values are determined by " +
			"            sever-side device detection on the first request.</p>\";\n" +
			"            }\n" +
			"        }\n" +
			"  \n" +
			"  </script>\n" +
			
			"  <div id=\"evidence\">\n" +
			"  <strong></br>Evidence values used: </strong>\n" +
			"  <table>\n" +
			"     <tr>\n" +
			"          <th>Key</th>\n" +
			"         <th>Value</th>\n" +
			"     </tr>\n");

        	// Get evidence based on the device detection engine being used.
        	Object engine = null;
        	EvidenceKeyFilter evidenceKeyfilter = null;
        	if(data.getPipeline().getElement(DeviceDetectionHashEngine.class) != null) {
        		engine = (DeviceDetectionHashEngine) data.getPipeline().getElement(DeviceDetectionHashEngine.class);
        		evidenceKeyfilter = ((DeviceDetectionHashEngine) engine).getEvidenceKeyFilter();
        	}
        	else {
       		 engine = (CloudRequestEngine) data.getPipeline().getElement(CloudRequestEngine.class);
       		 evidenceKeyfilter = ((CloudRequestEngine) engine).getEvidenceKeyFilter();
       	    }       	

        	for (Map.Entry<String, Object> evidence : data.getEvidence().asKeyMap().entrySet()) {
        		if(evidenceKeyfilter.include(evidence.getKey())) {
        			out.println("<tr>");
        			out.println("<td>" + evidence.getKey() + "</td>");
        		    out.println("<td>" + evidence.getValue() + "</td>");
        		    out.println("</>");
        		}
        	}
        	
        	out.println("</table>\n" +
            "</div>\n" +
			"<div id=description></div>\n" +
           	"<div id=\"content\">\n" +
            "</br><strong>Detection results:</strong></br>\n" +
           	"    <p>\n" +
           	"        Hardware Vendor: " + (hardwareVendor.hasValue() ? hardwareVendor.getValue() : "Unknown " + hardwareVendor.getNoValueMessage()) + "<br />\n" +
           	"        Hardware Name: " + (hardwareName.hasValue() ? stringJoin(hardwareName.getValue(), ",") : "Unknown " + hardwareName.getNoValueMessage()) +"<br />\n" +
           	"        Device Type: " + (deviceType.hasValue() ? deviceType.getValue() : "Unknown " + deviceType.getNoValueMessage()) + "<br />\n" +
           	"        Platform Vendor: " + (platformVendor.hasValue() ? platformVendor.getValue() : "Unknown " + platformVendor.getNoValueMessage()) + "<br />\n" +
           	"        Platform Name: " + (platformName.hasValue() ? platformName.getValue() : "Unknown " + platformName.getNoValueMessage()) + "<br />\n" +
           	"        Platform Version: " + (platformVersion.hasValue() ? platformVersion.getValue() : "Unknown " + platformVersion.getNoValueMessage()) + "<br />\n" +
           	"        Browser Vendor: " + (browserVendor.hasValue() ? browserVendor.getValue() : "Unknown " + browserVendor.getNoValueMessage()) + "<br />\n" +
           	"        Browser Name: " + (browserName.hasValue() ? browserName.getValue() : "Unknown " + browserName.getNoValueMessage()) + "<br />\n" +
           	"        Browser Version: " + (browserVersion.hasValue() ? browserVersion.getValue() : "Unknown " + browserVersion.getNoValueMessage()) + "\n" +            	"    </p>\n" +
           	"</div>\n" +
        	"\n");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    		throws ServletException {
        try {
            processRequest(request, response);
        } catch (ServletException se) {
            throw se;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
