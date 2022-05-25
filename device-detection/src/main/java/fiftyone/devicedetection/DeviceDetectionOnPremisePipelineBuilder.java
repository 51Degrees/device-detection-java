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

import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngineBuilder;
import fiftyone.pipeline.core.exceptions.PipelineConfigurationException;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.Constants;
import fiftyone.pipeline.engines.data.AspectData;
import fiftyone.pipeline.engines.data.AspectPropertyMetaData;
import fiftyone.pipeline.engines.fiftyone.flowelements.ShareUsageBuilder;
import fiftyone.pipeline.engines.flowelements.AspectEngine;
import fiftyone.pipeline.engines.flowelements.PrePackagedPipelineBuilderBase;
import fiftyone.pipeline.engines.services.DataUpdateService;
import fiftyone.pipeline.engines.services.HttpClient;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
    private Integer drift = null;
    private Boolean usePerformanceGraph = null;
    private Boolean usePredictiveGraph = null;
    private final List<String> properties = new ArrayList<String>();
    private Boolean autoUpdateEnabled = null;
    private Boolean dataFileSystemWatcher = null;
    private Boolean dataUpdateOnStartup = null;
    private Long updatePollingInterval = null;
    private Long updateRandomisationMax = null;
    private String dataUpdateLicenseKey = null;
    private Constants.PerformanceProfiles performanceProfile =
        Constants.PerformanceProfiles.Balanced;

    private DataUpdateService dataUpdateService;
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
        // Make sure to add dataUpdateService to the list of managed services
        this.addService(dataUpdateService);
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
        if (filename.endsWith(".dat")) {
            throw new Exception("The Pattern data format data " +
                "files can not be used in version 4. Please use a " +
                "Hash V4.1 data file.");
        }
        if (filename.endsWith(".hash") == false) {
            throw new Exception("Unrecognised filename. " +
                "Expected a '*.hash' hash data file.");
        }
        return this;
    }

    /**
     * Set the byte array to use as a data source when 
     * creating the engine.
     * @param data The entire device detection data file as a byte array.
      * @return This builder instance.
     */
    DeviceDetectionOnPremisePipelineBuilder setEngineData(byte[] data) {
        this.engineData = data;
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
     * Automatic updates require a {@link DataUpdateService}.
     * @param dataUpdateService an instance of a dataUpdateService
     * @return This builder instance.
     */
    public DeviceDetectionOnPremisePipelineBuilder setDataUpdateService(
            DataUpdateService dataUpdateService) {
        this.dataUpdateService = dataUpdateService;
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
     * Set the maximum drift to allow when matching hashes. If the
     * drift is exceeded, the result is considered invalid and
     * values will not be returned. By default this is 0.
     * @param drift to set
     * @return this builder
     */
    public DeviceDetectionOnPremisePipelineBuilder setDrift(int drift) {
        this.drift = drift;
        return this;
    }

    /**
     * Set whether or not the performance optimized graph is used
     * for processing. When processing evidence, the performance
     * graph is optimised to find an answer as quick as possible.
     * However, this can be at the expense of finding the best
     * match for evidence which was not in the training data. If
     * the predictive graph is also enabled, it will be used
     * next if there was no match in the performance graph.
     * @see <a href="https://51degrees.com/documentation/_device_detection__hash.html#DeviceDetection_Hash_DataSetProduction_Performance">Hash Algorithm</a>
     * @param use true if the performance graph should be used
     * @return this builder
     */
    public DeviceDetectionOnPremisePipelineBuilder setUsePerformanceGraph(boolean use) {
        this.usePerformanceGraph = use;
        return this;
    }

    /**
     * Set whether or not the predictive optimized graph is used
     * for processing. When processing evidence, the predictive
     * graph is optimised to find the best answer for evidence
     * which was not in the training data. However, this is at the
     * expense of processing time, as more possibilities are taken into
     * consideration.
     * @see <a href="https://51degrees.com/documentation/_device_detection__hash.html#DeviceDetection_Hash_DataSetProduction_Predictive">Hash Algorithm</a>
     * @param use true if the predictive graph should be used
     * @return this builder
     */
    public DeviceDetectionOnPremisePipelineBuilder setUsePredictiveGraph(boolean use) {
        this.usePredictiveGraph = use;
        return this;
    }

    /**
     * Add a property to the list of properties that the engine will populate in
     * the response. By default all properties will be populated.
     * @param property the property that we want the engine to populate
     * @return this builder
     */
    public DeviceDetectionOnPremisePipelineBuilder setProperty(String property) {
        this.properties.add(property);
        return this;
    }

    /**
     * Build and return a pipeline that can perform device detection.
     * @return the built pipeline
     * @throws Exception on error
     */
    @Override
    public Pipeline build() throws Exception {
        AspectEngine<? extends AspectData, ? extends AspectPropertyMetaData> deviceDetectionEngine;

        DeviceDetectionHashEngineBuilder hashBuilder =
                new DeviceDetectionHashEngineBuilder(loggerFactory, dataUpdateService);
        deviceDetectionEngine = configureAndBuild(hashBuilder);

        if (deviceDetectionEngine != null) {
            // Add the share usage element to the list if enabled
            if (shareUsageEnabled) {
                getFlowElements().add(
                    new ShareUsageBuilder(loggerFactory).build());
            }
            // Add the device detection engine to the list
            getFlowElements().add(deviceDetectionEngine);
        } else {
            throw new RuntimeException("Unexpected error creating device detection engine.");
        }

        setAutoCloseElements(true);

        // Create and return the pipeline
        return super.build();
    }

    /**
     * Private method used to set configuration options common to 
     * both hash and pattern engines and build the engine.
     * @param builder The builder to configure.
     * @return A new device detection engine instance.
     * @throws Exception 
     */
    private DeviceDetectionHashEngine configureAndBuild(
        DeviceDetectionHashEngineBuilder builder) throws Exception {
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
        // Configure the available properties.
        if (properties.size() > 0) {
            for (String property : properties) {
                builder.setProperty(property);
            }
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
        // Configure drift
        if (drift != null) {
            builder.setDrift(drift);
        }
        // Configure performance graph
        if (usePerformanceGraph != null) {
            builder.setUsePerformanceGraph(usePerformanceGraph);
        }
        // Configure predictive graph
        if (usePredictiveGraph != null) {
            builder.setUsePredictiveGraph(usePredictiveGraph);
        }

        // Build the engine
        DeviceDetectionHashEngine engine;
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
