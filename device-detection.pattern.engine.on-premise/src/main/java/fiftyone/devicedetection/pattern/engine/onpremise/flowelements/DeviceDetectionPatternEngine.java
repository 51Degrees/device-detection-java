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
import fiftyone.devicedetection.pattern.engine.onpremise.data.ProfileMetaDataPattern;
import fiftyone.devicedetection.pattern.engine.onpremise.data.PropertyMetaDataPattern;
import fiftyone.devicedetection.pattern.engine.onpremise.data.ValueMetaDataPattern;
import fiftyone.devicedetection.pattern.engine.onpremise.interop.ComponentIterable;
import fiftyone.devicedetection.pattern.engine.onpremise.interop.ProfileIterable;
import fiftyone.devicedetection.pattern.engine.onpremise.interop.PropertyIterable;
import fiftyone.devicedetection.pattern.engine.onpremise.interop.ValueIterable;
import fiftyone.devicedetection.pattern.engine.onpremise.interop.swig.*;
import fiftyone.devicedetection.pattern.engine.onpremise.interop.swig.Date;
import fiftyone.pipeline.core.data.EvidenceKeyFilter;
import fiftyone.pipeline.core.data.EvidenceKeyFilterWhitelist;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.data.factories.ElementDataFactory;
import fiftyone.pipeline.engines.data.AspectEngineDataFile;
import fiftyone.pipeline.engines.fiftyone.data.*;
import fiftyone.pipeline.engines.fiftyone.flowelements.FiftyOneOnPremiseAspectEngineBase;
import org.slf4j.Logger;

import java.util.*;

