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

import com.blueconic.browscap.BrowsCapField;
import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;

import java.util.Arrays;

public class DetectionImplBrowsCap {
    public static final String BROWS_CAP_BLUECONIC = "BrowsCap/Blueconic";

    public static class BrowsCapProperties extends Detection.Properties.Base {

        public BrowsCapProperties(Detection.Request request, Capabilities capabilities) {
            super(request);
            this.isMobile = capabilities.getValue(BrowsCapField.IS_MOBILE_DEVICE);
            this.deviceType = capabilities.getValue(BrowsCapField.DEVICE_TYPE);
            this.hardwareVendor = capabilities.getValue(BrowsCapField.DEVICE_MAKER);
            this.hardwareModel = capabilities.getValue(BrowsCapField.DEVICE_NAME);
            this.browserVendor = capabilities.getValue(BrowsCapField.BROWSER_MAKER);
            this.browserVersion = capabilities.getValue(BrowsCapField.BROWSER_VERSION);
        }

        @Override
        public String getVendorId() {
            return BROWS_CAP_BLUECONIC;
        }

    }

    public static class BrowsCapSolution implements Detection.Solution {
        UserAgentParser parser;
        @Override
        public void initialise(int numberOfThreads) throws Exception {
                   parser = new UserAgentService().loadParser(
                           Arrays.asList(BrowsCapField.BROWSER,
                                   BrowsCapField.BROWSER_TYPE,
                                   BrowsCapField.BROWSER_MAJOR_VERSION,
                                   BrowsCapField.DEVICE_TYPE,
                                   BrowsCapField.PLATFORM,
                                   BrowsCapField.PLATFORM_VERSION,
                                   BrowsCapField.RENDERING_ENGINE_VERSION,
                                   BrowsCapField.RENDERING_ENGINE_NAME,
                                   BrowsCapField.PLATFORM_MAKER,
                                   BrowsCapField.RENDERING_ENGINE_MAKER));
        }

        @Override
        public Detection.Properties detect(Detection.Request request) {
            final Capabilities capabilities = parser.parse(request.getEvidence().get("header.user-agent"));
            return new BrowsCapProperties(request, capabilities);
        }

        @Override
        public String getVendorId() {
            return DetectionImplBrowsCap.BROWS_CAP_BLUECONIC;
        }

        @Override
        public void close() {
        }
    }
}
