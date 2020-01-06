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

package fiftyone.devicedetection.pattern.engine.onpremise.data;

import fiftyone.devicedetection.pattern.engine.onpremise.flowelements.DeviceDetectionPatternEngine;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.devicedetection.shared.testhelpers.data.DataValidator;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.exceptions.NoValueException;
import fiftyone.pipeline.engines.fiftyone.data.FiftyOneAspectPropertyMetaData;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DataValidatorPattern implements DataValidator {

    private DeviceDetectionPatternEngine engine;

    public DataValidatorPattern(DeviceDetectionPatternEngine engine) {
        this.engine = engine;
    }

    @Override
    public void validateData(FlowData data, boolean validEvidence) throws NoValueException {
        DeviceData elementData = data.getFromElement(engine);
        Map<String, Object> map = elementData.asKeyMap();
        for (FiftyOneAspectPropertyMetaData property :
            engine.getProperties()) {
            if (property.isAvailable()) {
                assertTrue(map.containsKey(property.getName()));
                AspectPropertyValue value = (AspectPropertyValue)map.get(property.getName());
                if (validEvidence) {
                    if (value.hasValue() == false) {
                        int a = 1;
                    }
                    assertTrue(value.hasValue());
                }
                else {
                    if (property.getCategory().equals("Device Metrics")) {
                        assertTrue(value.hasValue());
                    }
                    else {
                        assertFalse(value.hasValue());
                    }
                }

            }
        }
        assertNotNull(elementData.getDeviceId());
        assertFalse(elementData.getDeviceId().getValue().isEmpty());
        if (validEvidence == false) {
            assertEquals("0-0-0-0", elementData.getDeviceId().getValue());
        }
        int validKeys = 0;
        for (String key : data.getEvidence().asKeyMap().keySet()) {
            if (engine.getEvidenceKeyFilter().include(key)) {
                validKeys++;
            }
        }
        assertEquals(validKeys, elementData.getUserAgents().getValue().size());
    }

    @Override
    public void validateProfileIds(FlowData data, List<String> profileIds) throws NoValueException {
        DeviceData elementData = data.getFromElement(engine);
        List<String> matchedProfiles = Arrays.asList(elementData.getDeviceId().getValue().split("-"));
        assertTrue("One or more profiles were not set in the result",
            matchedProfiles.containsAll(profileIds));
    }
}
