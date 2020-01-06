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

package fiftyone.devicedetection.pattern.engine.onpremise.flowelements;

import fiftyone.devicedetection.pattern.engine.onpremise.TestsBase;
import fiftyone.devicedetection.pattern.engine.onpremise.data.DataValidatorPattern;
import fiftyone.devicedetection.shared.testhelpers.flowelements.ProcessTests;
import fiftyone.pipeline.engines.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Process extends TestsBase {

    @Before
    public void init() throws Exception {
        testInitialize(Constants.PerformanceProfiles.HighPerformance);
    }

    @After
    public void cleanup() {
        testCleanup();
    }

    @Test
    public void Process_Pattern_NoEvidence() throws Exception {
        ProcessTests.noEvidence(getWrapper(), new DataValidatorPattern(getWrapper().getEngine()));
    }

    @Test
    public void Process_Pattern_EmptyUserAgent() throws Exception {
        ProcessTests.emptyUserAgent(getWrapper(), new DataValidatorPattern(getWrapper().getEngine()));
    }

    @Test
    public void Process_Pattern_NoHeaders() throws Exception {
        ProcessTests.noHeaders(getWrapper(), new DataValidatorPattern(getWrapper().getEngine()));
    }

    @Test
    public void Process_Pattern_NoUsefulHeaders() throws Exception {
        ProcessTests.noUsefulHeaders(getWrapper(), new DataValidatorPattern(getWrapper().getEngine()));
    }

    @Test
    public void Process_Pattern_CaseInsensitiveKeys() throws Exception {
        ProcessTests.caseInsensitiveEvidenceKeys(getWrapper(), new DataValidatorPattern(getWrapper().getEngine()));
    }

    @Test
    public void Process_Pattern_ProfileOverride() throws Exception {
        ProcessTests.profileOverride(getWrapper(), new DataValidatorPattern(getWrapper().getEngine()));
    }

    @Test
    public void Process_Pattern_ProfileOverrideNoHeaders() throws Exception {
        ProcessTests.profileOverrideNoHeaders(getWrapper(), new DataValidatorPattern(getWrapper().getEngine()));
    }

    @Test
    public void Process_Pattern_DeviceId() throws Exception {
        ProcessTests.deviceId(getWrapper(), new DataValidatorPattern(getWrapper().getEngine()));
    }
}
