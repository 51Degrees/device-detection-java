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

package fiftyone.devicedetection.pattern.engine.onpremise.flowelements;

import fiftyone.devicedetection.pattern.engine.onpremise.data.DeviceDataPattern;
import fiftyone.devicedetection.pattern.engine.onpremise.interop.swig.ConfigPatternSwig;
import fiftyone.devicedetection.pattern.engine.onpremise.interop.swig.RequiredPropertiesConfigSwig;
import fiftyone.devicedetection.pattern.engine.onpremise.interop.swig.VectorStringSwig;
import fiftyone.devicedetection.shared.flowelements.OnPremiseDeviceDetectionEngineBuilderBase;
import fiftyone.pipeline.annotations.ElementBuilder;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.data.factories.ElementDataFactory;
import fiftyone.pipeline.core.exceptions.PipelineConfigurationException;
import fiftyone.pipeline.core.flowelements.FlowElement;
import fiftyone.pipeline.engines.Constants.PerformanceProfiles;
import fiftyone.pipeline.engines.data.AspectEngineDataFile;
import fiftyone.pipeline.engines.fiftyone.data.FiftyOneDataFileDefault;
import fiftyone.pipeline.engines.services.DataUpdateService;
import fiftyone.pipeline.engines.services.MissingPropertyServiceDefault;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static fiftyone.pipeline.util.StringManipulation.stringJoin;

@ElementBuilder(alternateName = "PatternDeviceDetection")
public class DeviceDetectionPatternEngineBuilder
    extends OnPremiseDeviceDetectionEngineBuilderBase<DeviceDetectionPatternEngineBuilder, DeviceDetectionPatternEngine> {

    private ConfigPatternSwig config = new ConfigPatternSwig();

    public DeviceDetectionPatternEngineBuilder() {
        super(LoggerFactory.getILoggerFactory());
        config.setConcurrency(Runtime.getRuntime().availableProcessors());

    }

    public DeviceDetectionPatternEngineBuilder(ILoggerFactory loggerFactory) {
        super(loggerFactory, null);
        config.setConcurrency(Runtime.getRuntime().availableProcessors());
    }

    public DeviceDetectionPatternEngineBuilder(
        ILoggerFactory loggerFactory,
        DataUpdateService dataUpdateService) {
        super(loggerFactory, dataUpdateService);
        config.setConcurrency(Runtime.getRuntime().availableProcessors());
    }

    @Override
    protected DeviceDetectionPatternEngine newEngine(List<String> properties) {
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
            VectorStringSwig tempDirs = new VectorStringSwig();
            tempDirs.add(tempDir);
            config.setTempDirectories(tempDirs);
            config.setUseTempFile(true);
        }

        VectorStringSwig propertiesSwig = new VectorStringSwig();
        for (String property : properties) {
            propertiesSwig.add(property);
        }
        return new DeviceDetectionPatternEngine(
            loggerFactory.getLogger(DeviceDetectionPatternEngine.class.getName()),
            dataFile,
            config,
            new RequiredPropertiesConfigSwig(propertiesSwig),
            new PatternDataFactory(loggerFactory),
            tempDir);
    }

    public DeviceDetectionPatternEngineBuilder setReuseTempFile(boolean reuse) {
        config.setReuseTempFile(reuse);
        return this;
    }

    public DeviceDetectionPatternEngineBuilder setUpdateMatchedUserAgent(
        boolean update) {
        config.setUpdateMatchedUserAgent(update);
        return this;
    }

    public DeviceDetectionPatternEngineBuilder setPerformanceProfile(
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
                    "Available profiles are " + stringJoin(available, ", ") + ".");
        }
    }

    @Override
    public DeviceDetectionPatternEngineBuilder setPerformanceProfile(
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
                    "The performance profile '" + profile.name() + "' is not valid " +
                        "for a DeviceDetectionPatternEngine.");
        }
        return this;
    }

    public DeviceDetectionPatternEngineBuilder setUserAgentCache(int capacity) {
        config.setUserAgentCacheCapacity(capacity);
        return this;
    }

    @Override
    public DeviceDetectionPatternEngineBuilder setConcurrency(int concurrency) {
        config.setConcurrency(concurrency);
        return this;
    }

    @Override
    public DeviceDetectionPatternEngineBuilder setDifference(int difference) {
        config.setDifference(difference);
        return this;
    }

    @Override
    public DeviceDetectionPatternEngineBuilder setAllowUnmatched(boolean allow) {
        config.setAllowUnmatched(allow);
        return this;
    }

    public DeviceDetectionPatternEngineBuilder setClosestSignatures(
        int closestSignatures) {
        config.setClosestSignatures(closestSignatures);
        return this;
    }

    @Override
    protected AspectEngineDataFile newAspectEngineDataFile() {
        return new FiftyOneDataFileDefault();
    }

    private static class PatternDataFactory implements ElementDataFactory<DeviceDataPattern> {

        private final ILoggerFactory loggerFactory;

        public PatternDataFactory(ILoggerFactory loggerFactory) {
            this.loggerFactory = loggerFactory;
        }

        @Override
        public DeviceDataPattern create(FlowData flowData, FlowElement<DeviceDataPattern, ?> engine) {
            return new DeviceDataPatternDefault(
                loggerFactory.getLogger(DeviceDataPatternDefault.class.getName()),
                flowData,
                (DeviceDetectionPatternEngine) engine,
                MissingPropertyServiceDefault.getInstance());
        }
    }
}
