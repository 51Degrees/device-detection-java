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

import fiftyone.pipeline.engines.services.DataUpdateService;
import fiftyone.pipeline.engines.services.DataUpdateServiceDefault;
import fiftyone.pipeline.engines.services.HttpClient;
import fiftyone.pipeline.engines.services.HttpClientDefault;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

public class DeviceDetectionPipelineBuilder {

    protected ILoggerFactory loggerFactory;
    private DataUpdateService dataUpdateService;
    private HttpClient httpClient;

    public DeviceDetectionPipelineBuilder() {
        this(LoggerFactory.getILoggerFactory());
    }


    public DeviceDetectionPipelineBuilder(ILoggerFactory loggerFactory) {
        this(loggerFactory, new HttpClientDefault());
    }

    public DeviceDetectionPipelineBuilder(
        ILoggerFactory loggerFactory,
        HttpClient httpClient) {
        this.httpClient = httpClient;
        this.loggerFactory = loggerFactory;
        dataUpdateService = new DataUpdateServiceDefault(
            loggerFactory.getLogger(DataUpdateServiceDefault.class.getName()),
            httpClient);
    }

    public DeviceDetectionOnPremisePipelineBuilder useOnPremise(
        String datafile,
        boolean createTempDataCopy) throws Exception {
        DeviceDetectionOnPremisePipelineBuilder builder =
            new DeviceDetectionOnPremisePipelineBuilder(
                loggerFactory,
                dataUpdateService,
                httpClient);
        builder.setFilename(datafile, createTempDataCopy);
        return builder;
    }

    public DeviceDetectionOnPremisePipelineBuilder useOnPremise(
        byte[] data,
        Enums.DeviceDetectionAlgorithm algorithm) {
        DeviceDetectionOnPremisePipelineBuilder builder =
            new DeviceDetectionOnPremisePipelineBuilder(
                loggerFactory,
                dataUpdateService,
                httpClient);
        builder.setEngineData(data, algorithm);
        return builder;
    }

    public DeviceDetectionCloudPipelineBuilder useCloud(String resourceKey) {
        DeviceDetectionCloudPipelineBuilder builder =
            new DeviceDetectionCloudPipelineBuilder(loggerFactory, httpClient);
        builder.setResourceKey(resourceKey);
        return builder;
    }
}
