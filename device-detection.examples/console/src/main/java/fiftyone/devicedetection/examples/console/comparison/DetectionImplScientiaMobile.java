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

package fiftyone.devicedetection.examples.console.comparison;


/**
 * Uncomment the following code blocks to test with WURFL. The code will
 * need modification as the comparison has not been tested with the real
 * WURFL API. The code has been constructed based on the documentation
 * available on the ScientiaMobile web site at the following location.
 * <p>
 * https://docs.scientiamobile.com/documentation/onsite/onsite-java-api
 * <p>
 * It will also be necessary to add WURFL specific dependencies to the POM.xml
 */

@SuppressWarnings({"unused", "CommentedOutCode"})
public class DetectionImplScientiaMobile {
    public static final String WURFL = "WURFL";

    public static class WurflProperties extends Detection.Properties.Base {


        public WurflProperties(Detection.Request request
                // ,  Device device // to add WURFL Device parameter
                ) {
            super(request);
/*
            result.isMobile = device.getCapabilityAsBool("is_wireless_device");
            result.hardwareVendor = device.getCapability("brand_name");
            result.hardwareModel = device.getCapability("model_name");
            result.deviceType = device.getCapability("form_factor");
*/
            // this.browserVendor = //?
            // this.browserVersion = //?
        }

        @Override
        public String getVendorId() {
            return WURFL;
        }
    }

    public static class WurflSolution implements Detection.Solution {
        // private GeneralWURFLEngine wurfl;
        @Override
        public void initialise(int numberOfThreads) {
            /*
             this.wurfl = new GeneralWURFLEngine(dataFile);
             // load method is available on API version 1.8.1.0 and above
             wurfl.load();
             */
        }

        @Override
        public Detection.Properties detect(Detection.Request request) {
/*
               // it doesn't look as though you can pass UACH headers ...
            Device device = this.wurfl.getDeviceForRequest(userAgent);
            return new WurflProperties(request, device);
*/
            return null; // to remove
        }

        @Override
        public String getVendorId() {
            return DetectionImplScientiaMobile.WURFL;
        }

        @Override
        public void close() {
            //this.wurfl = null;
        }
    }
}
