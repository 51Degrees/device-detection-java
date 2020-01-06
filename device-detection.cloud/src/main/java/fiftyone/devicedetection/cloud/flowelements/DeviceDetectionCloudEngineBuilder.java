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

package fiftyone.devicedetection.cloud.flowelements;

import fiftyone.devicedetection.cloud.data.DeviceDataCloud;
import fiftyone.pipeline.annotations.ElementBuilder;
import fiftyone.pipeline.cloudrequestengine.flowelements.CloudRequestEngine;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.data.factories.ElementDataFactory;
import fiftyone.pipeline.core.flowelements.FlowElement;
import fiftyone.pipeline.engines.flowelements.CloudAspectEngineBuilderBase;
import fiftyone.pipeline.engines.services.HttpClient;
import fiftyone.pipeline.engines.services.HttpClientDefault;
import fiftyone.pipeline.engines.services.MissingPropertyServiceDefault;
import org.slf4j.ILoggerFactory;

import java.util.List;

@ElementBuilder
public class DeviceDetectionCloudEngineBuilder
    extends CloudAspectEngineBuilderBase<
            DeviceDetectionCloudEngineBuilder,
            DeviceDetectionCloudEngine> {
    private HttpClient httpClient;
    private CloudRequestEngine cloudRequestEngine;

    public DeviceDetectionCloudEngineBuilder(ILoggerFactory loggerFactory) {
        this(loggerFactory, new HttpClientDefault(), null);
    }

    public DeviceDetectionCloudEngineBuilder(
        ILoggerFactory loggerFactory,
        HttpClient httpClient,
        CloudRequestEngine engine) {
        super(loggerFactory);
        this.httpClient = httpClient;
        this.cloudRequestEngine = engine;
    }

    @Override
    protected DeviceDetectionCloudEngine newEngine(List<String> properties) {
        return new DeviceDetectionCloudEngine(
            loggerFactory.getLogger(DeviceDetectionCloudEngine.class.getName()),
            new DeviceDataCloudFactory(loggerFactory));
    }

    public DeviceDetectionCloudEngine build() throws Exception {
        return buildEngine();
    }

    private static class DeviceDataCloudFactory implements ElementDataFactory<DeviceDataCloud> {

        private final ILoggerFactory loggerFactory;

        public DeviceDataCloudFactory(ILoggerFactory loggerFactory) {
            this.loggerFactory = loggerFactory;
        }

        @Override
        public DeviceDataCloud create(FlowData flowData, FlowElement<DeviceDataCloud, ?> engine) {
            return new DeviceDataCloud(
                loggerFactory.getLogger(DeviceDataCloud.class.getName()),
                flowData,
                (DeviceDetectionCloudEngine) engine,
                MissingPropertyServiceDefault.getInstance());
        }
    }
}