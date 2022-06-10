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

import fiftyone.devicedetection.cloud.data.MultiDeviceDataCloud;
import fiftyone.devicedetection.shared.Constants;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.devicedetection.shared.testhelpers.KeyUtils;
import fiftyone.pipeline.core.configuration.PipelineOptions;
import fiftyone.pipeline.core.configuration.PipelineOptionsFactory;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.fiftyone.flowelements.FiftyOnePipelineBuilder;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import static fiftyone.common.testhelpers.LogbackHelper.configureLogback;
import static fiftyone.devicedetection.examples.shared.PropertyHelper.asString;
import static fiftyone.devicedetection.examples.shared.KeyHelper.*;
import static fiftyone.pipeline.util.FileFinder.getFilePath;

/**
 * This example demonstrates looking up device details using a TAC code.
 * <p>
 * Unlike other examples, use of this example requires a license key which can be purchased from our
 * <a href="http://51degrees.com/pricing">pricing page</a>. Once this is done, a resource key with the
 * properties required by this example can be created
 * <a href="https://configure.51degrees.com/QKyYH5XT">here</a>.
 * <p>
 * This resource key can be supplied as a command line argument to this program, as an environment
 * variable "SuperResourceKey", as a System Property "SuperResourceKey" or by editing the config
 * file tacCloud.xml.
 */
public class TacCloud {
    // Example values to use when looking up device details from TACs.
    static final String TAC_1 = "35925406";
    static final String TAC_2 = "86386802";
    public static final String TAC_EXAMPLE_RESOURCE_KEY_NAME = "SuperResourceKey";

    public static void main (String[] args) throws Exception {
        configureLogback(getFilePath("logback.xml"));
        String resourceKey = args.length > 0 ? args[0] : null;
        run(resourceKey, System.out);
    }

    public static void run(String resourceKey, OutputStream os) throws Exception {
        // In this example, we use the FiftyOnePipelineBuilder and configure it from a file.
        // For a demonstration of how to do this in code instead, see the
        // NativeModelLookup example.
        // For more information about builders in general see the documentation at
        // http://51degrees.com/documentation/_concepts__configuration__builders__index.html

        // the configuration file is in the resources directory
        File optionsFile = getFilePath("tacCloud.xml");
        // load the options and if no resource key has been set in the file
        // use the one supplied to this method
        PipelineOptions pipelineOptions = PipelineOptionsFactory.getOptionsFromFile(optionsFile);
        String fileResourceKey = pipelineOptions.find("CloudRequestEngine", "ResourceKey");
        if (KeyUtils.isInvalidKey(fileResourceKey)) {
            getOrSetSuperResourceKey(resourceKey, TAC_EXAMPLE_RESOURCE_KEY_NAME);
        }
        try (PrintWriter writer = new PrintWriter(os)) {
            writer.println("This example shows the details of devices " +
                    "associated with a given 'Type Allocation Code' or 'TAC'.");
            writer.println("More background information on TACs can be " +
                    "found through various online sources such as Wikipedia: " +
                    "https://en.wikipedia.org/wiki/Type_Allocation_Code");
            writer.println("----------------------------------------");



            // Create the pipeline using the service provider and the configured options.
            try (Pipeline pipeline = new FiftyOnePipelineBuilder()
                    .buildFromConfiguration(pipelineOptions)) {
                // Pass a TAC into the pipeline and list the matching devices.
                analyseTac(TAC_1, pipeline, writer);
                analyseTac(TAC_2, pipeline, writer);
            }
        }
    }

    static void analyseTac(String tac, Pipeline pipeline, PrintWriter output) throws Exception {
        // Create the FlowData instance.
        try (FlowData data = pipeline.createFlowData()) {
            // Add the TAC as evidence.
            data.addEvidence(Constants.EVIDENCE_QUERY_TAC_KEY, tac);
            // Process the supplied evidence.
            data.process();
            // Get result data from the flow data.
            MultiDeviceDataCloud result = data.get(MultiDeviceDataCloud.class);
            // The 'MultiDeviceDataCloud' object contains one or more instances
            // implementing 'DeviceData' which is the same interface used for standard device
            // detection, so we have access to all the same properties.
            output.format("Which devices are associated with the TAC '%s'?%n", tac);
            for (DeviceData device : result.getProfiles()) {
                AspectPropertyValue<String> vendor = device.getHardwareVendor();
                AspectPropertyValue<List<String>> name = device.getHardwareName();
                AspectPropertyValue<String> model = device.getHardwareModel();

                // output the result, taking care to deal with "no value" cases
                output.format("\t%s; %s; %s%n", asString(vendor), asString(name), asString(model));
            }
        }
    }
}
/*!
 * @example console/TacCloud.java
 *
 * This example shows how to use the 51Degrees Cloud service to look up the details of a device
 * based on a given 'TAC'. More background information on TACs can be found through various online
 * sources such as <a href="https://en.wikipedia.org/wiki/Type_Allocation_Code">Wikipedia</a>.
 *
 * Unlike other examples, use of this example requires a license key which can be purchased from our
 * [pricing page](//51degrees.com/pricing). Once this is done, a resource key with the
 * properties required by this example can be created [here](//configure.51degrees.com/QKyYH5XT).
 *
 * This example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/blob/master/device-detection.examples/console/src/main/java/fiftyone/devicedetection/examples/console/TacCloud.java).
 *
 * @include{doc} example-require-resourcekey.txt
 */