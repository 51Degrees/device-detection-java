/* *********************************************************************
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2023 51 Degrees Mobile Experts Limited, Davidson House,
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

package fiftyone.devicedetection.hash.engine.onpremise;

import fiftyone.devicedetection.shared.testhelpers.UserAgentGenerator;
import fiftyone.devicedetection.shared.testhelpers.FileUtils;
import fiftyone.pipeline.engines.Constants;

import java.io.File;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import static fiftyone.devicedetection.shared.testhelpers.FileUtils.UA_FILE_NAME;
import static fiftyone.pipeline.util.FileFinder.getFilePath;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeTrue;

public class TestsBase {

    private WrapperHash wrapper = null;
    private UserAgentGenerator userAgents;

    protected WrapperHash getWrapper() {
        return wrapper;
    }

    protected UserAgentGenerator getUserAgents() {
        return userAgents;
    }

    protected void testInitialize(Constants.PerformanceProfiles profile) throws Exception {
        File dataFile = FileUtils.getHashFile();
        if (dataFile == null || dataFile.exists() == false) {
            ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
            Logger logger = loggerFactory.getLogger(getClass().getName());
            logger.warn("No Hash data file was available. Test will be skipped.");
        }
        assumeTrue(dataFile != null && dataFile.exists());

        wrapper = new WrapperHash(
            dataFile,
            profile);
        userAgents = new UserAgentGenerator(
            getFilePath(UA_FILE_NAME));
    }

    public void testCleanup() {
        if (wrapper != null) {
            wrapper.close();
        }
    }
}
