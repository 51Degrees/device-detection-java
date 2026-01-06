/* *********************************************************************
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2026 51 Degrees Mobile Experts Limited, Davidson House,
 * Forbury Square, Reading, Berkshire, United Kingdom RG1 3EU.
 *
 * This Original Work is licensed under the European Union Public Licence
 * (EUPL) v.1.2 and is subject to its terms as set out below.
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

package fiftyone.devicedetection.cloud.data;

import fiftyone.devicedetection.cloud.TestsBase;
import fiftyone.devicedetection.cloud.ValueTests;
import fiftyone.devicedetection.shared.testhelpers.UserAgentGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ValueCloudTests extends TestsBase {

    	private UserAgentGenerator userAgents;
    	
        @Before
        public void init() throws Exception {
            testInitialize();
        }

        @After
        public void cleanup() {
            testCleanup();
        }

        @Test
        public void ValueTests_Cloud_ValueTypes() throws Exception {
            ValueTests.valueTypes(getWrapper());
        }

        @Test
        public void ValueTests_Cloud_AvailableProperties() throws Exception {
            ValueTests.availableProperties(getWrapper());
        }

        @Test
        public void ValueTests_Cloud_TypedGetters() throws Exception {
            ValueTests.typedGetters(getWrapper());
        }

        @Test
        public void ValueTests_Cloud_DeviceId() throws Exception {
            ValueTests.deviceId(getWrapper());
        }

        @Test
        public void ValueTests_Cloud_MatchedUserAgents() throws Exception {
            ValueTests.matchedUserAgents(getWrapper());
        }
    }

