package fiftyone.devicedetection.examples.web;

import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.web.Constants;
import fiftyone.pipeline.web.services.FlowDataProviderCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static fiftyone.common.testhelpers.LogbackHelper.configureLogback;
import static fiftyone.devicedetection.examples.web.HtmlContentHelper.*;
import static fiftyone.devicedetection.shared.testhelpers.FileUtils.getFilePath;

/**
 * @example GettingStartedWebCloud.java
 * 
 * @include{doc} example-getting-started-web.txt
 * 
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/blob/master/device-detection.examples/web/getting-started.cloud/src/main/java/fiftyone/devicedetection/examples/web/GettingStartedWebCloud.java).
 * 
 * @include{doc} example-require-resourcekey.txt
 * 
 * ## Overview
 * 
 * The `PipelineFilter` to intercept requests and perform device detection. The results will be 
 * stored in the HttpServletRequest object.
 * The filter will also handle setting response headers (e.g. Accept-CH for User-Agent 
 * Client Hints) and serving requests for client-side JavaScript and JSON resources.
 * 
 * The results of detection can be accessed by using a FlowDataProvider.
 * ```{java}
 * FlowData flowData = flowDataProvider.getFlowData(request);
 * DeviceData device = flowData.get(DeviceData.class);
 * ```
 * 
 * Results can also be accessed in client-side code by using the `fod` object.
 * 
 * ```{java}
 * window.onload = function () {
 *     fod.complete(function(data) {
 *         var hardwareName = data.device.hardwarename;
 *         alert(hardwareName.join(", "));
 *     }
 * }
 * ```
 * 
 */

/**
 * This is the getting started Web/Cloud example showing use of the 51Degrees
 * supplied filter which automatically creates and configures a device detection pipeline.
 * <p>
 * To use this example you must obtain a resource key. A resource key suitable for
 * use with the example can be obtained here: https://configure.51degrees.com/jqz435Nc.
 * <p>
 * The configuration file for the pipeline is at src/main/webapp/WEB-INF/51Degrees-Cloud.xml
 */
public class GettingStartedWebCloud extends HttpServlet {
    private static final long serialVersionUID = 1734154705981153540L;
    public static String resourceBase = "device-detection.examples/web/getting-started.cloud/src/main/webapp";
    public static Logger logger = LoggerFactory.getLogger(GettingStartedWebCloud.class);

    public static void main(String[] args) throws Exception {
        configureLogback(getFilePath("logback.xml"));
        logger.info("Running Example {}", GettingStartedWebCloud.class);
        // if the config file doesn't contain a valid resource key we will try to find
        // one in args or in environment or system variables
        ExampleHelper.findResourceKey(resourceBase + "/WEB-INF/51Degrees-Cloud.xml", args);
        // start Jetty with this WebApp
        EmbedJetty.runWebApp(resourceBase, 8081);
    }

     FlowDataProviderCore flowDataProvider = new FlowDataProviderCore.Default();

    /**
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws Exception when things go wrong
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // the detection has already been carried out by the Filter
        FlowData flowData = flowDataProvider.getFlowData(request);
        // retrieve the device data from the flowdata
        DeviceData device = flowData.get(DeviceData.class);

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            doHtmlPreamble(out, "Web Integration Cloud Example");

            // request main 51Degrees Client Side Script - this is automatically
            // served by inclusion of the PipelineFilter which intercepts the request
            // and serves dynamically generated JavaScript
            out.println("<script src=\"" + Constants.CORE_JS_NAME+ "\"></script>");

            // include description of example
            doStaticText(out, resourceBase + "/WEB-INF/html/example-description.html");

            // include a script to display the results of the client side detection
            doStaticText(out, resourceBase + "/WEB-INF/html/client-side-js-include.html");
            doDeviceData(out, device, flowData, null);
            doStaticText(out, resourceBase + "/WEB-INF/html/apple-detection.html");
            doEvidence(out, request, flowData);
            doResponseHeaders(out, response);
            doHtmlPostamble(out);
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        // any failure causes 500 error
        try {
            processRequest(request, response);
            response.setStatus(200);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }
}
