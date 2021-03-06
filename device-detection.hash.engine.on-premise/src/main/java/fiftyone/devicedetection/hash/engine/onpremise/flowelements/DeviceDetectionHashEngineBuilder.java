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

package fiftyone.devicedetection.hash.engine.onpremise.flowelements;

import fiftyone.devicedetection.hash.engine.onpremise.data.DeviceDataHash;
import fiftyone.devicedetection.hash.engine.onpremise.interop.swig.ConfigHashSwig;
import fiftyone.devicedetection.hash.engine.onpremise.interop.swig.RequiredPropertiesConfigSwig;
import fiftyone.devicedetection.hash.engine.onpremise.interop.swig.VectorStringSwig;
import fiftyone.devicedetection.shared.flowelements.OnPremiseDeviceDetectionEngineBuilderBase;
import fiftyone.pipeline.annotations.ElementBuilder;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.data.factories.ElementDataFactory;
import fiftyone.pipeline.core.exceptions.PipelineConfigurationException;
import fiftyone.pipeline.core.flowelements.FlowElement;
import fiftyone.pipeline.engines.Constants.PerformanceProfiles;
import fiftyone.pipeline.engines.configuration.CacheConfiguration;
import fiftyone.pipeline.engines.data.AspectEngineDataFile;
import fiftyone.pipeline.engines.services.DataUpdateService;
import fiftyone.pipeline.engines.services.MissingPropertyServiceDefault;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static fiftyone.pipeline.util.StringManipulation.stringJoin;

/**
 * Builder for the {@link DeviceDetectionHashEngine}. All options for the engine
 * should be set here.
 */
