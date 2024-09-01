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

package fiftyone.devicedetection.shared.testhelpers;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Helper class to identify the test being run by maven surefire in the log output. This
 * is particularly useful if there is a lot of "chatty" testing going on.
 * <p>
 * See: <a href="https://maven.apache.org/surefire/maven-surefire-plugin/examples/junit.html#Using_Custom_Listeners_and_Reporters">Using Custom Listeners and Reporters</a>
 */
@RunListener.ThreadSafe
public class MavenRunListener extends RunListener {
    static Logger logger = LoggerFactory.getLogger(MavenRunListener.class);
    static Marker marker = MarkerFactory.getMarker("Starting Test");
    static Marker endMarker = MarkerFactory.getMarker("Finished Test");
    static Marker failMarker = MarkerFactory.getMarker("Failed Test");
    static Marker assumeFailMarker = MarkerFactory.getMarker("Assumption Failed");
    static Marker ignoredMarker = MarkerFactory.getMarker("Ignored Test");

    @Override
    public void testStarted(Description description) throws Exception {
        super.testStarted(description);
        logger.info(marker, "{}", description.getDisplayName());
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        super.testFailure(failure);
        logger.error(failMarker, "{} {}", failure.getMessage(), failure.getDescription().getDisplayName());
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        super.testAssumptionFailure(failure);
        logger.warn(assumeFailMarker, "{} {}", failure.getMessage(), failure.getDescription().getDisplayName());
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        super.testIgnored(description);
        logger.info(ignoredMarker, "{}", description.getDisplayName());
    }

    @Override
    public void testFinished(Description description) throws Exception {
        super.testFinished(description);
        logger.info(endMarker, "Finished {}", description.getDisplayName());
    }
}
