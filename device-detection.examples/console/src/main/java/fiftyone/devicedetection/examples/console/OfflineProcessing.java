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
import fiftyone.devicedetection.examples.shared.DataFileHelper;
import fiftyone.devicedetection.examples.shared.EvidenceHelper;
import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.Constants;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static fiftyone.common.testhelpers.LogbackHelper.configureLogback;
import static fiftyone.devicedetection.examples.shared.PropertyHelper.asString;
import static fiftyone.pipeline.util.FileFinder.getFilePath;

/**
 * Provides an example of processing a YAML file containing evidence for device detection. There are
 * 20,000 examples in the supplied file of evidence representing HTTP Headers. For example:
 * <p>
 * <code><pre>
 *   header.user-agent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36'
 *   header.sec-ch-ua: '" Not A;Brand";v="99", "Chromium";v="98", "Google Chrome";v="98"'
 *   header.sec-ch-ua-full-version: '"98.0.4758.87"'
 *   header.sec-ch-ua-mobile: '?0'
 *   header.sec-ch-ua-platform: '"Android"'
 * </pre></code>
 * <p>
 * We create a device detection pipeline to read the data and find out about the associated devices,
 * we write this data to a YAML formatted output stream.
 * <p>
 * As well as explaining the basic operation of offline processing using the defaults, for advanced
 * operation this example can be used to experiment with tuning device detection for performance and
 * predictive power using Performance Profile, Graph and Difference and Drift settings.
 */
public class OfflineProcessing {
    static final Logger logger = LoggerFactory.getLogger(OfflineProcessing.class);
    // This 51degrees "Lite" file (distributed with the source) needs to
    // be somewhere in the project space
    //
    // Note that the Lite data file is only used for illustration, and has
    // limited accuracy and capabilities. Find out about the Enterprise data
    // file here: https://51degrees.com/pricing
    public static final String LITE_V_4_1_HASH =
            "device-detection-data/51Degrees-LiteV4.1.hash";
    // This 51degrees file of 20,000 examples (distributed with the source)
    // needs to be somewhere in the project space
    public static final String HEADER_EVIDENCE_YML =
            "device-detection-data/20000 Evidence Records.yml";

    public static void main(String[] args) throws Exception {
        configureLogback(getFilePath("logback.xml"));
        File evidenceFile = getFilePath(HEADER_EVIDENCE_YML);
        run(LITE_V_4_1_HASH, Files.newInputStream(evidenceFile.toPath()), System.out);
    }

