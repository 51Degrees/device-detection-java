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

import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.devicedetection.shared.testhelpers.Utils;
import fiftyone.pipeline.cloudrequestengine.flowelements.CloudRequestEngine;
import fiftyone.pipeline.core.configuration.PipelineOptions;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.fiftyone.flowelements.FiftyOnePipelineBuilder;
import fiftyone.pipeline.web.services.UACHServiceCore;
import fiftyone.pipeline.web.services.WebRequestEvidenceServiceCore;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.apache.catalina.Context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static fiftyone.pipeline.util.StringManipulation.stringJoin;
import static fiftyone.pipeline.web.examples.shared.EmbedTomcat.startTomcat;
import static fiftyone.pipeline.web.examples.shared.EmbedTomcat.stopTomcat;


/**
 * @example servlet/UAClientHintsManualExample.java
 * <p>
 * Servlet User Agent Client Hints-Manual example
 * <p>
 * In this scenario, the standard Pipeline API web integration is not used.
 * This means that several jobs that the API usually takes care of automatically
 * must be handled manually. For example, setting the HTTP response headers to
 * request user-agent client hints.
 * <p>
 * The source code for this example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/tree/master/device-detection.web.examples/pipeline.uachmanual.examples.mvc).
 * <p>
 * This example can be configured to use the 51Degrees cloud service or a local
 * data file. If you don't already have data file you can obtain one from the
 * [device-detection-data](https://github.com/51Degrees/device-detection-data)
 * GitHub repository.
 * <p>
 * To use the cloud service you will need to create a **resource key**.
 * The resource key is used as short-hand to store the particular set of
 * properties you are interested in as well as any associated license keys
 * that entitle you to increased request limits and/or paid-for properties.
 * <p>
 * You can create a resource key using the 51Degrees [Configurator](https://configure.51degrees.com).
 * <p>
 * 1. Set up configuration options to add elements to the 51Degrees Pipeline.
 * ```{xml}
 * <PipelineOptions>
 * <Elements>
 * <Element>
 * <BuildParameters>
 * <EndPoint>https://cloud.51degrees.com/api/v4</EndPoint>
 * <!-- Obtain a resource key for free at
 * https://configure.51degrees.com
 * Make sure to include the 'BrowserName','BrowserVendor',
 * 'BrowserVersion','HardwareName','HardwareVendor',
 * 'PlatformName','PlatformVendor','PlatformVersion'
 * properties as they are used by this example. -->
 * <ResourceKey>!!YOUR_RESOURCE_KEY!!</ResourceKey>
 * </BuildParameters>
 * <BuilderName>CloudRequestEngine</BuilderName>
 * </Element>
 * <Element>
 * <BuilderName>DeviceDetectionCloudEngine</BuilderName>
 * </Element>
 * </Elements>
 * </PipelineOptions>
 * ```
 * <p>
 * ```
 * <p>
 * 2. Enable configuration, Add builders and the Pipeline to the Servlet.
 * <p>
 * ```{java}
 * <p>
 * // Create the configuration object from an XML file
 * ServletContext context = request.getSession().getServletContext();
 * File configFile = new File(context.getRealPath("/WEB-INF/51Degrees-Hash.xml"));
 * <p>
 * JAXBContext jaxbContext = JAXBContext.newInstance(PipelineOptions.class);
 * Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
 * PipelineOptions options = (PipelineOptions) unmarshaller.unmarshal(configFile);
 * <p>
 * // Build a new Pipeline from the configuration.
 * Pipeline pipeline = new FiftyOnePipelineBuilder()
 * .buildFromConfiguration(options);
 * ...
 * // Pipeline should be created once in 'init(...)' method of HttpServlet.
 * // Make pipeline an attribute to ServletContext for reusability.
 * config.getServletContext().setAttribute(PIPELINE, pipeline);
 * ```
 * <p>
 * <p>
 * 3. Process the request, set response headers and send the results to the view.
 * <p>
 * ```{java}
 * ...
 * // Create an instance of WebRequestEvidenceServiceCore and Set evidence
 * // from HTTP request headers to flowData
 * WebRequestEvidenceServiceCore evidenceService = new WebRequestEvidenceServiceCore.Default();
 * evidenceService.addEvidenceFromRequest(flowData, request);
 * flowData.process();
 * <p>
 * // Create an  instance of UACHServiceCore and Set UACH response headers.
 * UACHServiceCore uachServiceCore = new UACHServiceCore.Default();
 * uachServiceCore.setResponseHeaders(flowData, (HttpServletResponse)response);
 * ...
 * ```
 * <p>
 * 4. Display device details in the view.
 * <p>
 * ```{java}
 * ...
 * <strong>Detection results:</strong><br /><br />
 * Hardware Vendor:  ${hardwareVendor.hasValue() ? hardwareVendor.getValue() : "Unknown: ".concat(hardwareVendor.getNoValueMessage())}<br />
 * <p>
 * ...
 * ```
 */

/**
 * Servlet UAClientHintsManualExample.
 */
public class UAClientHintsManualExample extends HttpServlet {
    private static final long serialVersionUID = 1734154705981153541L;

    // this is the key for storing the pipeline in the servlet context
    public static final String PIPELINE = "fiftyone.pipeline.pipeline";
    // this is the key for the resource key for servlet init
    public static final String RESOURCE_KEY_PARAMETER_NAME = "fiftyone.init.resourcekey";

