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
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;

/**
 * This is the code for the minimal example displayed in the configurator. Please see
 * <a href="https://51degrees.com/documentation/_examples__device_detection__getting_started__console__cloud.html">
 * Getting Started Console Cloud</a> for a fuller example.
 */

/*
  This is the dependency needed in your POM or similar:

<dependency>
    <groupId>com.51degrees</groupId>
    <artifactId>device-detection</artifactId>
    <version>[4.4.11,)</version>
</dependency>
 */
public class MinimalExample {

    public static void main(String[] args) throws Exception {
        String resource = args.length > 0 ? args[0] : null;
        run(resource);
    }

    public static void run(String resource) throws Exception {

        // create a minimal pipeline to access the cloud engine
        // you only need one pipeline for multiple requests
        // use try-with-resources to free the pipeline when done
        try (Pipeline pipeline = new DeviceDetectionPipelineBuilder()
                .useCloud(resource)
                .build()) {

            /* get a flow data from the singleton pipeline for each detection */
            // it's important to free the flowdata when done
            try (FlowData data = pipeline.createFlowData()) {
                // add user-agent and client hint headers (if any) from the HTTP request
                data.addEvidence("header.user-agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                               "AppleWebKit/537.36 (KHTML, like Gecko) " +
                               "Chrome/98.0.4758.102 Safari/537.36");
                data.addEvidence("header.sec-ch-ua-mobile", "?0");
                data.addEvidence("header.sec-ch-ua",
                            "\" Not A; Brand\";v=\"99\", \"Chromium\";v=\"98\", " +
                               "\"Google Chrome\";v=\"98\"");
                data.addEvidence("header.sec-ch-ua-platform", "\"Windows\"");
                data.addEvidence("header.sec-ch-ua-platform-version", "\"14.0.0\"");
                // process evidence
                data.process();
                // get the results
                DeviceData device = data.get(DeviceData.class);

                System.out.println("device.IsMobile: " + device.getIsMobile().getValue());
            }
        }
    }
}
