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
import fiftyone.devicedetection.hash.engine.onpremise.data.PropertyMetaDataHash;
import fiftyone.devicedetection.hash.engine.onpremise.interop.ComponentIterable;
import fiftyone.devicedetection.hash.engine.onpremise.interop.PropertyIterable;
import fiftyone.devicedetection.hash.engine.onpremise.interop.swig.*;
import fiftyone.devicedetection.hash.engine.onpremise.interop.swig.Date;
import fiftyone.pipeline.core.data.EvidenceKeyFilter;
import fiftyone.pipeline.core.data.EvidenceKeyFilterWhitelist;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.data.factories.ElementDataFactory;
import fiftyone.pipeline.engines.data.AspectEngineDataFile;
import fiftyone.pipeline.engines.fiftyone.data.*;
import fiftyone.pipeline.engines.fiftyone.flowelements.FiftyOneOnPremiseAspectEngineBase;
import org.slf4j.Logger;

import java.util.*;

public class DeviceDetectionHashEngine
    extends FiftyOneOnPremiseAspectEngineBase<DeviceDataHash, FiftyOneAspectPropertyMetaData> {
    private EngineHashSwig engine = null;
    private final List<FiftyOneAspectPropertyMetaData> properties = new ArrayList<>();
    private final ConfigHashSwig config;
    private final RequiredPropertiesConfigSwig propertiesConfigSwig;
    private List<String> evidenceKeys;
    private EvidenceKeyFilter evidenceKeyFilter;
    private volatile boolean propertiesPopulated = false;

    DeviceDetectionHashEngine(
        Logger logger,
        AspectEngineDataFile dataFile,
        ConfigHashSwig config,
        RequiredPropertiesConfigSwig properties,
        ElementDataFactory<DeviceDataHash> deviceDataFactory,
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
    public FiftyOneAspectPropertyMetaData getProperty(String name) {
        PropertyMetaDataSwig swigProperty =
            engine.getMetaData().getProperties().getByKey(name);
        if (swigProperty != null) {
            return new PropertyMetaDataHash(this, swigProperty);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public CloseableIterable<ComponentMetaData> getComponents() {
        return new ComponentIterable(this, engine.getMetaData().getComponents());
    }

    @Override
    public CloseableIterable<ValueMetaData> getValues() {
        throw new UnsupportedOperationException();
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
    public void refreshData(String dataFileIdentifier) {
        AspectEngineDataFile dataFile = getDataFiles().get(0);

        if (dataFile.getDataFilePath() != null &&
            dataFile.getDataFilePath().isEmpty() == false) {
            engine = new EngineHashSwig(dataFile.getDataFilePath(), config, propertiesConfigSwig);
        }
        else {
            engine.refreshData();
        }
        setEngineMetaData();
    }

    @Override
    public void refreshData(String dataFileIdentifier, byte[] data) {
        if (engine == null) {
            engine = new EngineHashSwig(data, config, propertiesConfigSwig);
        }
        else {
            engine.refreshData(data);
        }
        setEngineMetaData();
    }

    @Override
    protected void processEngine(FlowData flowData, DeviceDataHash deviceData) {
        EvidenceDeviceDetectionSwig relevantEvidence =
            new EvidenceDeviceDetectionSwig();
        List<String> keys = evidenceKeys;
        for (Map.Entry<String, Object> evidenceItem : flowData.getEvidence().asKeyMap().entrySet()) {
            boolean containsKey = false;
            for (String key : keys) {
                if (key.equalsIgnoreCase(evidenceItem.getKey())) {
                    containsKey = true;
                    break;
                }
            }

            if (containsKey == true) {
                relevantEvidence.addFromBytes(
                    evidenceItem.getKey(),
                    evidenceItem.getKey().length(),
                    evidenceItem.getValue().toString(),
                    evidenceItem.getValue().toString().length());
            }
        }
        ((DeviceDataHashDefault) deviceData).setResults(engine.process(relevantEvidence));
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

    private List<FiftyOneAspectPropertyMetaData> getMetricProperties() {
        List<String> dataFileList = Arrays.asList(
            "Lite", "Premium", "Enterprise");
        FiftyOneAspectPropertyMetaData[] metricProperties = new FiftyOneAspectPropertyMetaData[]{

            new FiftyOneAspectPropertyMetaDataDefault(
                "MatchedNodes",
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
                "Indicates the number of hash nodes matched within the evidence.",
                null,
                null,
                null),
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
                "Drift",
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
                "Total difference in character positions where the substrings hashes were found away from where they were expected.",
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
                true,
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
            throw new IllegalArgumentException("DeviceDetectionHashEngine already " +
                "has a configured data source.");
        }
        super.addDataFile(dataFile);
        if (dataFile instanceof  FiftyOneDataFile) {
            FiftyOneDataFile fiftyOneDataFile = (FiftyOneDataFile)dataFile;
            fiftyOneDataFile.setDataUpdateDownloadType("HashTrieV34");
        }
    }
}
