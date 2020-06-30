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

package fiftyone.devicedetection.examples.cloud;

import fiftyone.devicedetection.DeviceDetectionPipelineBuilder;
import fiftyone.devicedetection.examples.ExampleBase;
import fiftyone.devicedetection.examples.ProgramBase;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.data.AspectPropertyValue;


/**
 * @example cloud/GettingStarted.java
 *
 * Getting started example of using the 51Degrees Device Detection Cloud to determine whether a given User-Agent corresponds to a mobile device or not.
 *
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/blob/master/device-detection.examples/src/main/java/fiftyone/devicedetection/examples/cloud/GettingStarted.java).
 *
 * To run this example, you will need to create a **resource key**.
 * The resource key is used as short-hand to store the particular set of
 * properties you are interested in as well as any associated license keys
 * that entitle you to increased request limits and/or paid-for properties.
 *
 * You can create a resource key using the 51Degrees [Configurator](https://configure.51degrees.com).
 *
 * Build the device detection pipeline using the builder that comes with the
 * fiftyone.devicedetection package and pass in the desired settings. Additional
 * flow elements / engines can be added before the build() method is called if
 * needed.
 *
 * ```
 *
 * Pipeline pipeline = new DeviceDetectionPipelineBuilder()
 *     .useCloud(resourceKey)
 *     .build()
 *
 * ```
 *
 * A pipeline can create a flow data element which is where evidence is added
 * (for example from a device web request). This evidence is then processed by
 * the pipeline through the flow data's `process()` method.
 *
 * Here is an example of a function that checks if a User-Agent is a mobile
 * device. In some cases the IsMobile value is not meaningful so instead of
 * returning a default, a .hasValue() check can be made.
 *
 * ```
 * AspectPropertyValue<Boolean> isMobile = data.get(DeviceData.class).getIsMobile();
 * if (isMobile.hasValue()) {
 *     println("IsMobile: " + isMobile.getValue());
 * } else {
 *     println("IsMobile: " + isMobile.getNoValueMessage());
 * }
 *
 * ```
 */

public class GettingStarted extends ProgramBase {

    public static void main(String[] args) throws Exception {
        String resourceKey = args.length > 0 ? args[0] :
            // Obtain a resource key for free at https://configure.51degrees.com
            // Make sure to include the 'IsMobile' property as it is used by this example.
            "!!YOUR_RESOURCE_KEY!!";
        new fiftyone.devicedetection.examples.cloud.GettingStarted.Example(true).run(resourceKey);
        System.out.println("Complete. Press enter to exit.");
        System.in.read();
    }

    public static class Example extends ExampleBase {
        private final String mobileUserAgent =
            "Mozilla/5.0 (iPhone; CPU iPhone OS 7_1 like Mac OS X) " +
                "AppleWebKit/537.51.2 (KHTML, like Gecko) Version/7.0 Mobile" +
                "/11D167 Safari/9537.53";

        public Example(boolean printOutput) {
            super(printOutput);
        }

        public void run(String resourceKey) throws Exception {
            if (resourceKey.startsWith("!!")) {
                println("You need to create a resource key at " +
                        "https://configure.51degrees.com and paste it into this example.");
                println("Make sure to include the 'IsMobile' " +
                        "property as it is used by this example.");
            }
            else {
                println("Constructing pipeline with cloud engine " +
                        "with resource key: " + resourceKey);
                // Create a simple pipeline to access the engine with.
                Pipeline pipeline = new DeviceDetectionPipelineBuilder()
                        // Obtain a resource key from https://configure.51degrees.com
                        .useCloud(resourceKey)
                        .useLazyLoading(1000)
                        .setAutoCloseElements(true)
                        .build();


                FlowData data = pipeline.createFlowData();
                data.addEvidence(
                        "header.user-agent",
                        mobileUserAgent)
                        .process();

                AspectPropertyValue<Boolean> isMobile = data.get(DeviceData.class).getIsMobile();
                if (isMobile.hasValue()) {
                    println("IsMobile: " + isMobile.getValue());
                } else {
                    println("IsMobile: " + isMobile.getNoValueMessage());
                }
            }
        }
    }
}
