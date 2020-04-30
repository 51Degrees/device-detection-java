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

import fiftyone.devicedetection.examples.ExampleBase;
import fiftyone.devicedetection.examples.ProgramBase;
import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngineBuilder;
import fiftyone.pipeline.engines.Constants;
import fiftyone.pipeline.engines.fiftyone.data.FiftyOneAspectPropertyMetaData;

/**
 * @example hash/MetaData.java
 *
 * Metadata example of using 51Degrees device detection.
 *
 * The example shows how to:
 *
 * 1. Build a new on-premise Hash engine with the low memory performance profile.
 * ```
 * DeviceDetectionHashEngine engine = new DeviceDetectionHashEngineBuilder()
 *     .setAutoUpdate(false)
 *     .setPerformanceProfile(Constants.PerformanceProfiles.LowMemory)
 *     .build("51Degrees-LiteV4.1.hash", false);
 * ```
 *
 * 2. Iterate over all properties in the data file, printing the name, value type,
 * and description for each one.
 * ```
 * for (FiftyOneAspectPropertyMetaData property : engine.getProperties()) {
 *     printf("%s (%s) - %s%n",
 *         property.getName(),
 *         property.getType().getSimpleName(),
 *         property.getDescription());
 * }
 * ```
 */

public class MetaData extends ProgramBase {

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

        // truncate value if it contains newline (esp for the JavaScript property)
        private String truncateToNl(String s) {
            int i = s.indexOf('\n', 3);
            if (i == -1) {
                return s;
            }
            return s.substring(0, i + 2) + " ...";
        }

        public void run(String dataFile) throws Exception {
            println("Constructing pipeline with engine " +
                "from file " + dataFile);
            try (DeviceDetectionHashEngine engine =
                     new DeviceDetectionHashEngineBuilder()
                         .setAutoUpdate(false)
                         // Prefer low memory profile where all data streamed
                         // from disk on-demand. Experiment with other profiles.
                         //.setPerformanceProfile(ConstantsPerformanceProfiles.HighPerformance)
                         .setPerformanceProfile(Constants.PerformanceProfiles.LowMemory)
                         //.setPerformanceProfile(ConstantsPerformanceProfiles.Balanced)
                         .build(dataFile, false)) {
                for (FiftyOneAspectPropertyMetaData property :
                    engine.getProperties()) {

                    printf("%s (%s) - %s%n",
                        property.getName(),
                        property.getType().getSimpleName(),
                        property.getDescription());
                }
            }
        }
    }
}
