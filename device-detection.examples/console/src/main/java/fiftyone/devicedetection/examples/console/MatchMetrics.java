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
import fiftyone.devicedetection.hash.engine.onpremise.data.DeviceDataHash;
import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.devicedetection.shared.testhelpers.FileUtils;
import fiftyone.pipeline.core.data.ElementPropertyMetaData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.Constants;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.fiftyone.data.ComponentMetaData;
import fiftyone.pipeline.engines.fiftyone.data.FiftyOneAspectPropertyMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static fiftyone.common.testhelpers.LogbackHelper.configureLogback;
import static fiftyone.pipeline.util.FileFinder.getFilePath;
import static java.util.stream.Collectors.groupingBy;

/**
 * The example illustrates the various metrics that can be obtained about the device detection
 * process, for example, the degree of certainty about the result. Running the example outputs
 * those property names and (optionally) their descriptions.
  * <p>
 * There is a
 * <a href="https://51degrees.com/documentation/_device_detection__hash.html#DeviceDetection_Hash_DataSetProduction_Performance">discussion</a>
 * of metrics and controlling performance on our web site. See also the
 * <a href="https://51degrees.com/documentation/_device_detection__features__performance_options.html">performance options</a>
 * page.
 */
public class MatchMetrics {
    private static final Logger logger = LoggerFactory.getLogger(MatchMetrics.class);

    public static void main(String[] args) throws Exception {
        configureLogback(getFilePath("logback.xml"));
        String dataFilename = args.length > 0 ? args[0] : null;
        run(dataFilename, EvidenceHelper.setUpEvidence(), false, System.out);
    }

