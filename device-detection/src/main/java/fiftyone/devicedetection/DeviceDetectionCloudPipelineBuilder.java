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

package fiftyone.devicedetection;

import fiftyone.devicedetection.cloud.flowelements.DeviceDetectionCloudEngineBuilder;
import fiftyone.pipeline.cloudrequestengine.flowelements.CloudRequestEngine;
import fiftyone.pipeline.cloudrequestengine.flowelements.CloudRequestEngineBuilder;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.configuration.CacheConfiguration;
import fiftyone.pipeline.engines.configuration.LazyLoadingConfiguration;
import fiftyone.pipeline.engines.flowelements.CloudPipelineBuilderBase;
import fiftyone.pipeline.engines.services.HttpClient;
import org.slf4j.ILoggerFactory;

public class DeviceDetectionCloudPipelineBuilder
    extends CloudPipelineBuilderBase<DeviceDetectionCloudPipelineBuilder> {

    private final HttpClient httpClient;

    public DeviceDetectionCloudPipelineBuilder(
        ILoggerFactory loggerFactory,
        HttpClient httpClient) {
        super(loggerFactory);
        this.httpClient = httpClient;
    }

    @Override
    public Pipeline build() throws Exception {
        // Configure and build the cloud request engine
        CloudRequestEngineBuilder cloudRequestEngineBuilder =
            new CloudRequestEngineBuilder(loggerFactory, httpClient);
        if (lazyLoading) {
            cloudRequestEngineBuilder.setLazyLoading(new LazyLoadingConfiguration(
                (int) lazyLoadingTimeoutMillis));
        }
        if (resultsCache) {
            cloudRequestEngineBuilder.setCache(
                new CacheConfiguration(resultsCacheSize));
        }
        if (url != null && url.isEmpty() == false) {
            cloudRequestEngineBuilder.setEndpoint(url);
        }
        if (dataEndpoint != null && dataEndpoint.isEmpty() == false) {
            cloudRequestEngineBuilder.setDataEndpoint(dataEndpoint);
        }
        if (propertiesEndpoint != null && propertiesEndpoint.isEmpty() == false) {
            cloudRequestEngineBuilder.setPropertiesEndpoint(propertiesEndpoint);
        }
        if (evidenceKeysEndpoint != null && evidenceKeysEndpoint.isEmpty() == false){
            cloudRequestEngineBuilder.setEvidenceKeysEndpoint(evidenceKeysEndpoint);
        }
        if (resourceKey != null && resourceKey.isEmpty() == false) {
            cloudRequestEngineBuilder.setResourceKey(resourceKey);
        }
        if (licenseKey != null && licenseKey.isEmpty() == false) {
            cloudRequestEngineBuilder.setLicenseKey(licenseKey);
        }
        CloudRequestEngine cloudRequestEngine = cloudRequestEngineBuilder.build();

        // Configure and build the device detection engine
        DeviceDetectionCloudEngineBuilder deviceDetectionEngineBuilder =
            new DeviceDetectionCloudEngineBuilder(
                loggerFactory,
                httpClient,
                cloudRequestEngine);
        if (lazyLoading) {
            deviceDetectionEngineBuilder.setLazyLoading(new LazyLoadingConfiguration(
                (int) lazyLoadingTimeoutMillis));
        }

        // Add the elements to the list
        flowElements.add(cloudRequestEngine);
        flowElements.add(deviceDetectionEngineBuilder.build());

        // Build and return the pipeline
        return super.build();
    }
}
