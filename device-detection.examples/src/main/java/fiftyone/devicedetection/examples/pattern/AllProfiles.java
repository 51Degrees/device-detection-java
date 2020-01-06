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
import fiftyone.devicedetection.pattern.engine.onpremise.flowelements.DeviceDetectionPatternEngine;
import fiftyone.pipeline.core.data.types.JavaScript;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.Constants;
import fiftyone.pipeline.engines.data.AspectPropertyMetaData;
import fiftyone.pipeline.engines.fiftyone.data.ComponentMetaData;
import fiftyone.pipeline.engines.fiftyone.data.FiftyOneAspectPropertyMetaData;
import fiftyone.pipeline.engines.fiftyone.data.ProfileMetaData;
import fiftyone.pipeline.engines.fiftyone.data.ValueMetaData;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import static fiftyone.pipeline.util.StringManipulation.stringJoin;

public class AllProfiles extends ProgramBase {

    private static String getValuesForDisplay(Iterable<ValueMetaData> values) {
        List<String> strings = new ArrayList<>();
        if (values != null) {
            for (ValueMetaData value : values) {
                strings.add(value.getName());
            }
        } else {
            strings.add("N/A");
        }
        return stringJoin(strings, "|");
    }

    public static void main(String[] args) throws Exception {
        String dataFile = args.length > 0 ? args[0] :
            getDefaultFilePath("51Degrees-LiteV3.2.dat").getAbsolutePath();

        new Example(true).run(dataFile, Example.outputFilePath);
        System.out.println("Complete. Press enter to exit.");
        System.in.read();
    }

    public static class Example extends ExampleBase {
        // Output file in current working directory.
        public static final String outputFilePath = "allProfilesOutput.csv";
        ComponentMetaData hardwareComponent;
        Iterable<FiftyOneAspectPropertyMetaData> hardwareProperties;
        List<ProfileMetaData> hardwareProfiles = new ArrayList<>();

        public Example(boolean printOutput) {
            super(printOutput);
        }

        public void run(String dataFile, String outputFilename) throws Exception {
            DeviceDetectionPipelineBuilder builder = new DeviceDetectionPipelineBuilder();
            println("Constructing pipeline with engine " +
                "from file " + dataFile);
            // Create a simple pipeline to access the engine with.
            try (Pipeline pipeline = builder
                .useOnPremise(dataFile, false)
                .setAutoUpdate(false)
                // Prefer low memory profile where all data streamed
                // from disk on-demand. Experiment with other profiles.
                .setPerformanceProfile(Constants.PerformanceProfiles.HighPerformance)
                //.setPerformanceProfile(Constants.PerformanceProfiles.LowMemory)
                //.setShareUsage(false)
                //.setPerformanceProfile(Constants.PerformanceProfiles.Balanced)
                .build()) {

                DeviceDetectionPatternEngine engine =
                    pipeline.getElement(DeviceDetectionPatternEngine.class);

                // Get the Hardware properties.
                for (ComponentMetaData component : engine.getComponents()) {
                    if (component.getName().contains("Hardware")) {
                        hardwareComponent = component;
                        hardwareProperties = component.getProperties();
                        break;
                    }
                }

                // Get the Hardware profiles
                for (ProfileMetaData profile : engine.getProfiles()) {
                    if (profile.getComponent().equals(hardwareComponent)) {
                        hardwareProfiles.add(profile);
                    }
                }

                try (FileWriter fileWriter = new FileWriter(outputFilename)) {
                    // Write the headers for the CSV file.
                    fileWriter.append("Id");
                    for (AspectPropertyMetaData property : hardwareProperties) {
                        fileWriter.append(",").append(property.getName());
                    }
                    fileWriter.append("\n");

                    // Loop over all devices.
                    for (ProfileMetaData profile : hardwareProfiles) {
                        // Write the device's profile id.
                        fileWriter.append(Integer.toString(profile.getProfileId()));
                        for (AspectPropertyMetaData property : hardwareProperties) {
                            // Get some property values from the match
                            Iterable<ValueMetaData> values = profile.getValues(property.getName());
                            // Prevents big chunks of javascript overrides from
                            // being written.
                            if (property.getType().equals(JavaScript.class)) {
                                values = null;
                            }
                            // Write result to file
                            fileWriter.append(",").append(getValuesForDisplay(values));
                        }
                        fileWriter.append("\n").flush();
                    }
                }
                println("Output written to " + outputFilename);
            }
        }
    }
}
