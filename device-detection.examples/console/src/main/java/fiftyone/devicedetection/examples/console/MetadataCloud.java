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
import fiftyone.devicedetection.cloud.flowelements.DeviceDetectionCloudEngine;
import fiftyone.devicedetection.examples.shared.KeyHelper;
import fiftyone.pipeline.cloudrequestengine.flowelements.CloudRequestEngine;
import fiftyone.pipeline.core.data.EvidenceKeyFilterWhitelist;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.data.AspectPropertyMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

import static fiftyone.common.testhelpers.LogbackHelper.configureLogback;
import static fiftyone.pipeline.util.FileFinder.getFilePath;


public class MetadataCloud {
    private static final Logger logger = LoggerFactory.getLogger(GettingStartedOnPrem.class);

    public static void main(String[] args) throws Exception {
        configureLogback(getFilePath("logback.xml"));
        String resourceKey = args.length > 0 ? args[0]: null;
        run(resourceKey, System.out);
    }


    public static void run(String resourceKey, OutputStream output) throws Exception {
        logger.info("Running MetadataCloud example");

        resourceKey = KeyHelper.getOrSetTestResourceKey(resourceKey);

        // Build a new on-premise Hash engine with the low memory performance profile.
        // Note that there is no need to construct a complete pipeline in order to access
        // the meta-data.
        // If you already have a pipeline and just want to get a reference to the engine 
        // then you can use `var engine = pipeline.GetElement<DeviceDetectionHashEngine>();`
        try (Pipeline pipeline = new DeviceDetectionPipelineBuilder(LoggerFactory.getILoggerFactory())
                .useCloud(resourceKey)
                .build()) {

            PrintWriter writer = new PrintWriter(output);

            logger.info("Listing Properties");
            outputProperties(pipeline.getElement(DeviceDetectionCloudEngine.class), writer);
            writer.println();
            writer.flush();

            // We use the CloudRequestEngine to get evidence key details, rather than the
            // DeviceDetectionCloudEngine.
            // This is because the DeviceDetectionCloudEngine doesn't actually make use
            // of any evidence values. It simply processes the JSON that is returned
            // by the call to the cloud service that is made by the CloudRequestEngine.
            // The CloudRequestEngine is actually taking the evidence values and passing
            // them to the cloud, so that's the engine we want the keys from.
            logger.info("Listing Evidence Key Details");
            outputEvidenceKeyDetails(pipeline.getElement(CloudRequestEngine.class), writer);
            writer.println();
            writer.flush();
        }
    }

    private static void outputEvidenceKeyDetails(CloudRequestEngine engine, PrintWriter output) {
        output.println();
        if (engine.getEvidenceKeyFilter() instanceof EvidenceKeyFilterWhitelist) {
            // If the evidence key filter extends EvidenceKeyFilterWhitelist then we can
            // display a list of accepted keys.
            EvidenceKeyFilterWhitelist filter =
                    (EvidenceKeyFilterWhitelist) engine.getEvidenceKeyFilter();
            output.println("Accepted evidence keys:");
            for (Map.Entry<String, Integer> entry : filter.getWhitelist().entrySet()){
                output.println("\t" + entry.getKey());
            }
        }  else {
            output.format("The evidence key filter has type " +
                    "%s. As this does not extend " +
                    "EvidenceKeyFilterWhitelist, a list of accepted values cannot be " +
                    "displayed. As an alternative, you can pass evidence keys to " +
                    "filter.include(string) to see if a particular key will be included " +
                    "or not.\n", engine.getEvidenceKeyFilter().getClass().getName());
            output.println("For example, header.user-agent " +
                    (engine.getEvidenceKeyFilter().include("header.user-agent") ?
                            "is " : "is not ") + "accepted.");
        }
    }

    private static void outputProperties(DeviceDetectionCloudEngine engine, PrintWriter output) {
        Spliterator<AspectPropertyMetaData> spliterator = engine.getProperties().spliterator();
        StreamSupport.stream(spliterator,false)
                .forEach(property-> {
                            // Output some details about the property.
                            // If we're outputting to console then we also add some formatting to make it
                            // more readable.
                            output.format("    Property - %s [Category: %s] (%s)\n",
                                    property.getName(),
                                    property.getCategory(),
                                    property.getType().getName());
                });
    }
}

/*!
 * @example MetadataCloud.java
 *
 * The device detection data file contains meta data that can provide additional information
 * about the various records in the data model.
 * This example shows how to access this data and display the values available.
 *
 * To help navigate the data, it's useful to have an understanding of the types of records that
 * are present:
 * - Component - A record relating to a major aspect of the entity making a web request. There are currently 4 components: Hardware, Software Platform (OS), Browser and Crawler.
 * - Profile - A record containing the details for a specific instance of a component. An example of a hardware profile would be the profile for the iPhone 13. An example of a platform profile would be Android 12.1.0.
 * - Property - Each property will have a specific value (or values) for each profile. An example of a hardware property is 'IsMobile'. An example of a browser property is 'BrowserName'.
 *
 * The example will output each component in turn, with a list of the properties associated with
 * each component. Some of the possible values for each property are also displayed.
 * There are too many profiles to display, so we just list the number of profiles for each
 * component.
 *
 * Finally, the evidence keys that are accepted by device detection are listed. These are the
 * keys that, when added to the evidence collection in flow data, could have some impact on the
 * result returned by device detection.
 *
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-dotnet/blob/master/device-detection.examples/console/src/main/java/fiftyone/devicedetection/examples/console/MetadataCloud.java).
 *
 * @include{doc} example-require-datafile.txt
 */
