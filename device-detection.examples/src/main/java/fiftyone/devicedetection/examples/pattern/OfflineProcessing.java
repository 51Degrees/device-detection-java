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

package fiftyone.devicedetection.examples.pattern;

import fiftyone.devicedetection.DeviceDetectionPipelineBuilder;
import fiftyone.devicedetection.examples.ExampleBase;
import fiftyone.devicedetection.examples.ProgramBase;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.Constants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * @example pattern/OfflineProcessing.java
 *
 * Offline processing example of using 51Degrees device detection.
 *
 * The example shows how to:
 *
 * 1. Build a new on-premise Pattern engine with the low memory performance profile.
 * ```
 * DeviceDetectionPatternEngine engine = new DeviceDetectionPatternEngineBuilder()
 *     .setAutoUpdate(false)
 *     .setPerformanceProfile(Constants.PerformanceProfiles.LowMemory)
 *     .build("51Degrees-LiteV3.2.dat", false);
 * ```
 *
 * 2. Read a batch of User-Agent strings from a file.
 * ```
 * BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
 * for (int i = 0; i < nLines; i++) {
 *     String userAgentString = bufferedReader.readLine();
 * }
 * ```
 *
 * 3. Process each User-Agent using the Pipeline.
 * ```
 * FlowData flowData = pipeline.createFlowData();
 * flowData.addEvidence("header.user-agent", userAgentString)
 *     .process();
 * DeviceData device = flowData.get(DeviceData.class);
 * ```
 *
 * 4. Create a new CSV file to write the results of the processing to.
 * ```
 * FileWriter fileWriter = new FileWriter(outputFile);
 * ```
 *
 * 5. Write the values of any required properties to the output file for later.
 * ```
 * fileWriter.write(userAgentString +
 *     "," + device.getIsMobile() +
 *     "," + device.getPlatformName() +
 *     "," + device.getPlatformVersion() +
 *     "\n");
 * ```
 */
public class OfflineProcessing extends ProgramBase {

    public static void main(String[] args) throws Exception {
        String dataFile = args.length > 0 ? args[0] :
            getDefaultFilePath("51Degrees-LiteV3.2.dat").getAbsolutePath();

        new Example(true).run(
            dataFile,
            getDefaultFilePath("20000 User Agents.csv").getAbsolutePath(),
            Example.outputFilePath);
        System.out.println("Complete. Press enter to exit.");
        System.in.read();
    }

    public static class Example extends ExampleBase {

        // output file in current working directory
        public static final String outputFilePath = "batch-processing-example-results.csv";

        public Example(boolean printOutput) {
            super(printOutput);
        }

        public void run(String dataFile, String inputFile, String outputFile) throws Exception {
            println("Constructing pipeline with engine " +
                "from file " + dataFile);
            // Create a simple pipeline to access the engine with.
            Pipeline pipeline = new DeviceDetectionPipelineBuilder()
                .useOnPremise(dataFile, false)
                .setAutoUpdate(false)
                // Prefer low memory profile where all data streamed
                // from disk on-demand. Experiment with other profiles.
                //.setPerformanceProfile(Constants.PerformanceProfiles.HighPerformance)
                .setPerformanceProfile(Constants.PerformanceProfiles.LowMemory)
                //.setShareUsage(false)
                //.setPerformanceProfile(Constants.PerformanceProfiles.Balanced)
                .build();

            try (BufferedReader bufferedReader =
                     new BufferedReader(new FileReader(inputFile));
                 FileWriter fileWriter = new FileWriter(outputFile)) {
                for (int i = 0; i < 20; i++) {
                    // read next line
                    String userAgentString = bufferedReader.readLine();

                    FlowData flowData = pipeline.createFlowData();

                    flowData.addEvidence("header.user-agent",
                        userAgentString)
                        .process();

                    DeviceData device = flowData.get(DeviceData.class);

                    // get some property values from the match
                    // write result to file
                    fileWriter.write(userAgentString +
                        "," + device.getIsMobile() +
                        "," + device.getPlatformName() +
                        "," + device.getPlatformVersion() +
                        "\n");
                }
            }
            println("Output written to " +
                outputFile);
        }
    }
}
