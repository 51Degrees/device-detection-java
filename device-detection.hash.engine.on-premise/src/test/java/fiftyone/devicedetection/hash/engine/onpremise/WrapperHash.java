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

import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngineBuilder;
import fiftyone.devicedetection.shared.testhelpers.Wrapper;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.core.flowelements.PipelineBuilder;
import fiftyone.pipeline.engines.Constants;
import fiftyone.pipeline.engines.fiftyone.data.ComponentMetaData;
import fiftyone.pipeline.engines.fiftyone.data.FiftyOneAspectPropertyMetaData;
import fiftyone.pipeline.engines.fiftyone.data.ProfileMetaData;
import fiftyone.pipeline.engines.fiftyone.data.ValueMetaData;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import java.io.File;

public class WrapperHash implements Wrapper {

    protected static final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
    private Pipeline pipeline;
    private DeviceDetectionHashEngine engine;

    public WrapperHash(File dataFile, Constants.PerformanceProfiles profile) throws Exception {
        engine = new DeviceDetectionHashEngineBuilder(loggerFactory, null)
                .setPerformanceProfile(profile)
                .setUpdateMatchedUserAgent(true)
                .setAutoUpdate(false)
                .build(dataFile.toString(), false);
        pipeline = new PipelineBuilder(loggerFactory)
                .addFlowElement(engine)
                .build();
    }

    @Override
    public Pipeline getPipeline() {
        return pipeline;
    }

    @Override
    public DeviceDetectionHashEngine getEngine() {
        return engine;
    }

    @Override
    public Iterable<FiftyOneAspectPropertyMetaData> getProperties() {
        return engine.getProperties();
    }

    @Override
    public Iterable<ValueMetaData> getValues() {
        return engine.getValues();
    }

    public Iterable<ProfileMetaData> getProfiles() {
        return engine.getProfiles();
    }

    public Iterable<ComponentMetaData> getComponents() {
        return engine.getComponents();
    }

    @Override
    public void close() {
        try {
            pipeline.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        pipeline = null;
        try {
            engine.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        engine = null;
    }
}