@ElementBuilder(alternateName = "HashDeviceDetection")
public class DeviceDetectionHashEngineBuilder
    extends OnPremiseDeviceDetectionEngineBuilderBase<
    DeviceDetectionHashEngineBuilder,
    DeviceDetectionHashEngine> {

    private final String dataDownloadType = "HashV41";
   
    /**
     * Native configuration instance for this engine.
     */
    private final ConfigHashSwig config = new ConfigHashSwig();

    /**
     * Default constructor which uses the {@link ILoggerFactory} implementation
     * returned by {@link LoggerFactory#getILoggerFactory()}.
     */
    public DeviceDetectionHashEngineBuilder() {
        super(LoggerFactory.getILoggerFactory());
        config.setConcurrency(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Construct a new instance using the {@link ILoggerFactory} supplied.
     * @param loggerFactory the logger factory to use
     */
    public DeviceDetectionHashEngineBuilder(ILoggerFactory loggerFactory) {
        super(loggerFactory, null);
        config.setConcurrency(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Construct a new instance using the {@link ILoggerFactory} and
     * {@link DataUpdateService} supplied.
     * @param loggerFactory the logger factory to use
     * @param dataUpdateService the {@link DataUpdateService} to use when
     *                          automatic updates happen on the data file
     */
    public DeviceDetectionHashEngineBuilder(
        ILoggerFactory loggerFactory,
        DataUpdateService dataUpdateService) {
        super(loggerFactory, dataUpdateService);
        config.setConcurrency(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Set whether or not an existing temp file should be used if one is found
     * in the temp directory.
     * @param reuse true if an existing file should be used
     * @return this builder
     */
    public DeviceDetectionHashEngineBuilder setReuseTempFile(boolean reuse) {
        config.setReuseTempFile(reuse);
        return this;
    }

    /**
     * Set whether or not the matched characters of the User-Agent should
     * be stored to be returned in the results.
     * @param update true if the matched User-Agent should be stored
     * @return this builder
     */
    public DeviceDetectionHashEngineBuilder setUpdateMatchedUserAgent(
        boolean update) {
        config.setUpdateMatchedUserAgent(update);
        return this;
    }

    /**
     * Set the performance profile to use when constructing the data set.
     * @param profileName name of the profile to use
     * @return this builder
     */
    public DeviceDetectionHashEngineBuilder setPerformanceProfile(
        String profileName) {
        PerformanceProfiles profile;
        try {
            profile = PerformanceProfiles.valueOf(profileName);
        } catch (IllegalArgumentException e) {
            profile = null;
        }

        if (profile != null) {
            return setPerformanceProfile(profile);
        } else {
            List<String> available = new ArrayList<>();
            for (PerformanceProfiles p : PerformanceProfiles.values()) {
                available.add("'" + p.name() + "'");
            }
            throw new IllegalArgumentException(
                "'" + profileName + "' is not a valid performance profile. " +
                    "Available profiles are " +
                    stringJoin(available, ", ") + ".");
        }
    }

    @Override
    public DeviceDetectionHashEngineBuilder setPerformanceProfile(
        PerformanceProfiles profile) {
        switch (profile) {
            case LowMemory:
                config.setLowMemory();
                break;
            case MaxPerformance:
                config.setMaxPerformance();
                break;
            case Balanced:
                config.setBalanced();
                break;
            case BalancedTemp:
                config.setBalancedTemp();
                break;
            case HighPerformance:
                config.setHighPerformance();
                break;
            default:
                throw new IllegalArgumentException(
                    "The performance profile '" + profile.name() +
                        "' is not valid for a DeviceDetectionHashEngine.");
        }
        return this;
    }

    @Override
    public DeviceDetectionHashEngineBuilder setConcurrency(int concurrency) {
        config.setConcurrency(concurrency);
        return this;
    }

    @Override
    public DeviceDetectionHashEngineBuilder setAllowUnmatched(boolean allow) {
        config.setAllowUnmatched(allow);
        return this;
    }

    @Override
    public DeviceDetectionHashEngineBuilder setDifference(int difference) {
        config.setDifference(difference);
        return this;
    }

    /**
     * Set the maximum drift to allow when matching hashes. If the drift is
     * exceeded, the result is considered invalid and values will not be
     * returned. By default this is 0.
     * @param drift to set
     * @return this builder
     */
    public DeviceDetectionHashEngineBuilder setDrift(int drift) {
        config.setDrift(drift);
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
     * @see <a href="https://51degrees.com/documentation/_device_detection__hash.html#DeviceDetection_Hash_DataSetProduction_Predictive">Hash Algorithm</a>
     * @param use true if the performance graph should be used
     * @return this builder
     */
    public DeviceDetectionHashEngineBuilder setUsePerformanceGraph(boolean use) {
        config.setUsePerformanceGraph(use);
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
    public DeviceDetectionHashEngineBuilder setUsePredictiveGraph(boolean use) {
        config.setUsePredictiveGraph(use);
        return this;
    }

    @Override
    public DeviceDetectionHashEngineBuilder setCache(
        CacheConfiguration cacheConfiguration) {
        throw new UnsupportedOperationException(
            "A results cache cannot be configured in the on-premise Hash " +
            "engine. The overhead of having to manage native object " + 
            "lifetimes when a cache is enabled outweighs the benefit of the " +
            "cache.");
    }

    /**
     * The default value to use for the 'Type' parameter when sending
     * a request to the Distributor
     * @return default data download type;
     */
    @Override
    protected String getDefaultDataDownloadType() {
        return dataDownloadType;
    }

    @Override
    protected DeviceDetectionHashEngine newEngine(List<String> properties) {
        if (dataFiles.size() != 1) {
            throw new PipelineConfigurationException(
                "This builder requires one and only one configured file " +
                    "but it has {DataFiles.Count}");
        }
        AspectEngineDataFile dataFile = dataFiles.get(0);
        // We remove the data file configuration from the list.
        // This is because the on-premise engine builder base class
        // adds all the data file configs after engine creation.
        // However, the device detection data files are supplied
        // directly to the constructor.
        // Consequently, we remove it here to stop it from being added
        // again by the base class.
        dataFiles.remove(0);

        // Update the swig configuration object.
        config.setUseUpperPrefixHeaders(false);
        if (dataFile.getConfiguration().getCreateTempDataCopy() &&
            tempDir != null && tempDir.isEmpty() == false) {
            try (VectorStringSwig tempDirs = new VectorStringSwig()) {
                tempDirs.add(tempDir);
                config.setTempDirectories(tempDirs);
                config.setUseTempFile(true);
            }
        }
        RequiredPropertiesConfigSwig requiredProperties;
        try (VectorStringSwig propertiesSwig = new VectorStringSwig()) {
            propertiesSwig.addAll(properties);
            requiredProperties = new RequiredPropertiesConfigSwig(propertiesSwig);
        }
        return new DeviceDetectionHashEngine(
            loggerFactory.getLogger(DeviceDetectionHashEngine.class.getName()),
            dataFile,
            config,
            requiredProperties,
            new HashDataFactory(loggerFactory),
            tempDir);
    }

    private static class HashDataFactory implements
        ElementDataFactory<DeviceDataHash> {

        private final ILoggerFactory loggerFactory;

        public HashDataFactory(ILoggerFactory loggerFactory) {
            this.loggerFactory = loggerFactory;
        }

        @Override
        public DeviceDataHash create(
            FlowData flowData,
            FlowElement<DeviceDataHash, ?> engine) {
            return new DeviceDataHashDefault(
                loggerFactory.getLogger(DeviceDataHash.class.getName()),
                flowData,
                (DeviceDetectionHashEngine) engine,
                MissingPropertyServiceDefault.getInstance());
        }
    }
}
