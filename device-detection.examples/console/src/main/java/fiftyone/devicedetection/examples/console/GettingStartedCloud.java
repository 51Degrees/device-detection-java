/*
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2022 51 Degrees Mobile Experts Limited, Davidson House,
 * Forbury Square, Reading, Berkshire, United Kingdom RG1 3EU.
 *
 * This Original Work is licensed under the European Union Public Licence
 *  (EUPL) v.1.2 and is subject to its terms as set out below.
 *
 *  If a copy of the EUPL was not distributed with this file, You can obtain
 *  one at https://opensource.org/licenses/EUPL-1.2.
 *
 *  The 'Compatible Licences' set out in the Appendix to the EUPL (as may be
 *  amended by the European Commission) shall be deemed incompatible for
 *  the purposes of the Work and the provisions of the compatibility
 *  clause in Article 5 of the EUPL shall not apply.
 *
 *   If using the Work as, or as part of, a network application, by
 *   including the attribution notice(s) required under Article 5 of the EUPL
 *   in the end user terms of the application under an appropriate heading,
 *   such notice(s) shall fulfill the requirements of that article.
 */

package fiftyone.devicedetection.examples.console;

import fiftyone.devicedetection.DeviceDetectionPipelineBuilder;
import fiftyone.devicedetection.examples.shared.KeyHelper;
import fiftyone.devicedetection.examples.shared.EvidenceHelper;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import static fiftyone.common.testhelpers.LogbackHelper.configureLogback;
import static fiftyone.devicedetection.examples.shared.PropertyHelper.asString;
import static fiftyone.pipeline.util.FileFinder.getFilePath;

/**
 * Provides an illustration of the fundamental elements of carrying out device detection using
 * our "cloud" service. Meaning that you don't host the datafile on your server, but request
 * detection to be carried out on our servers.
 * <p>
 * In order to use the cloud service you will need to obtain a "Resource Key". A free resource key
 * configured with the properties required by this example may be obtained from
 * <a href="https://configure.51degrees.com/jqz435Nc">https://configure.51degrees.com/jqz435Nc</a>
 * <p>
 * The concepts of "pipeline", "flow data", "evidence" and "results" are illustrated.
 */
@SuppressWarnings("DuplicatedCode")
public class GettingStartedCloud {
    private static final Logger logger = LoggerFactory.getLogger(GettingStartedCloud.class);

    /**
     * The resource key can be supplied as an argument to this program or as an environment
     * variable or as a Java system property called "TestResourceKey".
     */
    public static void main(String[] args) throws Exception {
        configureLogback(getFilePath("logback.xml"));
        String resourceKey = args.length > 0 ? args[0]: null;

        // prepare 'evidence' for use in pipeline (see below)
        List<Map<String, String>> evidence = EvidenceHelper.setUpEvidence();
        run(resourceKey, evidence, System.out);
    }

    /**
     * Run the example
     * @param resourceKey a 51Degrees "resource key"
     * @param evidenceList a List&lt;Map&lt;String, String>> representing evidence
     * @param outputStream somewhere for the results
     */
    public static void run(String resourceKey,
                           List<Map<String, String>> evidenceList,
                           OutputStream outputStream) throws Exception {
        logger.info("Running GettingStarted Cloud example");

        resourceKey = KeyHelper.getOrSetTestResourceKey(resourceKey);

        /* In this example, we use the DeviceDetectionPipelineBuilder and configure it in code.

        For more information about pipelines in general see the documentation at
        http://51degrees.com/documentation/_concepts__configuration__builders__index.html

        Note that we wrap the creation of a pipeline in a try/resources to control its lifecycle */
        try (Pipeline pipeline = new DeviceDetectionPipelineBuilder()
                .useCloud(resourceKey)
                .build()) {


            // carry out some sample detections
            for (Map<String, String> evidence : evidenceList) {
                analyzeEvidence(evidence, pipeline, outputStream);
            }

            logger.info("All done");
        }
    }

    /**
     * Taking a map of evidence as a parameter, process it in the pipeline
     * supplied and output the device detection results to the output stream.
     * @param evidence a map representing HTTP headers
     * @param pipeline a pipleine set up to process the evidence
     * @param out somewhere to send the detection results
     */
    private static void analyzeEvidence(Map<String, String> evidence,
                                        Pipeline pipeline,
                                        OutputStream out) throws Exception {
        PrintWriter writer = new PrintWriter(out);
        /* FlowData is a data structure that is used to convey information required for detection
        and the results of the detection through the pipeline. Information required for
        detection is called "evidence" and usually consists of a number of HTTP Header field
        values, in this case represented by a Map<String, String> of header name/value entries.

        FlowData is wrapped in a try/resources block in order to ensure that the unmanaged
        resources allocated by the native device detection library are freed */
        try (FlowData data = pipeline.createFlowData()) {

            // list the evidence
            writer.println("Input values:");
            for (Map.Entry<String, String> entry : evidence.entrySet()) {
                writer.format("\t%s: %s\n", entry.getKey(), entry.getValue());
            }

            // Add the evidence values to the flow data
            data.addEvidence(evidence);

            // Process the flow data.
            data.process();

            writer.println("Results:");
            /* Now that it has been processed, the flow data will have been populated with the result.

            In this case, we want information about the device, which we can get by asking for a
            result matching the "DeviceData" interface. */
            DeviceData device = data.get(DeviceData.class);

            /* Display the results of the detection, which are called device properties. See the
            property dictionary at https://51degrees.com/developers/property-dictionary for
            details of all available properties. */
            writer.println("\tMobile Device: " + asString(device.getIsMobile()));
            writer.println("\tPlatform Name: " + asString(device.getPlatformName()));
            writer.println("\tPlatform Version: " + asString(device.getPlatformVersion()));
            writer.println("\tBrowser Name: " + asString(device.getBrowserName()));
            writer.println("\tBrowser Version: " + asString(device.getBrowserVersion()));
        }
        writer.println();
        writer.flush();
    }
}
/*!
 * @example console/GettingStartedCloud.java
 *
 * @include{doc} example-getting-started-cloud.txt
 *
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/blob/master/device-detection.examples/console/src/main/java/fiftyone/devicedetection/examples/console/GettingStartedCloud.java).
 *
 * @include{doc} example-require-resourcekey.txt
 */

