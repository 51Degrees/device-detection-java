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

package fiftyone.pipeline.web.examples.servlet;

import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.exceptions.NoValueException;
import fiftyone.pipeline.jsonbuilder.data.JsonBuilderData;
import fiftyone.pipeline.web.services.FlowDataProviderCore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static fiftyone.pipeline.util.StringManipulation.stringJoin;


/**
 * @example servlet/Example.java
 * Servlet device detection example
 *
 * This example shows how to:
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
 *         <Element>
 *             <BuilderName>JavaScriptBuilderElement</BuilderName>
 *             <BuildParameters>
 *                 <Endpoint>/pipeline.web.examples.mvc/json</Endpoint>
 *                 <Protocol>http</Protocol>
 *             </BuildParameters>
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
 *         </Element>
 *         <Element>
 *             <BuilderName>JavaScriptBuilderElement</BuilderName>
 *             <BuildParameters>
 *                 <Endpoint>/pipeline.web.examples.servlet/json</Endpoint>
 *                 <Protocol>http</Protocol>
 *             </BuildParameters>
 *         </Element>
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
 * public class Example extends HttpServlet {
 *     FlowDataProviderCore flowDataProvider = new FlowDataProviderCore.Default();
 * ...
 * ```
 *
 * 4. User the results contained in the flow data to display something on a page, and
 * optionally add the client-side code to improve detection accuracy on devices like iPhones.
 * ```{java}
 * public class Example extends HttpServlet {
 *     ...
 *     protected void processRequest(HttpServletRequest request, HttpServletResponse response)
 *         throws ServletException, IOException {
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
 * ```
 *
 * ## Servlet
 */
public class Example extends HttpServlet {

    FlowDataProviderCore flowDataProvider = new FlowDataProviderCore.Default();

    private void processPost(
        HttpServletRequest request,
        HttpServletResponse response) throws IOException {
        FlowData data = flowDataProvider.getFlowData(request);

        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            out.print(data.get(JsonBuilderData.class).getJson());
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, NoValueException {
        FlowData data = flowDataProvider.getFlowData(request);
        DeviceData device = data.get(DeviceData.class);
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Example</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<script src=\"/pipeline.web.examples.servlet/51Degrees.core.js\"></script>");

            AspectPropertyValue<String> hardwareVendor = device.getHardwareVendor();
            AspectPropertyValue<List<String>> hardwareName = device.getHardwareName();
            AspectPropertyValue<String> deviceType = device.getDeviceType();
            AspectPropertyValue<String> platformVendor = device.getPlatformVendor();
            AspectPropertyValue<String> platformName = device.getPlatformName();
            AspectPropertyValue<String> platformVersion = device.getPlatformVersion();
            AspectPropertyValue<String> browserVendor = device.getBrowserVendor();
            AspectPropertyValue<String> browserName = device.getBrowserName();
            AspectPropertyValue<String> browserVersion = device.getBrowserVersion();


            out.println("<h2>Example</h2>\n" +
            "\n" +
            "<div id=\"content\">\n" +
            "    <p>\n" +
            "        Hardware Vendor: " + (hardwareVendor.hasValue() ? hardwareVendor.getValue() : "Unknown " + hardwareVendor.getNoValueMessage()) + "<br />\n" +
            "        Hardware Name: " + (hardwareName.hasValue() ? stringJoin(hardwareName.getValue(), ",") : "Unknown " + hardwareName.getNoValueMessage()) +"<br />\n" +
            "        Device Type: " + (deviceType.hasValue() ? deviceType.getValue() : "Unknown " + deviceType.getNoValueMessage()) + "<br />\n" +
            "        Platform Vendor: " + (platformVendor.hasValue() ? platformVendor.getValue() : "Unknown " + platformVendor.getNoValueMessage()) + "<br />\n" +
            "        Platform Name: " + (platformName.hasValue() ? platformName.getValue() : "Unknown " + platformName.getNoValueMessage()) + "<br />\n" +
            "        Platform Version: " + (platformVersion.hasValue() ? platformVersion.getValue() : "Unknown " + platformVersion.getNoValueMessage()) + "<br />\n" +
            "        Browser Vendor: " + (browserVendor.hasValue() ? browserVendor.getValue() : "Unknown " + browserVendor.getNoValueMessage()) + "<br />\n" +
            "        Browser Name: " + (browserName.hasValue() ? browserName.getValue() : "Unknown " + browserName.getNoValueMessage()) + "<br />\n" +
            "        Browser Version: " + (browserVersion.hasValue() ? browserVersion.getValue() : "Unknown " + browserVersion.getNoValueMessage()) + "\n" +
            "    </p>\n" +
            "</div>\n" +
            "\n" +
            "<script>\n" +
            "    // This function will fire when the JSON data object is updated \n" +
            "    // with information from the server.\n" +
            "    // The sequence is:\n" +
            "    // 1. Response contains JavaScript property 'getLatitude' that gets executed on the client\n" +
            "    // 2. This triggers another call to the webserver that passes the location as evidence\n" +
            "    // 3. The web server responds with new JSON data that contains the hemisphere based on the location.\n" +
            "    // 4. The JavaScript integrates the new JSON data and fires the onChange callback below.\n" +
            "    window.onload = function () {\n" +
            "        fod.complete(function (data) {\n" +
            "            var para = document.createElement(\"p\");\n" +
            "            var br = document.createElement(\"br\");\n" +
            "            var text = document.createTextNode(\"Updated information from client-side evidence:\");\n" +
            "            para.appendChild(text);\n" +
            "            para.appendChild(br);\n" +
            "            text = document.createTextNode(\"Hardware Name: \" + data.device.hardwarename.join(\",\"));\n" +
            "            br = document.createElement(\"br\");\n" +
            "            para.appendChild(text);\n" +
            "            para.appendChild(br);\n" +
            "            text = document.createTextNode(\"Screen width (pixels): \" + data.device.screenpixelswidth);\n" +
            "            br = document.createElement(\"br\");\n" +
            "            para.appendChild(text);\n" +
            "            para.appendChild(br);\n" +
            "            text = document.createTextNode(\"Screen height (pixels): \" + data.device.screenpixelsheight);\n" +
            "            br = document.createElement(\"br\");\n" +
            "            para.appendChild(text);\n" +
            "            para.appendChild(br);\n" +
            "\n" +
            "            var element = document.getElementById(\"content\");\n" +
            "            element.appendChild(para);\n" +
            "        });\n" +
            "    }\n" +
            "</script>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (NoValueException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processPost(request, response);
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
