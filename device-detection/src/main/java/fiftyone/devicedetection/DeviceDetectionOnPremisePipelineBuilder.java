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

import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngineBuilder;
import fiftyone.devicedetection.shared.flowelements.OnPremiseDeviceDetectionEngineBuilderBase;
import fiftyone.pipeline.core.exceptions.PipelineConfigurationException;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.Constants;
import fiftyone.pipeline.engines.configuration.CacheConfiguration;
import fiftyone.pipeline.engines.fiftyone.flowelements.FiftyOneAspectEngine;
import fiftyone.pipeline.engines.fiftyone.flowelements.ShareUsageBuilder;
import fiftyone.pipeline.engines.flowelements.AspectEngine;
import fiftyone.pipeline.engines.flowelements.PrePackagedPipelineBuilderBase;
import fiftyone.pipeline.engines.services.DataUpdateService;
import fiftyone.pipeline.engines.services.HttpClient;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

/**
 * Builder used to create pipelines with an on-premise
 * device detection engine.
 */
public class DeviceDetectionOnPremisePipelineBuilder
    extends PrePackagedPipelineBuilderBase<DeviceDetectionOnPremisePipelineBuilder> {

    protected boolean shareUsageEnabled = true;
    private String filename;
    private boolean createTempDataCopy;
    private byte[] engineData;
    private int concurrency = -1;
    private Integer difference = null;
    private Boolean allowUnmatched = null;
    private Boolean autoUpdateEnabled = null;
    private Boolean dataFileSystemWatcher = null;
    private Boolean dataUpdateOnStartup = null;
    private Long updatePollingInterval = null;
    private Long updateRandomisationMax = null;
    private String dataUpdateLicenseKey = null;
    private Constants.PerformanceProfiles performanceProfile =
        Constants.PerformanceProfiles.Balanced;
    private Enums.DeviceDetectionAlgorithm algorithm =
        Enums.DeviceDetectionAlgorithm.Hash;
    private final DataUpdateService dataUpdateService;
    private final HttpClient httpClient;

    DeviceDetectionOnPremisePipelineBuilder(
        DataUpdateService dataUpdateService,
        HttpClient httpClient) {
        this(
            LoggerFactory.getILoggerFactory(),
            dataUpdateService,
            httpClient);
    }

    DeviceDetectionOnPremisePipelineBuilder(
        ILoggerFactory loggerFactory,
        DataUpdateService dataUpdateService,
        HttpClient httpClient) {
        super(loggerFactory);
        this.dataUpdateService = dataUpdateService;
        this.httpClient = httpClient;
    }

    /**
     * Set the filename of the device detection data file that the
     * engine should use.
     * @param filename The data file.
     * @param createTempDataCopy
     * @return This builder instance.
     * @throws Exception Thrown if the filename has an unknown extension.
     */
    DeviceDetectionOnPremisePipelineBuilder setFilename(
        String filename,
        boolean createTempDataCopy) throws Exception {
        this.filename = filename;
        this.createTempDataCopy = createTempDataCopy;
        if (filename.substring(filename.length() - 4)
            .equalsIgnoreCase(".dat")) {
            throw new Exception("The Pattern data format data " +
                "files are deprecated in version 4. Please use a " +
                "Hash V4.1 data file.");
        } else if (filename.substring(filename.length() - 5)
            .equalsIgnoreCase(".hash")) {
            algorithm = Enums.DeviceDetectionAlgorithm.Hash;
        } else {
            throw new Exception("Unrecognised filename. " +
                "Expected a '*.hash' hash data file.");
        }
        return this;
    }

    /**
     * Set the byte array to use as a data source when 
     * creating the engine.
     * @param data The entire device detection data file as a byte array.
     * @param algorithm The detection algorithm that the supplied data supports.
     * @return This builder instance.
     */
    DeviceDetectionOnPremisePipelineBuilder setEngineData(
        byte[] data,
        Enums.DeviceDetectionAlgorithm algorithm) {
        this.engineData = data;
        this.algorithm = algorithm;
        return this;
    }

    /**
     * Set share usage enabled/disabled.
     * Defaults to enabled.
     * @param enabled True to enable usage sharing. False to disable.
     * @return This builder instance.
     */
    public DeviceDetectionOnPremisePipelineBuilder setShareUsage(
        boolean enabled) {
        shareUsageEnabled = enabled;
        return this;
    }

    /**
     * Enable/Disable auto update.
     * Defaults to enabled.
     * If enabled, the auto update system will automatically download
     * and apply new data files for device detection.
     * @param enabled True to enable auto update. False to disable.
     * @return This builder instance.
     */
    public DeviceDetectionOnPremisePipelineBuilder setAutoUpdate(
        boolean enabled) {
        autoUpdateEnabled = enabled;
        return this;
    }
    
    /**
     * The DataUpdateService has the ability to watch a 
     * file on disk and refresh the engine as soon as that file is 
     * updated.
     * This setting enables/disables that feature.
     * @param enabled True to enable file system watcher. False to disable.
     * @return This builder instance.
     */
    public DeviceDetectionOnPremisePipelineBuilder setDataFileSystemWatcher(
        boolean enabled) {
        dataFileSystemWatcher = enabled;
        return this;
    }
    
    
    /**
     * Enable/Disable update on startup.
     * Defaults to enabled.
     * If enabled, the auto update system will be used to check for
     * an update before the device detection engine is created.
     * If an update is available, it will be downloaded and applied
     * before the pipeline is built and returned for use so this may 
     * take some time.
     * @param enabled True to enable update on startup. False to disable.
     * @return This builder instance.
     */
    public DeviceDetectionOnPremisePipelineBuilder setDataUpdateOnStartup(
        boolean enabled) {
        dataUpdateOnStartup = enabled;
        return this;
    }
    
    /**
     * Set the time between checks for a new data file made by the 
     * DataUpdateService in seconds.
     * Default = 30 minutes.
     * @param pollingIntervalSeconds The number of seconds between checks.
     * @return This builder instance.
     */
    public DeviceDetectionOnPremisePipelineBuilder setUpdatePollingInterval(
        int pollingIntervalSeconds) {
        
        updatePollingInterval = (long)pollingIntervalSeconds * 1000;
        return this;
    }
    
    /**
     * Set the time between checks for a new data file made by the 
     * DataUpdateService in milliseconds.
     * @param pollingIntervalMillis The number of milliseconds between checks.
     * @return This builder instance.
     */
    public DeviceDetectionOnPremisePipelineBuilder setUpdatePollingIntervalMillis(
        long pollingIntervalMillis) {
        updatePollingInterval = pollingIntervalMillis;
        return this;
    }
    
    /**
     * A random element can be added to the DataUpdateService polling interval.
     * This option sets the maximum length of this random addition.
     * Default = 10 minutes.
     * @param randomisationMaxSeconds The maximum time added to the data update 
     * polling interval in seconds.
     * @return This builder instance.
     */
    public DeviceDetectionOnPremisePipelineBuilder setUpdateRandomisationMax(
        int randomisationMaxSeconds) {
        updateRandomisationMax = (long)randomisationMaxSeconds * 1000;
        return this;
    }
    
    /**
     * A random element can be added to the DataUpdateService polling interval.
     * This option sets the maximum length of this random addition.
     * Default = 10 minutes.
     * @param randomisationMaxMillis The maximum time added to the data update 
     * polling interval in milliseconds.
     * @return This builder instance.
     */
    public DeviceDetectionOnPremisePipelineBuilder setUpdateRandomisationMaxMillis(
        long randomisationMaxMillis) {
        updateRandomisationMax = randomisationMaxMillis;
        return this;
    }

    /**
     * Set the license key used when checking for new 
     * device detection data files.
     * Defaults to null.
     * @param key The license key.
     * @return This builder instance.
     */
    public DeviceDetectionOnPremisePipelineBuilder setDataUpdateLicenseKey(
        String key) {
        dataUpdateLicenseKey = key;
        return this;
    }

    /**
     * Set the performance profile for the device detection engine.
     * Defaults to balanced.
     * @param profile The performance profile to use.
     * @return This builder instance.
     */
    public DeviceDetectionOnPremisePipelineBuilder setPerformanceProfile(
        Constants.PerformanceProfiles profile) {
        performanceProfile = profile;
        return this;
    }

    /**
     * Set the expected number of concurrent operations using the engine.
     * This sets the concurrency of the internal caches to avoid excessive
     * locking.
     * @param concurrency expected concurrent accesses
     * @return this builder
     */
    public DeviceDetectionOnPremisePipelineBuilder setConcurrency(int concurrency) {
        this.concurrency = concurrency;
        return this;
    }

    /**
     * Set the maximum difference to allow when processing HTTP headers.
     * The meaning of difference depends on the Device Detection API being
     * used. The difference is the difference in hash value between the
     * hash that was found, and the hash that is being searched for.
     * By default this is 0.
     * @param difference to allow
     * @return this builder
     */
    public DeviceDetectionOnPremisePipelineBuilder setDifference(int difference) {
        this.difference = difference;
        return this;
    }

    /**
     * If set to false, a non-matching User-Agent will result in
     * properties without set values. If set to true, a non-matching
     * User-Agent will cause the 'default profiles' to be returned. This
     * means that properties will always have values (i.e. no need to
     * check .hasValue) but some may be inaccurate. By default, this is
     * false.
     * @param allow true if results with no matched hash nodes should be
     *              considered valid
     * @return this builder
     */
    public DeviceDetectionOnPremisePipelineBuilder setAllowUnmatched(boolean allow) {
        this.allowUnmatched = allow;
        return this;
    }

    /**
     * Build and return a pipeline that can perform device detection.
     * @return
     * @throws Exception 
     */
    @Override
    public Pipeline build() throws Exception {
        AspectEngine deviceDetectionEngine;

        // Create the device detection engine based on the configuration.
        switch (algorithm) {
            case Hash:
                DeviceDetectionHashEngineBuilder hashBuilder =
                    new DeviceDetectionHashEngineBuilder(loggerFactory, dataUpdateService);
                deviceDetectionEngine = configureAndBuild(hashBuilder);
                break;
            default:
                throw new PipelineConfigurationException(
                    "Unrecognised algorithm '" + algorithm.name() + "'.");
        }

        if (deviceDetectionEngine != null) {
            // Add the share usage element to the list if enabled
            if (shareUsageEnabled) {
                getFlowElements().add(
                    new ShareUsageBuilder(loggerFactory, httpClient).build());
            }
            // Add the device detection engine to the list
            getFlowElements().add(deviceDetectionEngine);
        } else {
            throw new RuntimeException("Unexpected error creating device detection engine.");
        }

        // Create and return the pipeline
        return super.build();
    }

    /**
     * Private method used to set configuration options common to 
     * both hash and pattern engines and build the engine.
     * @param <TBuilder> The type of the builder. Can be inferred from the 
     * builder parameter.
     * @param <TEngine> The type of the engine. Can be inferred from the builder
     * parameter.
     * @param builder The builder to configure.
     * @return A new device detection engine instance.
     * @throws Exception 
     */
    private <TBuilder extends OnPremiseDeviceDetectionEngineBuilderBase<TBuilder, TEngine>,
        TEngine extends FiftyOneAspectEngine>
    TEngine configureAndBuild(
        OnPremiseDeviceDetectionEngineBuilderBase<TBuilder, TEngine> builder) throws Exception {
        // Configure caching
        if (resultsCache) {
            CacheConfiguration cacheConfig = new CacheConfiguration(resultsCacheSize);
            builder.setCache(cacheConfig);
        }
        
        // Configure auto update.
        if(autoUpdateEnabled != null) {
            builder.setAutoUpdate(autoUpdateEnabled);
        }
        // Configure file system watcher.
        if(dataFileSystemWatcher != null) {
            builder.setDataFileSystemWatcher(dataFileSystemWatcher);
        }
        // Configure update on startup.
        if(dataUpdateOnStartup != null) {
        builder.setDataUpdateOnStartup(dataUpdateOnStartup);
        }
        // Configure update polling interval.
        if(updatePollingInterval != null) {
        builder.setUpdatePollingInterval(updatePollingInterval);
        }
        // Configure update polling interval randomisation.
        if(updateRandomisationMax != null) {
        builder.setUpdateRandomisationMax(updateRandomisationMax);
        }
        // Configure data update license key.
        if (dataUpdateLicenseKey != null) {
            builder.setDataUpdateLicenseKey(dataUpdateLicenseKey);
        }
        
        // Configure performance profile
        builder.setPerformanceProfile(performanceProfile);

        // Configure the concurrency
        if (concurrency > 1) {
            builder.setConcurrency(concurrency);
        }
        // Configure difference
        if (difference != null) {
            builder.setDifference(difference);
        }
        // Configure unmatched
        if (allowUnmatched != null) {
            builder.setAllowUnmatched(allowUnmatched);
        }

        // Build the engine
        TEngine engine;
        if (filename != null && filename.isEmpty() == false) {
            engine = builder.build(filename, createTempDataCopy);
        } else if (engineData != null) {
            engine = builder.build(engineData);
        } else {
            throw new PipelineConfigurationException(
                "No source for engine data. " +
                    "Use setFilename or setEngineData to configure this.");
        }

        return engine;
    }
}
