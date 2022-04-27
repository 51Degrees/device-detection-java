package fiftyone.devicedetection.examples.web;

import fiftyone.devicedetection.examples.shared.OptionsHelper;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.web.Constants;
import fiftyone.pipeline.web.services.FlowDataProviderCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static fiftyone.common.testhelpers.LogbackHelper.configureLogback;
import static fiftyone.devicedetection.examples.web.HtmlContentHelper.*;
import static fiftyone.devicedetection.shared.testhelpers.FileUtils.getFilePath;

/*
 * @example GettingStartedWebOnPrem.java
 * 
 * @include{doc} example-getting-started-web.txt
 * 
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/blob/master/device-detection.examples/web/getting-started/onprem/src/main/java/fiftyone/devicedetection/examples/web/GettingStartedWebOnPrem.java).
 *  
 * @include{doc} example-require-datafile.txt
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
 * Results can also be accessed in client-side code by using the `fod` object. Note that the global 
 * object name can be changed by using the setObjectName option on the 
 * [JavaScriptBuilderElementBuilder](https://51degrees.com/pipeline-java/4.3/classfiftyone_1_1pipeline_1_1javascriptbuilder_1_1flowelements_1_1_java_script_builder_element_builder.html)
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
 * This is the getting started Web/On-Prem example showing use of the 51Degrees
 * supplied filter which automatically creates and configures a device detection pipeline.
 * <p>
 * The configuration file for the pipeline is at src/main/webapp/WEB-INF/51Degrees-OnPrem.xml
 */
public class GettingStartedWebOnPrem extends HttpServlet {
    private static final long serialVersionUID = 1734154705981153540L;
    public static String resourceBase = "device-detection.examples/web/getting-started" +
            ".onprem/src/main/webapp";
    public static Logger logger = LoggerFactory.getLogger(GettingStartedWebOnPrem.class);

    public static void main(String[] args) throws Exception {
        configureLogback(getFilePath("logback.xml"));
        logger.info("Running Example {}", GettingStartedWebOnPrem.class);

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
            doHtmlPreamble(out, "Web Integration On-Premise Example");

            // request main 51Degrees Client Side Script - this is automatically
            // served by inclusion of the PipelineFilter which intercepts the request
            // and serves dynamically generated JavaScript
            out.println("<script src=\"" + Constants.CORE_JS_NAME+ "\"></script>");

            // include description of example
            doStaticText(out, resourceBase + "/WEB-INF/html/example-description.html");

            // include a script to display the results of the client side detection
            doStaticText(out, resourceBase + "/WEB-INF/html/client-side-js-include.html");
            // find out where our data file is from the configuration
            OptionsHelper oh = new OptionsHelper(resourceBase + "/WEB-INF/51Degrees-OnPrem.xml");
            doDeviceData(out, device, flowData, oh.find("DeviceDetectionHashEngine", "DataFile"));

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
