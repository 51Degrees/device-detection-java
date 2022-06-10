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
 * Uncomment the following code blocks to test with DeviceAtlas. The code
 * will need modification as the comparison has not been tested with the
 * real DeviceAtlas API. The code has been constructed based on the
 * documentation available on the DeviceAtlas web site at the following
 * location.
 * <p>
 * https://docs.deviceatlas.com/apis/enterprise/java/2.4.5/README.DeviceApi.html
 * <p>
 * It will also be necessary to add DeviceAtlas specific dependencies to the POM.xml
 */


@SuppressWarnings({"unused", "CommentedOutCode"})
public class DetectionImplDeviceAtlas {
    public static final String DEVICE_ATLAS = "Device Atlas";

    public static class DeviceAtlasProperties extends Detection.Properties.Base {

        public DeviceAtlasProperties(Detection.Request request
                // , Properties properties // add the DeviceAtlas Properties class as a param
                ) {
            super(request);
            /*
            this.isMobile = properties.get("mobileDevice").asBoolean();
            this.hardwareVendor = properties.get("vendor").asString();
            this.hardwareModel = properties.get("model").asString();
            this.deviceType = properties.get("primaryHardwareType").asString();
             */
            // this.browserVendor = //??
            // this.browserVersion = //??
        }

        @Override
        public String getVendorId() {
            return DEVICE_ATLAS;
        }
    }

    public static class DeviceAtlasSolution implements Detection.Solution {
        //private DeviceApi da;
        @Override
        public void initialise(int numberOfThreads) {
           /*
             this.da = new DeviceApi();
             this.da.loadDataFromFile(dataFile);
             */

            // Some configuration of a User-Agent cache may be possible to
            // improve performance in the second pass.
        }

        @Override
        public Detection.Properties detect(Detection.Request request) {
             /*
             // this doesn't deal with UACH values:
             Properties properties = this.da.getProperties(userAgent);
             // or possibly, where request is a servlet request
             Properties properties = this.da.getProperties(request);
             return new DeviceAtlasProperties(request, properties);
             */
            return null; // placeholder for above

       }

        @Override
        public String getVendorId() {
            return DetectionImplDeviceAtlas.DEVICE_ATLAS;
        }

        @Override
        public void close() {
            /*
            da = null;
             */
        }
    }
}
