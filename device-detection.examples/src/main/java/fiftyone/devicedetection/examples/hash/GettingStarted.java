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
import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.Constants;
import fiftyone.pipeline.engines.data.AspectPropertyValue;

/**
 * @example hash/GettingStarted.java
 *
 * Getting started example of using 51Degrees device detection.
 *
 * The example shows how to:
 *
 * 1. Build a new Pipeline to use an on-premise Hash engine with the low memory
 * performance profile.
 * ```
 * Pipeline pipeline = new DeviceDetectionPipelineBuilder()
 *     .useOnPremise("51Degrees-LiteV4.1.hash", false)
 *     .setAutoUpdate(false)
 *     .setPerformanceProfile(Constants.PerformanceProfiles.LowMemory)
 *     .build();
 * ```
 *
 * 2. Create a new FlowData instance ready to be populated with evidence for the
 * Pipeline.
 * ```
 * FlowData data = pipeline.createFlowData();
 * ```
 *
 * 3. Process a single HTTP User-Agent string to retrieve the values associated
 * with the User-Agent for the selected properties.
 * ```
 * data.addEvidence("header.user-agent", mobileUserAgent)
 *     .process();
 * ```
 *
 * 4. Extract the value of a property as a string from the results.
 * ```
 * AspectPropertyValue<Boolean> isMobile =
 *     data.get(DeviceData.class).getIsMobile();
 * if (isMobile.hasValue()) {
 *     println("IsMobile: " + isMobile.getValue());
 * }
 * ```
 */

public class GettingStarted extends ProgramBase {

    public static void main(String[] args) throws Exception {
        String dataFile = args.length > 0 ? args[0] :
            getDefaultFilePath("51Degrees-LiteV4.1.hash").getAbsolutePath();

        new Example(true).run(dataFile);
        System.out.println("Complete. Press enter to exit.");
        System.in.read();
    }

    public static class Example extends ExampleBase {
        private String mobileUserAgent =
            "Mozilla/5.0 (iPhone; CPU iPhone OS 7_1 like Mac OS X) " +
                "AppleWebKit/537.51.2 (KHTML, like Gecko) Version/7.0 Mobile" +
                "/11D167 Safari/9537.53";

        public Example(boolean printOutput) {
            super(printOutput);
        }

        public void run(String dataFile) throws Exception {
            println("Constructing pipeline with engine " +
                "from file " + dataFile);
            // Create a simple pipeline to access the engine with.
            Pipeline pipeline = new DeviceDetectionPipelineBuilder()
                .useOnPremise(dataFile, false)
                //.setShareUsage(false)
                // Prefer low memory profile where all data streamed
                // from disk on-demand. Experiment with other profiles.
                //.setPerformanceProfile(Constants.PerformanceProfiles.HighPerformance)
                .setPerformanceProfile(Constants.PerformanceProfiles.LowMemory)
                //.setPerformanceProfile(Constants.PerformanceProfiles.Balanced)
                .build();

            FlowData data = pipeline.createFlowData();
            data.addEvidence(
                "header.user-agent",
                mobileUserAgent)
                .process();
            AspectPropertyValue<Boolean> isMobile = data.get(DeviceData.class).getIsMobile();
            if (isMobile.hasValue()) {
                println("IsMobile: " + isMobile.getValue());
            }
            else {
                println(isMobile.getNoValueMessage());
            }
        }
    }
}