    /**
     * Run the example
     * @param dataFile a data file to use, if null a default datafile will be chosen.
     * @param evidenceList a list of maps of evidence keys and values
     * @param showDescs show descriptions of properties
     * @param out an output stream
     */
    static void run(String dataFile, List<Map<String, String>> evidenceList,
                    boolean showDescs, OutputStream out) throws Exception {
        PrintWriter writer = new PrintWriter(out, true);
        if (Objects.isNull(dataFile)) {
            dataFile = FileUtils.getHashFileName();
        }
        logger.info("Constructing pipeline from file " + dataFile);

        // Build a new Pipeline to use an on-premise Hash engine with the
        // low memory performance profile.
        try (Pipeline pipeline = new DeviceDetectionPipelineBuilder()
                .useOnPremise(dataFile, true)
                .setAutoUpdate(false)
                // Prefer low memory profile where all data streamed from disk
                // on-demand. Experiment with other profiles.
                .setPerformanceProfile(Constants.PerformanceProfiles.LowMemory)
                //.setPerformanceProfile(Constants.PerformanceProfiles.HighPerformance)
                //.setPerformanceProfile(Constants.PerformanceProfiles.Balanced)
                // Disable share usage for this example.
                .setShareUsage(false)
                // You can improve matching performance by specifying only those
                // properties you wish to use. If you don't specify any properties
                // you will get all those available in the data file tier that
                // you have used. The free "Lite" tier contains fewer than 20.
                // Since we are specifying properties here, we will only see
                // those properties, along with the match metric properties
                // in the output.
                .setProperty("IsMobile")
                // Uncomment BrowserName to include Browser component profile ID
                // in the device ID value.
                //.setProperty("BrowserName")
                // If using the full on-premise data file this property will be
                // present in the data file. See https://51degrees.com/pricing
                .setProperty("HardwareName")
                // Only use the predictive graph to better handle variances
                // between the training data and the target User-Agent string.
                // For a more detailed description of the differences between
                // performance and predictive, see
                // https://51degrees.com/documentation/_device_detection__hash.html#DeviceDetection_Hash_DataSetProduction_Performance
                .setUsePredictiveGraph(true)
                .setUsePerformanceGraph(false)
                .build()) {

            DataFileHelper.logDataFileInfo(pipeline.getElement(DeviceDetectionHashEngine.class));

            // A try-with-resource block MUST be used for the FlowData instance.
            // This ensures that native resources created by the device
            // detection engine are freed.
            try (FlowData data = pipeline.createFlowData()) {

                // Process a single evidence to retrieve the values
                // associated with the user-agent and other evidence such as sec-ch-* for the
                // selected properties.
                data.addEvidence(evidenceList.get(2))
                        .process();

                DeviceDataHash device = data.get(DeviceDataHash.class);

                writer.println("--- Compare evidence with what was matched ---\n");
                writer.println("Evidence");
                // output the evidence in reverse value length order
                evidenceList.get(2).entrySet().stream()
                        .sorted(Comparator.comparingInt(e -> e.getValue().length()))
                        .forEach(e -> writer.format("    %-34s: %s%n", e.getKey(), e.getValue()));
                // Obtain the matched User-Agents: the matched substrings in the
                // User-Agents are separated with underscores - output in forward length order.
                writer.println("Matches");
                device.getUserAgents().getValue().stream()
                        .sorted(Comparator.comparingInt(String::length).reversed())
                        .forEach(v -> writer.format("    %-34s: %s%n", "Matched User-Agent", v));

                writer.println();


                writer.println("--- Listing all available properties, by component, by property " +
                        "name ---");
                writer.println("For a discussion of what the match properties mean, see: " +
                        "https://51degrees.com/documentation/_device_detection__hash" +
                        ".html#DeviceDetection_Hash_DataSetProduction_Performance\n");

                // get the properties available from the DeviceDetection engine
                // which has the key "device". For the sake of illustration we will
                // retrieve it indirectly.
                String hashEngineElementKey =
                        pipeline.getElement(DeviceDetectionHashEngine.class).getElementDataKey();

                // retrieve the available properties from the hash engine. The properties
                // available depends on
                // a) the use of setProperty() in the builder (see above)
                // which controls which properties will be extracted, and also affects
                // the performance of extraction
                // b) the tier of data file being used. The Lite data file contains fewer
                // than 20 of the >200 available properties
                Map<String, ElementPropertyMetaData> availableProperties =
                        pipeline.getElementAvailableProperties().get(hashEngineElementKey);

                // create a Map keyed on the component name of the properties available
                // components being hardware, browser, OS and Crawler.
                // Match metric properties are not allocated to a component, so we will
                // add a key "MatchMetric"
                Map<String, List<Map.Entry<String, ElementPropertyMetaData>>> categoryMap =
                        availableProperties.entrySet().stream()
                                .collect(groupingBy(e -> {
                                    ComponentMetaData component =
                                            ((FiftyOneAspectPropertyMetaData) e.getValue()).getComponent();
                                    return Objects.nonNull(component) ?
                                            component.getName() : "MatchMetric";
                                }));

                // iterate the map created above
                categoryMap.forEach((component, propertyMapEntry) -> {
                    writer.format("%s%n", component);
                    propertyMapEntry.forEach(e -> {
                        FiftyOneAspectPropertyMetaData propertyMetaData =
                                (FiftyOneAspectPropertyMetaData) e.getValue();
                        String propertyName = propertyMetaData.getName();
                        String propertyDescription = propertyMetaData.getDescription();

                        // while we get the available properties and their metadata from the
                        // pipeline we get the values for the last detection from flowData
                        AspectPropertyValue<?> propertyValue =
                                (AspectPropertyValue<?>) device.get(propertyName);

                        // output property names, values and descriptions
                        // some property values are lists. the following check is to avoid compiler
                        // warning about unsafe casting. propertyMetaData.getList() will be true
                        if (propertyValue.hasValue() && propertyValue.getValue() instanceof List) {
                            List<?> values = ((List<?>) propertyValue.getValue());
                            writer.format("    %-24s: %s Values%n", propertyName, values.size());
                            values.forEach(a -> writer.format("        %-20s: %s%n", "", a));
                        } else {
                            writer.format("    %-24s: %s%n", propertyName, propertyValue);
                        }
                        if (showDescs) {
                            writer.format("        %s%n", propertyDescription);
                        }
                    });
                });
                writer.println();
            }
        }
        logger.info("Finished Match Metrics Example");
    }
}
/*!
 * @example console/MatchMetrics.java
 * The example illustrates the various metrics that can be obtained about the device detection
 * process, for example, the degree of certainty about the result. Running the example outputs
 * those properties and values..
 * <p>
 * The example also illustrates controlling properties that are returned from the detection
 * process - reducing the number of components required to return the properties requested reduces
 * the overall time taken.
 * <p>
 * There is a (discussion)[https://51degrees.com/documentation/_device_detection__hash.html#DeviceDetection_Hash_DataSetProduction_Performance]
 * of metrics and controlling performance on our web site. See also the (performance options)
 * [//51degrees.com/documentation/_device_detection__features__performance_options.html]
 * page.
 * # Location
 * This example is available in full on (GitHub)[https://github.com/51Degrees/device-detection-java/blob/master/device-detection.examples/console/src/main/java/fiftyone/devicedetection/examples/console/MatchMetrics.java].
 *
 */

