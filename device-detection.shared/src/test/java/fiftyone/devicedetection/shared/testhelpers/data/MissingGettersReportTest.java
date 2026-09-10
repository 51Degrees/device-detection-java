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

package fiftyone.devicedetection.shared.testhelpers.data;

import org.junit.Test;
import org.junit.AssumptionViolatedException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fiftyone.devicedetection.shared.testhelpers.data.ValueTests.missingGettersMessage;
import static fiftyone.devicedetection.shared.testhelpers.data.ValueTests.reportMissingGetters;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the way {@link ValueTests#typedGetters} reports a data file property that
 * has no strongly typed getter on the DeviceData class.
 * <p>
 * This condition used to fail the build. Because the getters are generated from the
 * 51Degrees metadata service and the properties come from the data file, the two can
 * be out of step for days at a time through no fault of this repository, which is
 * what kept the nightly pipeline red from 2026-09-02 to 2026-09-10. See issue #584.
 */
public class MissingGettersReportTest {

    private static final List<String> TWO_MISSING_PROPERTIES =
        Arrays.asList("HasWebDriver", "IsHeadless");

    /**
     * A realistic number of properties that did have a typed getter, so the
     * reports below are exercising the upstream lag and not a broken API.
     */
    private static final int VERIFIED_GETTERS = 200;

    /**
     * A property with no typed getter must be reported as an assumption failure so
     * the test is skipped, rather than an assertion failure that fails the build.
     */
    @Test
    public void missingGetterIsReportedAsAnAssumptionFailure() {
        try {
            reportMissingGetters(TWO_MISSING_PROPERTIES, VERIFIED_GETTERS);
            fail("A missing typed getter should have been reported.");
        } catch (AssumptionViolatedException assumptionFailure) {
            assertTrue(
                "The assumption failure should name the properties that need " +
                    "generating, but the message was '" +
                    assumptionFailure.getMessage() + "'.",
                assumptionFailure.getMessage().contains("HasWebDriver"));
        }
    }

    /**
     * Nothing is reported when every property has a typed getter, which is the
     * normal state once the generated accessors have caught up with the data file.
     */
    @Test
    public void noMissingGettersReportsNothing() {
        reportMissingGetters(Collections.<String>emptyList(), VERIFIED_GETTERS);
    }

    /**
     * The joined property names must be separated from the word that follows them.
     * Without the space the report read "IsHeadlessare missing getters", which ran
     * the last property name into the sentence and hid it from anyone reading the
     * build log.
     */
    @Test
    public void multiplePropertiesAreSeparatedFromTheFollowingWord() {
        String message = missingGettersMessage(TWO_MISSING_PROPERTIES);

        assertTrue(
            "The property list should be followed by ' are missing getters', but " +
                "the message was '" + message + "'.",
            message.contains("HasWebDriver, IsHeadless are missing getters"));
    }

    /**
     * A single missing property is reported in the singular, naming just that one.
     */
    @Test
    public void oneMissingPropertyIsReportedInTheSingular() {
        String message = missingGettersMessage(
            Collections.singletonList("IsHeadless"));

        assertTrue(
            "A single missing property should be named on its own, but the " +
                "message was '" + message + "'.",
            message.contains("The property 'IsHeadless' is missing a getter"));
    }

    /**
     * If not one property has a typed getter then the generated accessors are
     * broken rather than merely behind the data file, which is this repository's
     * own defect. That must still fail the build, or the leniency above would let
     * a wholesale regression of DeviceData report as a green run.
     */
    @Test
    public void noVerifiedGettersAtAllStillFailsTheBuild() {
        // Captured rather than asserted inside the try, because fail() raises an
        // AssertionError that the catch below would otherwise swallow and report
        // in place of the real one.
        AssertionError hardFailure = null;
        try {
            reportMissingGetters(TWO_MISSING_PROPERTIES, 0);
        } catch (AssumptionViolatedException assumptionFailure) {
            fail("Losing every typed getter should have failed the test, not " +
                "been skipped as an upstream lag.");
        } catch (AssertionError assertionFailure) {
            hardFailure = assertionFailure;
        }

        assertNotNull(
            "Losing every typed getter should have failed the test.",
            hardFailure);
        assertTrue(
            "The failure should explain that the accessors are broken, but the " +
                "message was '" + hardFailure.getMessage() + "'.",
            hardFailure.getMessage().contains("missing or broken"));
    }

    /**
     * The message builder is only meaningful for a non-empty list; an empty one
     * used to fall through to the plural wording and name no properties at all.
     */
    @Test
    public void describingAnEmptyListIsRejected() {
        try {
            missingGettersMessage(Collections.<String>emptyList());
            fail("Describing an empty list should have been rejected.");
        } catch (IllegalArgumentException expected) {
            // The guard is the behaviour under test, so reaching here is a pass.
        }
    }
}
