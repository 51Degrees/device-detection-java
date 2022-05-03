package fiftyone.devicedetection.examples.console;

import fiftyone.devicedetection.DeviceDetectionPipelineBuilder;
import fiftyone.devicedetection.examples.shared.DataFileHelper;
import fiftyone.devicedetection.examples.shared.ExampleTestHelper;
import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.Constants;
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
 * "on premise" (aka Hash) detection - meaning the device detection data is stored on your server
 * and the detection software executes exclusively on your server.
 * <p>
 * The concepts of "pipeline", "flow data", "evidence" and "results" are illustrated.
 */
public class GettingStartedOnPrem {
    private static final Logger logger = LoggerFactory.getLogger(GettingStartedOnPrem.class);

    /* In this example, by default, the 51degrees "Lite" file needs to be somewhere in the project
    space, or you may specify another file as a command line parameter.

    Note that the Lite data file is only used for illustration, and has limited accuracy and
    capabilities. Find out about the Enterprise data file here: https://51degrees.com/pricing */
    public static String LITE_V_4_1_HASH = "51Degrees-LiteV4.1.hash";

    public static void main(String[] args) throws Exception {
        configureLogback(getFilePath("logback.xml"));
        String dataFile = args.length > 0 ? args[0] : LITE_V_4_1_HASH;
        // prepare 'evidence' for use in pipeline (see below)
        List<Map<String, String>> evidence = ExampleTestHelper.setUpEvidence();
        run(dataFile, evidence, System.out);
    }

    /**
     * Run the example
     * @param dataFile a device detection data file
     * @param evidenceList a List<Map<String, String>> representing evidence
     * @param outputStream somewhere for the results
     */
    public static void run(String dataFile,
                           List<Map<String, String>> evidenceList,
                           OutputStream outputStream) throws Exception {
        logger.info("Running GettingStarted example");
        String dataFileLocation;
        try {
            dataFileLocation = getFilePath(dataFile).getAbsolutePath();
        } catch (Exception e) {
            DataFileHelper.cantFindDataFile(dataFile);
            throw e;
        }

        /* In this example, we use the DeviceDetectionPipelineBuilder and configure it in code.

        For more information about pipelines in general see the documentation at
        http://51degrees.com/documentation/_concepts__configuration__builders__index.html

        Note that we wrap the creation of a pipeline in a try/resources to control its lifecycle */
        try (Pipeline pipeline = new DeviceDetectionPipelineBuilder()
                .useOnPremise(dataFileLocation, false)

                /* We use the low memory profile as its performance is
                sufficient for this example. See the documentation for
                more detail on this and other configuration options:
                http://51degrees.com/documentation/_device_detection__features__performance_options.html
                http://51degrees.com/documentation/_features__automatic_datafile_updates.html
                http://51degrees.com/documentation/_features__usage_sharing.html */
                .setPerformanceProfile(Constants.PerformanceProfiles.LowMemory)
                /* inhibit sharing usage for this test, in production it should be "true" */
                .setShareUsage(false)
                /* inhibit auto-update of the data file for this test */
                .setAutoUpdate(false)
                .setDataUpdateOnStartup(false)
                .setDataFileSystemWatcher(false)
                .build()) {


            // carry out some sample detections
            for (Map<String, String> evidence : evidenceList) {
                analyzeEvidence(evidence, pipeline, outputStream);
            }

            /* Get the 'engine' element within the pipeline that performs device detection. We
            can use this to get details about the data file as well as meta-data describing
            things such as the available properties. */
            DeviceDetectionHashEngine engine = pipeline.getElement(DeviceDetectionHashEngine.class);
            DataFileHelper.logDataFileInfo(dataFileLocation, engine);

            logger.info("All done");
        }
    }

    /**
     * Taking a map of evidence as a parameter, process it in the pipeline
     * supplied and output the device detection results to the output stream.
     * @param evidence a map representing HTTP headers
     * @param pipeline a pipeline set up to process the evidence
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
 * @example console/GettingStartedOnPrem.java
 * @include{doc} example-getting-started-onpremise.txt
 * <p>
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/blob/master/device-detection.examples/console/src/main/java/fiftyone/devicedetection/examples/console/GettingStartedOnPrem.java).
 * @include{doc} example-require-datafile.txt
 */
