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

import fiftyone.devicedetection.examples.ExampleBase;
import fiftyone.devicedetection.examples.ProgramBase;
import fiftyone.devicedetection.pattern.engine.onpremise.flowelements.DeviceDetectionPatternEngine;
import fiftyone.devicedetection.pattern.engine.onpremise.flowelements.DeviceDetectionPatternEngineBuilder;
import fiftyone.pipeline.engines.Constants;
import fiftyone.pipeline.engines.fiftyone.data.FiftyOneAspectPropertyMetaData;
import fiftyone.pipeline.engines.fiftyone.data.ProfileMetaData;
import fiftyone.pipeline.engines.fiftyone.data.ValueMetaData;

import java.util.ArrayList;
import java.util.List;

public class DynamicFilters extends ProgramBase {


    public static void main(String[] args) throws Exception {
        String dataFile = args.length > 0 ? args[0] :
            getDefaultFilePath("51Degrees-LiteV3.2.dat").getAbsolutePath();

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

        /**
         * Converts Iterable list of 51Degrees signatures to an ArrayList of
         * signatures.
         *
         * @param <E>  generic.
         * @param iter Iterable to convert to ArrayList.
         * @return ArrayList of 51Degrees signatures.
         */
        public static <E> ArrayList<E> iterableToArrayList(Iterable<E> iter) {
            ArrayList<E> list = new ArrayList<E>();
            for (E item : iter) {
                list.add(item);
            }
            return list;
        }

        public void run(String dataFile) throws Exception {
            println("Constructing engine from file " + dataFile);
            try (DeviceDetectionPatternEngine engine =
                     new DeviceDetectionPatternEngineBuilder()
                         .setPerformanceProfile(Constants.PerformanceProfiles.HighPerformance)
                         .setAutoUpdate(false)
                         .build(dataFile, false)) {

                List<ProfileMetaData> profiles = iterableToArrayList(engine.getProfiles());

                println("Total number of profiles in the data file: " +
                    profiles.size());

                ArrayList<ProfileMetaData> subsetOfProfiles =
                    filterBy(engine,
                        "IsMobile", "True", profiles);
                println("Profiles after removing non-mobile devices: " +
                    subsetOfProfiles.size());

                subsetOfProfiles =
                    filterBy(engine,
                        "ScreenPixelsWidth", "1080", subsetOfProfiles);
                println("Profiles after removing non 1080 width screens: " +
                    subsetOfProfiles.size());
            }
        }

        /**
         * Filters the provided set of signatures to only return those, where the
         * specified property is equal to the specified value. For example: calling
         * this function with "IsMobile","True",null will return a subset of
         * signatures where the IsMobile property evaluates to "True".
         * <p>
         * After checking for valid input method, iterates through the provided list
         * of signatures to check if the required property of the current signature
         * has the required value. If so, a signature is added to the temporary list
         * that gets returned at the end.
         * <p>
         * If the signature list was not provided, a complete list of signatures
         * available in the data file will be used.
         *
         * @param propertyName  String containing name of the property to check for,
         *                      not null.
         * @param propertyValue String with value that the required property must
         *                      evaluate to, not null.
         * @param listToFilter  an ArrayList of signatures to perform filtering on.
         *                      If null the entire set of signatures will be used.
         * @return Subset of signatures where property equals value.
         */
        public ArrayList<ProfileMetaData> filterBy(
            DeviceDetectionPatternEngine engine,
            String propertyName,
            String propertyValue,
            List<ProfileMetaData> listToFilter) {
            if (propertyName.isEmpty() || propertyValue.isEmpty()) {
                throw new IllegalArgumentException("Property and Value can not be "
                    + "empty or null.");
            }
            FiftyOneAspectPropertyMetaData property = engine.getProperty(propertyName);
            if (property == null) {
                throw new IllegalArgumentException("Property you requested " +
                    propertyName + " does not appear to exist in the current "
                    + "data file.");
            }
            ArrayList<ProfileMetaData> filterResults = new ArrayList<>();
            for (ProfileMetaData profile : listToFilter) {
                ValueMetaData value = profile.getValue(propertyName, propertyValue);
                if (value != null) {
                    filterResults.add(profile);
                }
            }
            return filterResults;
        }
    }
}
