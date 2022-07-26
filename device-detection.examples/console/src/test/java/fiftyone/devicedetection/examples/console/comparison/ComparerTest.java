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

import fiftyone.common.testhelpers.LogbackHelper;
import fiftyone.pipeline.util.FileFinder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;

import static fiftyone.devicedetection.examples.console.comparison.Comparer.*;

import static org.junit.Assume.assumeFalse;
public class ComparerTest {
    static Logger logger = LoggerFactory.getLogger(ComparerTest.class);
    @Test
    public void TestCompare() throws Exception {
        LogbackHelper.configureLogback(FileFinder.getFilePath("logback.xml"));
        try {
            new Comparer().compare(Arrays.asList(DEFAULT_SOLUTIONS),
                    DEFAULT_NUMBER_OF_THREADS,
                    DEFAULT_NUMBER_OF_RESULTS,
                    new Reporting.Minimal(),
                    new PrintWriter(System.out, true));
        } catch (FileNotFoundException e){
            // this test is optional - requires Enterprise data file, skip on error
            logger.warn(e.getMessage());
            assumeFalse("Skipping test", true);
        }
    }
}