public class DeviceDetectionPatternEngine
    extends FiftyOneOnPremiseAspectEngineBase<DeviceDataPattern, FiftyOneAspectPropertyMetaData> {
    private EnginePatternSwig engine = null;
    private final List<FiftyOneAspectPropertyMetaData> properties = new ArrayList<>();
    private final ConfigPatternSwig config;
    private final RequiredPropertiesConfigSwig propertiesConfigSwig;
    private List<String> evidenceKeys;
    private EvidenceKeyFilter evidenceKeyFilter;
    private volatile boolean propertiesPopulated = false;

    DeviceDetectionPatternEngine(
        Logger logger,
        AspectEngineDataFile dataFile,
        ConfigPatternSwig config,
        RequiredPropertiesConfigSwig properties,
        ElementDataFactory<DeviceDataPattern> deviceDataFactory,
        String tempDataFileDir) {
        super(logger, deviceDataFactory, tempDataFileDir);
        this.config = config;
        this.propertiesConfigSwig = properties;
        addDataFile(dataFile);
    }

    private static List<String> getKeysFromEngine(EngineDeviceDetectionSwig engine) {
        List<String> result = new ArrayList<>();
        VectorStringSwig keys = engine.getKeys();
        for (int i = 0; i < keys.size(); i++) {
            result.add(keys.get(i));
        }
        return result;
    }

    @Override
    public String getElementDataKey() {
        return "device";
    }

    public MetaDataSwig getMetaData() {
        return engine.getMetaData();
    }

    @Override
    public List<FiftyOneAspectPropertyMetaData> getProperties() {
        if (propertiesPopulated == false) {
            synchronized (properties) {
                if (propertiesPopulated == false) {
                    properties.clear();
                    List<FiftyOneAspectPropertyMetaData> newProperties = new ArrayList<>();

                    try (PropertyIterable iterable = new PropertyIterable(
                        this,
                        newProperties,
                        engine.getMetaData().getProperties())) {
                        for (FiftyOneAspectPropertyMetaData property : iterable) {
                            properties.add(property);
                        }
                    } catch (Exception e) {
                        logger.error("Exception occurred while constructing properties.", e);
                    }

                    for (FiftyOneAspectPropertyMetaData property : getMetricProperties()) {
                        newProperties.add(property);
                    }

                    propertiesPopulated = true;
                }
            }
        }
        return properties;
    }

    @Override
    public FiftyOneAspectPropertyMetaData getProperty(String name) {
        PropertyMetaDataSwig swigProperty =
            engine.getMetaData().getProperties().getByKey(name);
        if (swigProperty != null) {
            return new PropertyMetaDataPattern(this, swigProperty);
        }

        for (FiftyOneAspectPropertyMetaData property : getMetricProperties()) {
            if (property.getName().equalsIgnoreCase(name)) {
                return property;
            }
        }

        return null;
    }

    @Override
    public CloseableIterable<ProfileMetaData> getProfiles() {
        return new ProfileIterable(this, engine.getMetaData().getProfiles());
    }

    @Override
    public ProfileMetaData getProfile(int profileId) {
        return new ProfileMetaDataPattern(
            this,
            engine.getMetaData().getProfiles().getByKey(profileId));
    }

    @Override
    public CloseableIterable<ComponentMetaData> getComponents() {
        return new ComponentIterable(this, engine.getMetaData().getComponents());
    }

    @Override
    public CloseableIterable<ValueMetaData> getValues() {
        return new ValueIterable(this, engine.getMetaData().getValues());
    }

    @Override
    public ValueMetaData getValue(String propertyName, String valueName) {
        ValueMetaDataKeySwig key = new ValueMetaDataKeySwig(propertyName, valueName);
        return new ValueMetaDataPattern(
            this,
            engine.getMetaData().getValues().getByKey(key));
    }

    @Override
    public java.util.Date getDataFilePublishedDate(String dataFileIdentifier) {
        Date value = engine.getPublishedTime();
        Calendar calendar = Calendar.getInstance();
        calendar.set(value.getYear(), value.getMonth(), value.getDay());
        return calendar.getTime();
    }

    @Override
    public java.util.Date getDataFileUpdateAvailableTime(String dataFileIdentifier) {
        Date value = engine.getUpdateAvailableTime();
        Calendar calendar = Calendar.getInstance();
        calendar.set(value.getYear(), value.getMonth(), value.getDay());
        return calendar.getTime();
    }

    @Override
    public String getDataSourceTier() {
        return engine.getType();
    }

    private String getDataFileTempPath() {
        return engine == null ? null : engine.getDataFileTempPath();
    }

    @Override
    public EvidenceKeyFilter getEvidenceKeyFilter() {
        return evidenceKeyFilter;
    }

    @Override
    public void refreshData(String dataFileIdentifier) {
        AspectEngineDataFile dataFile = getDataFiles().get(0);

        if (dataFile.getDataFilePath() != null &&
            dataFile.getDataFilePath().isEmpty() == false) {
            engine = new EnginePatternSwig(dataFile.getDataFilePath(), config, propertiesConfigSwig);
        }
        else {
            engine.refreshData();
        }
        setEngineMetaData();
    }

    @Override
    public void refreshData(String dataFileIdentifier, byte[] data) {
        if (engine == null) {
            engine = new EnginePatternSwig(data, config, propertiesConfigSwig);
        }
        else {
            engine.refreshData(data);
        }
        setEngineMetaData();
    }

    @Override
    protected void processEngine(FlowData flowData, DeviceDataPattern deviceData) {
        EvidenceDeviceDetectionSwig relevantEvidence =
            new EvidenceDeviceDetectionSwig();
        List<String> keys = getKeysFromEngine(engine);
        for (Map.Entry<String, Object> evidenceItem : flowData.getEvidence().asKeyMap().entrySet()) {
            boolean containsKey = false;
            for (String key : keys) {
                if (key.equalsIgnoreCase(evidenceItem.getKey())) {
                    containsKey = true;
                    break;
                }
            }

            if (containsKey == true) {
                relevantEvidence.put(
                    evidenceItem.getKey(),
                    evidenceItem.getValue().toString());
            }
        }
        ((DeviceDataPatternDefault) deviceData).setResults(engine.process(relevantEvidence));
    }

    @Override
    protected void unmanagedResourcesCleanup() {
        if (engine != null) {
            engine.delete();
        }
    }

    private void setEngineMetaData() {
        evidenceKeys = getKeysFromEngine(engine);
        evidenceKeyFilter = new EvidenceKeyFilterWhitelist(
            evidenceKeys,
            String.CASE_INSENSITIVE_ORDER);
        propertiesPopulated = false;
        // Populate these data file properties from the native engine.
        FiftyOneDataFile dataFileMetaData = (FiftyOneDataFile)getDataFileMetaData();
        if (dataFileMetaData != null) {
            dataFileMetaData.setDataPublishedDateTime(getDataFilePublishedDate());
            dataFileMetaData.setUpdateAvailableTime(getDataFileUpdateAvailableTime());
            dataFileMetaData.setTempDataFilePath(getDataFileTempPath());
        }
    }

    private List<FiftyOneAspectPropertyMetaDataDefault> getMetricProperties() {
        List<String> dataFileList = Arrays.asList(
            "Lite", "Premium", "Enterprise");
        FiftyOneAspectPropertyMetaDataDefault[] metricProperties = new FiftyOneAspectPropertyMetaDataDefault[]{
            new FiftyOneAspectPropertyMetaDataDefault(
                "Difference",
                this,
                "Device Metrics",
                Integer.class,
                dataFileList,
                true,
                null,
                (byte)0,
                true,
                false,
                false,
                false,
                false,
                "Used when detection method is not Exact or None. This is an integer value and the larger the value the less confident the detector is in this result.",
                null,
                null,
                null),
            new FiftyOneAspectPropertyMetaDataDefault(
                "Method",
                this,
                "Device Metrics",
                String.class,
                dataFileList,
                true,
                null,
                (byte)0,
                true,
                false,
                false,
                false,
                false,
                "Provides information about the algorithm that was used to perform detection for a particular User-Agent.",
                null,
                null,
                null),
            new FiftyOneAspectPropertyMetaDataDefault(
                "Rank",
                this,
                "Device Metrics",
                Integer.class,
                dataFileList,
                true,
                null,
                (byte)0,
                true,
                false,
                false,
                false,
                false,
                "An integer value that indicates how popular the device is. The lower the rank the more popular the signature.",
                null,
                null,
                null),
            new FiftyOneAspectPropertyMetaDataDefault(
                "SignaturesCompared",
                this,
                "Device Metrics",
                Integer.class,
                dataFileList,
                true,
                null,
                (byte)0,
                true,
                false,
                false,
                false,
                false,
                "The number of device signatures that have been compared before finding a result.",
                null,
                null,
                null),
            new FiftyOneAspectPropertyMetaDataDefault(
                "DeviceId",
                this,
                "Device Metrics",
                String.class,
                dataFileList,
                true,
                null,
                (byte)0,
                true,
                false,
                false,
                false,
                false,
                "Consists of four components separated by a hyphen symbol: Hardware-Platform-Browser-IsCrawler where each Component represents an ID of the corresponding Profile.",
                null,
                null,
                null),
            new FiftyOneAspectPropertyMetaDataDefault(
                "UserAgents",
                this,
                "Device Metrics",
                List.class,
                dataFileList,
                true,
                null,
                (byte)0,
                true,
                false,
                false,
                false,
                false,
                "The matched User-Agents.",
                null,
                null,
                null)
        };
        return Arrays.asList(metricProperties);
    }

    @Override
    public void addDataFile(AspectEngineDataFile dataFile) {
        if (getDataFiles().size() >  0) {
            throw new IllegalArgumentException("DeviceDetectionPatternEngine already " +
                "has a configured data source.");
        }
        super.addDataFile(dataFile);
        if (dataFile instanceof  FiftyOneDataFile) {
            FiftyOneDataFile fiftyOneDataFile = (FiftyOneDataFile)dataFile;
            fiftyOneDataFile.setDataUpdateDownloadType("BinaryV32");
        }
    }
}