    /**
     * Process a YAML representation of evidence - and create a YAML output
     * containing the processed evidence
     *
     * @param dataFile the 51Degrees on premise data file containing
     *                 information about devices
     * @param is       an InputStream containing YAML documents - one per device
     * @param os       an OutputStream for the processed data
     */
    public static void run(String dataFile, InputStream is, OutputStream os) throws Exception {

        String detectionFile;
        try {
            detectionFile = getFilePath(dataFile).getAbsolutePath();
        } catch (Exception e) {
            DataFileHelper.cantFindDataFile(dataFile);
            throw e;
        }

        // get a YAML loader to iterate over the device evidence
        Iterable<Map<String, String>> evidenceIterator = EvidenceHelper.getEvidenceIterable(is);

        /*
          ---- Build a pipeline ----
         */

        logger.info("Constructing pipeline with on-premise hash " +
                "engine from file " + dataFile);
        // Build a new on-premise Hash engine in a try/resources so
        // that the pipeline is disposed when done
        try (Pipeline pipeline = new DeviceDetectionPipelineBuilder()
                .useOnPremise(detectionFile, false)
                // inhibit sharing usage for this test, usually
                // this should be set "true"
                .setShareUsage(false)
                // inhibit auto-update of the data file for this test
                .setAutoUpdate(false)
                // -- Setting the Profile
                // For information on profiles see
                // https://51degrees.com/documentation/_device_detection__features__performance_options.html
                //.setPerformanceProfile(Constants.PerformanceProfiles.MaxPerformance)
                //.setPerformanceProfile(Constants.PerformanceProfiles.HighPerformance)
                // Low memory profile has detection data streamed from disk on
                // demand and is conservative in its use of memory, but
                // slower because of disk access
                .setPerformanceProfile(Constants.PerformanceProfiles.LowMemory)
                //.setPerformanceProfile(Constants.PerformanceProfiles.Balanced)
                // -- Setting the Graph
                // see https://51degrees.com/documentation/_device_detection__hash.html#DeviceDetection_Hash_DataSetProduction
                //.setUsePerformanceGraph(false)
                //.setUsePredictiveGraph(true)
                // -- Setting Predictive Power
                // see https://51degrees.com/documentation/_device_detection__hash.html#DeviceDetection_Hash_PredictivePower
                //.setDifference(0)
                //.setDrift(0)
                .build()) {

            // get the details of the detection engine from the pipeline,
            // to find out what data file we are using
            DeviceDetectionHashEngine engine = pipeline.getElement(DeviceDetectionHashEngine.class);
            logger.info("Device data file was created {}", engine.getDataFilePublishedDate());

            /*
              ---- Iterate over the evidence ----
             */

            // open a writer to collect the results
            try (Writer writer = new OutputStreamWriter(os)) {
                // read a batch of device data from the stream
                int count = 0;
                while (evidenceIterator.iterator().hasNext() && count < 20) {
                    // Flow data is the container for inputs and outputs that
                    // flow through the pipeline a flowdata instance is
                    // created by the pipeline factory method it's important
                    // to dispose flowdata - so wrap in a try/resources
                    try (FlowData flowData = pipeline.createFlowData()) {
                        // the evidence values in the test YAML data are read
                        // as a Map<String, String> - add the evidence to the
                        // flowData
                        flowData.addEvidence(
                                filterEvidence(evidenceIterator.iterator().next(),
                                        "header."));

                        /*
                          ---- Do the detection ----
                         */

                        // carry out device-detection (and other
                        // pipeline actions) on the evidence
                        flowData.process();
                        // extract device data from the flowData
                        DeviceData device = flowData.get(DeviceData.class);

                        /*
                          ---- use the device data - output to YAML in this case
                         */

                        Map<String, ? super Object> resultMap = new HashMap<>();
                        resultMap.put("device.ismobile", asString(device.getIsMobile()));
                        resultMap.put("device.platformname", asString(device.getPlatformName()));
                        resultMap.put("device.platformversion", asString(device.getPlatformVersion()));

                        // to look at all device detection properties use the following:
                        // resultMap.putAll(getPopulatedProperties(device, "device."));

                        // write document to output stream
                        writer.write("---\n");
                        StringBuilder sb = new StringBuilder();
                        flowData.getEvidence().asKeyMap()
                                .forEach((k,v)->sb.append(String.format("%s: %s\n", k, v)));
                        resultMap.forEach((k,v)->sb.append(String.format("%s: %s\n", k, v)));
                        writer.write(sb.toString());
                        writer.flush();
                    }
                    count++;
                }
                // finish the last YAML document
                writer.write("...\n");
                writer.flush();
                logger.info("Finished processing {} records", count);

                if (engine.getDataSourceTier().equals("Lite")) {
                    logger.warn("You have used a Lite data file which has " +
                            "limited properties and is of limited accuracy");
                    logger.info("The example requires an Enterprise data file " +
                            "to work fully. Find out about the Enterprise " +
                            "data file here: https://51degrees.com/pricing");
                }
            }
        }
    }
	
    /**
     * Filter entries that are not keyed on the required prefix
     *
     * @param prefix a prefix for the evidence to filter - e.g. "header."
     * @param evidence a Map<String, String> of evidence entries
     * @return a filtered Map
     */
    @SuppressWarnings("SameParameterValue")
    private static Map<String, String> filterEvidence(Map<String, String> evidence, String prefix) {

        return evidence.entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(prefix))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Get a map of device properties that are populated
     * @param data DeviceData
     * @param prefix the prefix we want for the property name e.g "device."
     * @return a filtered map
     */
    @SuppressWarnings({"SameParameterValue", "unused"})
    private static Map<String, Object> getPopulatedProperties(DeviceData data, String prefix) {

        return data.asKeyMap().entrySet()
                .stream()
                .filter(e -> ((AspectPropertyValue<?>) e.getValue()).hasValue())
                .collect(Collectors.toMap(e -> prefix + e.getKey(),
                        e -> ((AspectPropertyValue<?>)e.getValue()).getValue()));
    }
}
/*!
 * @example console/OfflineProcessing.java
 * @include{doc} example-offline-processing-hash.txt
 * <p>
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/blob/master/device-detection.examples/console/src/main/java/fiftyone/devicedetection/examples/console/OfflineProcessing.java).
 * @include{doc} example-require-datafile.txt
 */


