package fiftyone.devicedetection.examples.console;

import fiftyone.devicedetection.DeviceDetectionPipelineBuilder;
import fiftyone.devicedetection.examples.console.helper.ExampleHelper;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static fiftyone.common.testhelpers.LogbackHelper.configureLogback;
import static fiftyone.devicedetection.examples.console.helper.ExampleHelper.getResourceKey;
import static fiftyone.devicedetection.shared.testhelpers.FileUtils.getFilePath;

/**
 * @example console/GettingStartedCloud.java
 *
 * @include{doc} example-getting-started-cloud.txt
 *
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/blob/master/device-detection.examples/console/src/main/java/fiftyone/devicedetection/examples/console/GettingStartedCloud.java).
 *
 * @include{doc} example-require-resourcekey.txt
 */

/**
 * Provides an illustration of the fundamental elements of carrying out device detection using
 * our "cloud" service. Meaning that you don't host the datafile on your server, but request
 * detection to be carried out on our servers.
 * <p>
 * In order to use the cloud service you will need to obtain a "Resource Key". The resource
 * key is obtained from https://configure.51degrees.com/. A free resource key configured with the
 * properties required by this example may be obtained from
 * https://configure.51degrees.com/jqz435Nc
 * <p>
 * The concepts of "pipeline", "flow data", "evidence" and "results" are illustrated.
 */
@SuppressWarnings("DuplicatedCode")
public class GettingStartedCloud {
    private static final Logger logger = LoggerFactory.getLogger(GettingStartedCloud.class);

    /**
     * The resource key can be supplied as an argument to this program or as an environment
     * variable or as a Java system property called "ResourceKey".
     */
    public static void main(String[] args) throws Exception {
        configureLogback(getFilePath("logback.xml"));
        String resourceKey = args.length > 0 ? args[0]: getResourceKey();

        // prepare 'evidence' for use in pipeline (see below)
        List<Map<String, String>> evidence = ExampleHelper.setUpEvidence();
        run(resourceKey, evidence, System.out);
    }

    /**
     * Run the example
     * @param resourceKey a 51Degrees "resource key"
     * @param evidenceList a List<Map<String, String>> representing evidence
     * @param outputStream somewhere for the results
     */
    public static void run(String resourceKey,
                           List<Map<String, String>> evidenceList,
                           OutputStream outputStream) throws Exception {
        logger.info("Running GettingStarted Cloud example");

        ExampleHelper.checkResourceKey(resourceKey);

        /* In this example, we use the DeviceDetectionPipelineBuilder and configure it in code.

        For more information about pipelines in general see the documentation at
        http://51degrees.com/documentation/4.3/_concepts__configuration__builders__index.html

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
            writer.println(outputValue("Mobile Device", device.getIsMobile()));
            writer.println(outputValue("Platform Name", device.getPlatformName()));
            writer.println(outputValue("Platform Version", device.getPlatformVersion()));
            writer.println(outputValue("Browser Name", device.getBrowserName()));
            writer.println(outputValue("Browser Version", device.getBrowserVersion()));
        }
        writer.println();
        writer.flush();
    }

    /**
     * Format a name and an AspectPropertyValue for display
     * @param name the name of the property
     * @param value a value (or no value) for it
     * @return the string representing the above parameters
     */
    private static String outputValue(String name, AspectPropertyValue<?> value) {
        /* Individual result values are wrapped with "AspectPropertyValue". This functions
        similarly to a null-able type. If the value has not been set then trying to access the
        "Value" property will throw an exception. AspectPropertyValue also includes the
        "NoValueMessage", which describes why the value has not been set. */
        return (value.hasValue() ?
                String.format("\t%s: %s", name, value.getValue().toString()) :
                String.format("\t%s: %s", name, value.getNoValueMessage()));
    }
}
