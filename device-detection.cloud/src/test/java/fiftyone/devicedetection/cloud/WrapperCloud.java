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

package fiftyone.devicedetection.cloud;

import fiftyone.devicedetection.cloud.flowelements.DeviceDetectionCloudEngine;
import fiftyone.devicedetection.cloud.flowelements.DeviceDetectionCloudEngineBuilder;
import fiftyone.devicedetection.shared.testhelpers.KeyUtils;
import fiftyone.pipeline.cloudrequestengine.flowelements.CloudRequestEngine;
import fiftyone.pipeline.cloudrequestengine.flowelements.CloudRequestEngineBuilder;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.core.flowelements.PipelineBuilder;
import fiftyone.pipeline.engines.data.AspectPropertyMetaData;
import fiftyone.pipeline.engines.flowelements.AspectEngine;

import java.io.Closeable;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

public class WrapperCloud implements Closeable {

    protected static final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
    public Pipeline pipeline;
    public CloudRequestEngine cloudRequestEngine;
    public DeviceDetectionCloudEngine deviceDetectionCloudEngine;

    public WrapperCloud() throws Exception {
        cloudRequestEngine = 
                new CloudRequestEngineBuilder(loggerFactory)
                .setResourceKey(KeyUtils.getNamedKey("TestResourceKey"))
                .build();
        deviceDetectionCloudEngine = new DeviceDetectionCloudEngineBuilder(loggerFactory)
                .build();
        pipeline = new PipelineBuilder(loggerFactory)
                .addFlowElement(cloudRequestEngine)
                .addFlowElement(deviceDetectionCloudEngine)
                .build();
    }
    
    public Iterable<AspectPropertyMetaData> getProperties() {
    	return deviceDetectionCloudEngine.getProperties();
    }

    public Pipeline getPipeline() {
        return pipeline;
    }
    
    @SuppressWarnings("rawtypes")
	public AspectEngine getEngine() {
       return deviceDetectionCloudEngine;
    }

    public void close() {
        try {
            pipeline.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        pipeline = null;
        try {
        	cloudRequestEngine.close();
        	deviceDetectionCloudEngine.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cloudRequestEngine = null;
        deviceDetectionCloudEngine = null;
    }
}