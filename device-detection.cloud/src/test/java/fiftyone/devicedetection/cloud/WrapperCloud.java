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

package fiftyone.devicedetection.cloud;

import fiftyone.devicedetection.cloud.flowelements.DeviceDetectionCloudEngine;
import fiftyone.devicedetection.cloud.flowelements.DeviceDetectionCloudEngineBuilder;
import fiftyone.devicedetection.shared.testhelpers.Constants;
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
    	
        String envResourceKey = System.getenv(Constants.RESOURCE_KEY_ENV_VAR);
        String propertyResourceKey = System.getProperty(Constants.RESOURCE_KEY_ENV_VAR);
        String resourceKey = null;
        
        if(envResourceKey == null || envResourceKey.isEmpty()) {
    	if (propertyResourceKey == null || propertyResourceKey.isEmpty())
    		throw new Exception("Resource key is required to run Cloud tests.");
    	else {
    		resourceKey = propertyResourceKey;
    	}
        }
        else {
        	resourceKey = envResourceKey;
        }
    	
        cloudRequestEngine = 
                new CloudRequestEngineBuilder(loggerFactory)
                .setResourceKey(resourceKey)
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