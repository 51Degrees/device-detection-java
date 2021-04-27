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

package fiftyone.devicedetection.examples.hash;

import fiftyone.devicedetection.DeviceDetectionPipelineBuilder;
import fiftyone.devicedetection.examples.ExampleBase;
import fiftyone.devicedetection.examples.ProgramBase;
import fiftyone.devicedetection.hash.engine.onpremise.data.DeviceDataHash;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.Constants;
import java.util.Map;

/**
 * @example hash/MatchMetrics.java
 *
 * @include{doc} example-match-metrics-hash.txt
 * 
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/blob/master/device-detection.examples/src/main/java/fiftyone/devicedetection/examples/hash/MatchMetrics.java).
 * 
 * @include{doc} example-require-datafile.txt
 */

/**
 * Match metrics example.
 */
public class MatchMetrics extends ProgramBase {

    public static void main(String[] args) throws Exception {
        String dataFile = args.length > 0 ? args[0] :
            getDefaultFilePath("51Degrees-LiteV4.1.hash").getAbsolutePath();

        new Example(true).run(dataFile);
        System.out.println("Complete. Press enter to exit.");
        System.in.read();
    }

    public static class Example extends ExampleBase {
        private final String mobileUserAgent =
            "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 " +
            "Mobile Safari/537.36";

        public Example(boolean printOutput) {
            super(printOutput);
        }

        public void run(String dataFile) throws Exception {
            println("Constructing pipeline with engine " +
                "from file " + dataFile);

            // Build a new Pipeline to use an on-premise Hash engine with the
            // low memory performance profile.
            Pipeline pipeline = new DeviceDetectionPipelineBuilder()
                .useOnPremise(dataFile, false)
                .setAutoUpdate(false)
                // Prefer low memory profile where all data streamed from disk 
                // on-demand. Experiment with other profiles.
                .setPerformanceProfile(Constants.PerformanceProfiles.LowMemory)
                //.setPerformanceProfile(Constants.PerformanceProfiles.HighPerformance)
                //.setPerformanceProfile(Constants.PerformanceProfiles.Balanced)
                // Disable share usage for this example.
                .setShareUsage(false)
                // Include the IsMobile property in the results. Notice how the
                // components related to OS, Browser and Crawler will not be
                // provided in the results.
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
                // <a href="https://51degrees.com/documentation/_device_detection__hash.html#DeviceDetection_Hash_DataSetProduction_Performance">Hash Algorithm</a>
                .setUsePredictiveGraph(true)
                .setUsePerformanceGraph(false)
                .build();

            // A try-with-resource block MUST be used for the FlowData instance.
            // This ensures that native resources created by the device 
            // detection engine are freed.
            try (FlowData data = pipeline.createFlowData()) {

                // Process a single HTTP User-Agent string to retrieve the values
                // associated with the User-Agent for the selected properties.
                data.addEvidence(
                    "header.user-agent",
                    mobileUserAgent)
                    .process();

                DeviceDataHash device = data.get(DeviceDataHash.class);
                println("User-Agent:         " + mobileUserAgent);
                // Obtain the matched User-Agent: the matched substrings in the
                // User-Agent separated with underscored.
                println("Matched User-Agent: " +
                    device.getUserAgents().getValue().get(0));
                // Obtains the matched Device ID: the IDs of the matched profiles
                // separated with hyphens. Notice how the value changes depending
                // on the properties that are used with the builder. Profile IDs are
                // replaced with zeros when there are no properties associated with
                // the corresponding component available.
                println("Id: " + device.getDeviceId().getValue());
                // Obtain difference: The total difference in hash code values
                // between the matched substrings and the actual substrings. The
                // maximum difference to allow when finding a match can be set
                // through the configuration structure.
                println("Difference: " + device.getDifference().getValue());
                // Obtain drift: The maximum drift for a matched substring from the
                // character position where it was expected to be found. The maximum
                // drift to allow when finding a match can be set through the
                // configuration structure.
                println("Drift: " + device.getDrift().getValue());
                // Obtain iteration count: The number of iterations required to get
                // the device offset in the devices collection in the graph of
                // nodes. This is indicative of the time taken to fetch the result.
                println("Iterations: " + device.getIterations().getValue());
                // Output the method that was used to obtain the result. Play with
                // the setUsePredictiveGraph and setUsePerformanceGraph values to
                // see the different results.
                println("Method: " + device.getMethod().getValue());
                // Use the dictionary of key value pairs to output all the available
                // values. Slightly less efficient than the strongly typed
                // accessors.
                Map<String, Object> entries = device.asKeyMap();
                for (Map.Entry<String, Object> entry : entries.entrySet()) {
                    println(entry.getKey() + ": " + entry.getValue());
                }
            }
        }
    }
}