    public static void main(String[] args) throws Exception {
        System.setProperty("logback.configurationFile", "./logback.xml");

        // cloud access requires resource key
        if (args.length < 1) {
            System.err.println("Must supply a resource key as first argument");
            System.exit(1);
        }
        
        // create the servlet and run Tomcat
        HttpServlet servlet = new UAClientHintsManualExample();
        int port = 8080;
        Map<String, String> initParams = new HashMap<>();
        initParams.put(RESOURCE_KEY_PARAMETER_NAME, args[0]);
        startTomcat("servlet", "", servlet, port, initParams);

        // wait for the user to shut tomcat down
        System.out.format("Browse to http://localhost:%d using a 'private' window in your browser\n" +
        		"Hit enter to stop tomcat:", port);
        new Scanner(System.in).nextLine();
        stopTomcat();
    }

    public static Pipeline createPipeline(String resourceKey) throws Exception {
        // Create the configuration object from an XML file
        // this could equally be done using the DeviceDetectionPipelineBuilder
        File configFile = Utils.getFilePath("WEB-INF/UACHManualExampleCloud.xml");

        JAXBContext jaxbContext = JAXBContext.newInstance(PipelineOptions.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        // Bind the configuration to a pipeline options instance
        PipelineOptions options = (PipelineOptions) unmarshaller.unmarshal(configFile);
        // Override resource key from file (where the value is a placeholder).
        options.elements.get(0).buildParameters.replace("ResourceKey", resourceKey);

        // Build a new Pipeline from the configuration.
        return new FiftyOnePipelineBuilder().buildFromConfiguration(options);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String resourceKey = config.getInitParameter(RESOURCE_KEY_PARAMETER_NAME);
        try {
            Pipeline pipeline = createPipeline(resourceKey);
            config.getServletContext().setAttribute(PIPELINE, pipeline);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws Exception if a servlet-specific error or an I/O error occurs
     *                   or if a value not available
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // retrieve previously created pipeline from context
        Pipeline pipeline = (Pipeline) this.getServletContext().getAttribute(PIPELINE);
        // A try-with-resource block MUST be used for the FlowData instance.
        // This ensures that native resources created by the device
        // detection engine are freed.
        try (FlowData flowData = pipeline.createFlowData()) {

            // Create an instance of WebRequestEvidenceServiceCore
            WebRequestEvidenceServiceCore evidenceService = new WebRequestEvidenceServiceCore.Default();

            // Set evidence from HTTP request headers to flowData
            evidenceService.addEvidenceFromRequest(flowData, request);

            // Start processing the data
            flowData.process();

            // Get the device data from flowData
            DeviceData device = flowData.get(DeviceData.class);

            // Create an  instance of UACHServiceCore
            UACHServiceCore uachServiceCore = new UACHServiceCore.Default();

            // Set UACH response headers.
            uachServiceCore.setResponseHeaders(flowData, response);

            response.setContentType("text/html;charset=UTF-8");

            try (PrintWriter out = response.getWriter()) {
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>User-Agent Client Hints Example</title>");
                out.println("</head>");
                out.println("<body>");

                AspectPropertyValue<String> hardwareVendor = device.getHardwareVendor();
                AspectPropertyValue<List<String>> hardwareName = device.getHardwareName();
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

                CloudRequestEngine engine = flowData.getPipeline().getElement(CloudRequestEngine.class);
                for (Map.Entry<String, Object> evidence : flowData.getEvidence().asKeyMap().entrySet()) {
                    if (engine.getEvidenceKeyFilter().include(evidence.getKey())) {
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
                        "        Hardware Name: " + (hardwareName.hasValue() ? stringJoin(hardwareName.getValue(), ",") : "Unknown " + hardwareName.getNoValueMessage()) + "<br />\n" +
                        "        Device Type: " + (deviceType.hasValue() ? deviceType.getValue() : "Unknown " + deviceType.getNoValueMessage()) + "<br />\n" +
                        "        Platform Vendor: " + (platformVendor.hasValue() ? platformVendor.getValue() : "Unknown " + platformVendor.getNoValueMessage()) + "<br />\n" +
                        "        Platform Name: " + (platformName.hasValue() ? platformName.getValue() : "Unknown " + platformName.getNoValueMessage()) + "<br />\n" +
                        "        Platform Version: " + (platformVersion.hasValue() ? platformVersion.getValue() : "Unknown " + platformVersion.getNoValueMessage()) + "<br />\n" +
                        "        Browser Vendor: " + (browserVendor.hasValue() ? browserVendor.getValue() : "Unknown " + browserVendor.getNoValueMessage()) + "<br />\n" +
                        "        Browser Name: " + (browserName.hasValue() ? browserName.getValue() : "Unknown " + browserName.getNoValueMessage()) + "<br />\n" +
                        "        Browser Version: " + (browserVersion.hasValue() ? browserVersion.getValue() : "Unknown " + browserVersion.getNoValueMessage()) + "\n" + "    </p>\n" +
                        "</div>\n" +
                        "\n");
            }

        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        // any failure causes 500 error
        try {
            processRequest(request, response);
            response.setStatus(200);
        } catch (ServletException se) {
            response.setStatus(500);
            throw se;
        } catch (Exception e) {
            response.setStatus(500);
            e.printStackTrace();
        }
    }
}
