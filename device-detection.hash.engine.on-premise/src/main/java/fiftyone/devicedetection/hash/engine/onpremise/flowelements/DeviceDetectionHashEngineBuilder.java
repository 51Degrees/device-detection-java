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
import fiftyone.pipeline.annotations.DefaultValue;
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
import fiftyone.pipeline.util.Check;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static fiftyone.pipeline.util.StringManipulation.stringJoin;

/**
 * Builder for the {@link DeviceDetectionHashEngine}. All options for the engine
 * should be set here.
 * <p>
 * Default values are taken from device-detection-cxx/src/hash/hash.c
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
        this(LoggerFactory.getILoggerFactory());
    }

    /**
     * Construct a new instance using the {@link ILoggerFactory} supplied.
     * @param loggerFactory the logger factory to use
     */
    public DeviceDetectionHashEngineBuilder(ILoggerFactory loggerFactory) {
        this(loggerFactory, null);
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
     * Set whether an existing temp file should be used if one is found
     * in the temp directory.
     * <p>
     * Default is false.
     * @param reuse true if an existing file should be used
     * @return this builder
     */
    @DefaultValue("false")
    public DeviceDetectionHashEngineBuilder setReuseTempFile(boolean reuse) {
        config.setReuseTempFile(reuse);
        return this;
    }

    /**
     * Set whether the matched characters of the User-Agent should
     * be stored to be returned in the results.
     * <p>
     * Default is true
     * @param update true if the matched User-Agent should be stored
     * @return this builder
     */
    @DefaultValue("true")
    public DeviceDetectionHashEngineBuilder setUpdateMatchedUserAgent(
        boolean update) {
        config.setUpdateMatchedUserAgent(update);
        return this;
    }

    /**
     * Set the performance profile to use when constructing the data set.
     * <p>
     * Default value is Balanced.
     * @param profileName name of the profile to use
     * @return this builder
     */
    @DefaultValue("Balanced")
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

    /**
     * Provide a hint as to how many threads will access the pipeline simultaneously
     * <p>
     * Default is the result of {@link Runtime#getRuntime()#getAvailableProcessors()}
     * @see <a href="https://51degrees.com/documentation/_device_detection__features__concurrent_processing.html">Concurrent processing</a>
     * @param concurrency expected concurrent accesses
     * @return this builder
     */
    @DefaultValue("The result of Runtime#getRuntime().getAvailableProcessors()")
    @Override
    public DeviceDetectionHashEngineBuilder setConcurrency(int concurrency) {
        config.setConcurrency(concurrency);
        return this;
    }

    /**
     * Whether to return a default profile if no match
     * <p>
     * Default false
     * @see <a href="https://51degrees.com/documentation/_device_detection__features__false_positive_control.html">No Match Found</a>
     * @param allow true if results with no matched hash nodes should be
     *              considered valid
     * @return this builder
     */
    @DefaultValue("false")
    @Override
    public DeviceDetectionHashEngineBuilder setAllowUnmatched(boolean allow) {
        config.setAllowUnmatched(allow);
        return this;
    }

    /**
     * The difference tolerance allows for User-Agents where some characters differ
     * slightly from what is expected.
     * <p>
     * Default is 0.
     * @see <a href="https://51degrees.com/documentation/_device_detection__hash.html#DeviceDetection_Hash_DataSetProduction_Predictive">Hash Algorithm</a>
     * @param difference to allow
     * @return this builder
     */
    @DefaultValue(intValue = 0)
    @Override
    public DeviceDetectionHashEngineBuilder setDifference(int difference) {
        config.setDifference(difference);
        return this;
    }

    /**
     * Set the maximum drift to allow when matching hashes. If the drift is
     * exceeded, the result is considered invalid and values will not be
     * returned.
     * <p>
     * Default is 0.
     * @see <a href="https://51degrees.com/documentation/_device_detection__hash.html#DeviceDetection_Hash_DataSetProduction_Predictive">Hash Algorithm</a>
     * @param drift to set
     * @return this builder
     */
    @DefaultValue(intValue = 0)
    public DeviceDetectionHashEngineBuilder setDrift(int drift) {
        config.setDrift(drift);
        return this;
    }

    /**
     * Set whether the performance optimized graph is used
     * for processing. When processing evidence, the performance
     * graph is optimized to find an answer as quickly as possible.
     * However, this can be at the expense of finding the best
     * match for evidence which was not in the training data. If
     * the predictive graph is also enabled, it will be used
     * next if there was no match in the performance graph.
     * <p>
     * Default is false
     * @see <a href="https://51degrees.com/documentation/_device_detection__hash.html#DeviceDetection_Hash_DataSetProduction_Predictive">Hash Algorithm</a>
     * @param use true if the performance graph should be used
     * @return this builder
     */
    @DefaultValue("false")
    public DeviceDetectionHashEngineBuilder setUsePerformanceGraph(boolean use) {
        config.setUsePerformanceGraph(use);
        return this;
    }

    /**
     * Set whether the predictive optimized graph is used
     * for processing. When processing evidence, the predictive
     * graph is optimised to find the best answer for evidence
     * which was not in the training data. However, this is at the
     * expense of processing time, as more possibilities are taken into
     * consideration.
     * <p>
     * Default is true
     * @see <a href="https://51degrees.com/documentation/_device_detection__hash.html#DeviceDetection_Hash_DataSetProduction_Predictive">Hash Algorithm</a>
     * @param use true if the predictive graph should be used
     * @return this builder
     */
    @DefaultValue("true")
    public DeviceDetectionHashEngineBuilder setUsePredictiveGraph(boolean use) {
        config.setUsePredictiveGraph(use);
        return this;
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
                    "but it has " + dataFiles.size());
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
        if (dataFile.getConfiguration().getCreateTempDataCopy() && Check.notNullOrBlank(tempDir)) {
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
