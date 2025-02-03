/* *********************************************************************
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2025 51 Degrees Mobile Experts Limited, Davidson House,
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

package fiftyone.devicedetection.hash.engine.onpremise.data;

import fiftyone.devicedetection.hash.engine.onpremise.TestsBase;
import fiftyone.devicedetection.shared.testhelpers.Wrapper;
import fiftyone.devicedetection.shared.testhelpers.data.ValueTests;
import fiftyone.pipeline.core.data.ElementData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.devicedetection.hash.engine.onpremise.flowelements.Constants;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.fiftyone.data.FiftyOneAspectPropertyMetaData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ValueHashTests extends TestsBase {

    @Before
    public void init() throws Exception {
        testInitialize(fiftyone.pipeline.engines
        	.Constants.PerformanceProfiles.HighPerformance);
    }

    @After
    public void cleanup() {
        testCleanup();
    }

    private void validateDescription(FiftyOneAspectPropertyMetaData metaData) {
    	String actual = metaData.getDescription();
    	String expected = "";
    	String propertyName = metaData.getName();
    	switch (propertyName) {
    	case Constants.MatchMetrics.MATCHED_NODES:
    		expected = Constants.MatchMetrics.MATCHED_NODES_DESCRIPTION;
    		break;
    	case Constants.MatchMetrics.DIFFERENCE:
    		expected = Constants.MatchMetrics.DIFFERENCE_DESCRIPTION;
    		break;
    	case Constants.MatchMetrics.DRIFT:
    		expected = Constants.MatchMetrics.DRIFT_DESCRIPTION;
    		break;
    	case Constants.MatchMetrics.DEVICE_ID:
    		expected = Constants.MatchMetrics.DEVICE_ID_DESCRIPTION;
    		break;
    	case Constants.MatchMetrics.USER_AGENTS:
    		expected = Constants.MatchMetrics.USER_AGENTS_DESCRIPTION;
    		break;
    	case Constants.MatchMetrics.METHOD:
    		expected = Constants.MatchMetrics.METHOD_DESCRIPTION;
    		break;
    	case Constants.MatchMetrics.ITERATIONS:
    		expected = Constants.MatchMetrics.ITERATIONS_DESCRIPTION;
    		break;
    	default:
    		fail(propertyName + " is not a match metric property.");
    		break;
    	}
    	
    	assertFalse("Description should not be empty", actual.isEmpty());
    	assertEquals(
    		String.format("Expected \"%s\" but get \"%s\"",
    			expected, actual), expected, actual);
    }
    
    @Test
    public void Values_Hash_MatchMetricsDescriptionByIndex() throws Exception {
    	// Matched nodes
    	validateDescription(getWrapper().getEngine().getProperty(
    		Constants.MatchMetrics.MATCHED_NODES));
    	// Difference
    	validateDescription(getWrapper().getEngine().getProperty(
    		Constants.MatchMetrics.DIFFERENCE));
        // Drift
    	validateDescription(getWrapper().getEngine().getProperty(
    		Constants.MatchMetrics.DRIFT));   
       	// Device ID
    	validateDescription(getWrapper().getEngine().getProperty(
    		Constants.MatchMetrics.DEVICE_ID));
        // User Agents
    	validateDescription(getWrapper().getEngine().getProperty(
    		Constants.MatchMetrics.USER_AGENTS));
        // Method
    	validateDescription(getWrapper().getEngine().getProperty(
    		Constants.MatchMetrics.METHOD));
        // Iterations
    	validateDescription(getWrapper().getEngine().getProperty(
    		Constants.MatchMetrics.ITERATIONS));
    }
    
    @Test
    public void Values_Hash_MatchMetricsDescription() throws Exception {
    	final AtomicInteger counter = new AtomicInteger(0);
    	getWrapper()
    		.getEngine()
    		.getProperties()
    		.stream()
    		.filter(m -> m.getCategory().equals("Device Metrics"))
    		.forEach(m -> {
    			validateDescription(m);
    			counter.incrementAndGet();
    		});
    	assertTrue("Match metrics properties are not present", counter.get() > 0);
    }
    
    @Test
    public void Values_Hash_ValueTypes() throws Exception {
        ValueTests.valueTypes(getWrapper());
    }

    @Test
    public void Values_Hash_AvailableProperties() throws Exception {
        ValueTests.availableProperties(getWrapper());
    }

    @Test
    public void Values_Hash_TypedGetters() throws Exception {
        ValueTests.typedGetters(getWrapper());
    }

    @Test
    public void Values_Hash_DeviceId() throws Exception {
        ValueTests.deviceId(getWrapper());
    }

    @Test
    public void Values_Hash_MatchedUserAgents() throws Exception {
        ValueTests.matchedUserAgents(getWrapper());
    }
}
