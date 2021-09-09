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
import fiftyone.devicedetection.hash.engine.onpremise.data.ProfileMetaDataHash;
import fiftyone.devicedetection.hash.engine.onpremise.data.PropertyMetaDataHash;
import fiftyone.devicedetection.hash.engine.onpremise.data.ValueMetaDataHash;
import fiftyone.devicedetection.hash.engine.onpremise.interop.*;
import fiftyone.devicedetection.hash.engine.onpremise.interop.swig.*;
import fiftyone.devicedetection.hash.engine.onpremise.interop.swig.Date;
import fiftyone.pipeline.core.data.EvidenceKeyFilter;
import fiftyone.pipeline.core.data.EvidenceKeyFilterWhitelist;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.data.factories.ElementDataFactory;
import fiftyone.pipeline.engines.caching.FlowCache;
import fiftyone.pipeline.engines.data.AspectEngineDataFile;
import fiftyone.pipeline.engines.fiftyone.data.*;
import fiftyone.pipeline.engines.fiftyone.flowelements.FiftyOneOnPremiseAspectEngineBase;
import org.slf4j.Logger;

import java.util.*;

/**
 * Hash device detection engine. This engine takes User-Agents and other
 * relevant HTTP headers and returns properties about the device which produced
 * them e.g. DeviceType or ReleaseDate.
 */
public class DeviceDetectionHashEngine
    extends FiftyOneOnPremiseAspectEngineBase<DeviceDataHash,
    FiftyOneAspectPropertyMetaData> {
    private EngineHashSwig engine = null;
    private final List<FiftyOneAspectPropertyMetaData> properties = new ArrayList<>();
    private final ConfigHashSwig config;
    private final RequiredPropertiesConfigSwig propertiesConfigSwig;
    private List<String> evidenceKeys;
    private EvidenceKeyFilter evidenceKeyFilter;
    private volatile boolean propertiesPopulated = false;
    private final Random rand = new Random();

    /**
     * Construct a new instance of the {@link DeviceDetectionHashEngine}.
     * @param logger logger instance to use for logging
     * @param dataFile data file to read the data set from
     * @param config native configuration which was configured by the builder
     * @param properties native required properties configuration which define
     *                   the properties which the engine should be initialised
     *                   with
     * @param deviceDataFactory the factory to use when creating a
     *                          {@link DeviceDataHash} instance
     * @param tempDataFileDir the file where a temporary data file copy
     *                        will be stored if one is created
     */
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

    /**
     * Get the evidence keys from the native engine and add to a {@link List}.
     * @param engine to get the keys from
     * @return evidence keys list
     */
    private static List<String> getKeysFromEngine(
        EngineDeviceDetectionSwig engine) {
        List<String> result = new ArrayList<>();
        // In this case, the vector does not need to be closed.
        // The vector is a pointer to memory owned by the native engine, so the
        // delete method actually doesn't call down to the native layer.
        VectorStringSwig keys = engine.getKeys();
        result.addAll(keys);
        return result;
    }

    @Override
    public String getElementDataKey() {
        return "device";
    }

    /**
     * Get the native meta data instance for this engine.
     * @return native meta data
     */
    public MetaDataSwig getMetaData() {
        return engine.getMetaData();
    }

    @Override
    public List<FiftyOneAspectPropertyMetaData> getProperties() {
        if (propertiesPopulated == false) {
            synchronized (properties) {
                if (propertiesPopulated == false) {
                    properties.clear();
                    List<FiftyOneAspectPropertyMetaData> newProperties =
                        new ArrayList<>();

                    try (PropertyIterable iterable = new PropertyIterable(
                            this,
                            newProperties,
                            engine.getMetaData().getProperties())) {
                        for (FiftyOneAspectPropertyMetaData property :
                            iterable) {
                            properties.add(property);
                        }
                    } catch (Exception e) {
                        logger.error(
                            "Exception occurred while constructing properties.",
                            e);
                    }

                    newProperties.addAll(getMetricProperties());

                    propertiesPopulated = true;
                }
            }
        }
        return properties;
    }

    @Override
    public FiftyOneAspectPropertyMetaData getProperty(String name) {
        FiftyOneAspectPropertyMetaData result = null;
        PropertyMetaDataCollectionSwig properties =
            engine.getMetaData().getProperties();
        PropertyMetaDataSwig swigProperty = properties.getByKey(name);
        if (swigProperty != null) {
            result = new PropertyMetaDataHash(this, swigProperty);
        }
        properties.delete();

        if (result == null) {
            for (FiftyOneAspectPropertyMetaData property : getMetricProperties()) {
                if (property.getName().equalsIgnoreCase(name)) {
                    return property;
                }
            }
        }

        return result;
    }

    @Override
    public CloseableIterable<ProfileMetaData> getProfiles() {
        return new ProfileIterable(
            this,
            engine.getMetaData().getProfiles());
    }

    @Override
    public ProfileMetaData getProfile(int profileId) {
        ProfileMetaDataCollectionSwig profiles =
            engine.getMetaData().getProfiles();
        ProfileMetaDataSwig profile = profiles.getByKey(profileId);
        profiles.delete();
        return profile == null ?
            null : new ProfileMetaDataHash(this, profile);
    }

    @Override
    public CloseableIterable<ComponentMetaData> getComponents() {
        return new ComponentIterable(
            this,
            engine.getMetaData().getComponents());
    }

    @Override
    public CloseableIterable<ValueMetaData> getValues() {
        return new ValueIterable(
            this,
            engine.getMetaData().getValues());
    }

    @Override
    public ValueMetaData getValue(String propertyName, String valueName) {
        ValueMetaDataHash result = null;
        ValueMetaDataKeySwig key = new ValueMetaDataKeySwig(
            propertyName,
            valueName);
        ValueMetaDataCollectionSwig values =
                 engine.getMetaData().getValues();
        result = new ValueMetaDataHash(
            this,
            // this value is closed by the enclosing meta data.
            values.getByKey(key));
        values.delete();
        key.delete();
        return result;
    }

    @Override
    public java.util.Date getDataFilePublishedDate(String dataFileIdentifier) {
        Calendar calendar = Calendar.getInstance();
        Date value = engine.getPublishedTime();
        // java.util.Calendar month is 0 based where January = 0
        calendar.set(
            value.getYear(),
            value.getMonth() - 1,
            value.getDay());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        return calendar.getTime();
    }

    @Override
    public java.util.Date getDataFileUpdateAvailableTime(String dataFileIdentifier) {
        Calendar calendar = Calendar.getInstance();
        Date value = engine.getUpdateAvailableTime();
        // java.util.Calendar month is 0 based where January = 0
        calendar.set(
            value.getYear(),
            value.getMonth() - 1,
            value.getDay());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, rand.nextInt(60));
        calendar.set(Calendar.HOUR, 12);
        return calendar.getTime();
    }

    @Override
    public String getDataSourceTier() {
        return engine.getType();
    }

    /**
     * Used internally to populate the meta data returned by
     * {@link #getDataFileMetaData()}.
     * @return temp path
     */
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
            engine = new EngineHashSwig(
                dataFile.getDataFilePath(),
                config,
                propertiesConfigSwig);
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
        try (EvidenceDeviceDetectionSwig relevantEvidence =
            new EvidenceDeviceDetectionSwig()) {
            List<String> keys = evidenceKeys;
            for (Map.Entry<String, Object> evidenceItem :
                flowData.getEvidence().asKeyMap().entrySet()) {
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
            ((DeviceDataHashDefault) deviceData).setResults(
                engine.process(relevantEvidence));
        }
    }

    @Override
    protected void unmanagedResourcesCleanup() {
        if (propertiesPopulated) {
            synchronized (properties) {
                if (propertiesPopulated) {
                    properties.clear();
                }
            }
        }
        if (config != null) {
            config.delete();
        }
        if (propertiesConfigSwig != null) {
            propertiesConfigSwig.delete();
        }
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
        FiftyOneDataFile dataFileMetaData =
            (FiftyOneDataFile)getDataFileMetaData();
        if (dataFileMetaData != null) {
            dataFileMetaData.setDataPublishedDateTime(
                getDataFilePublishedDate());
            dataFileMetaData.setUpdateAvailableTime(
                getDataFileUpdateAvailableTime());
            dataFileMetaData.setTempDataFilePath(getDataFileTempPath());
        }
    }

    /**
     * Get the match metric properties which are not defined in the data file.
     * @return meta data for metric properties
     */
    private List<FiftyOneAspectPropertyMetaData> getMetricProperties() {
        List<String> dataFileList = Arrays.asList(
            "Lite", "Premium", "Enterprise", "TAC");
        FiftyOneAspectPropertyMetaData[] metricProperties =
            new FiftyOneAspectPropertyMetaData[]{

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
                Arrays.asList(),
                new ValueMetaDataDefault("0")),
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
                Arrays.asList(),
                new ValueMetaDataDefault("0")),
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
                Arrays.asList(),
                new ValueMetaDataDefault("0")),
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
                Arrays.asList(),
                new ValueMetaDataDefault("0-0-0-0")),
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
                Arrays.asList(),
                new ValueMetaDataDefault("n/a")),
            new FiftyOneAspectPropertyMetaDataDefault(
                "Method",
                this,
                "Device Metrics",
                String.class,
                dataFileList,
                true,
                null,
                (byte) 0,
                true,
                true,
                false,
                false,
                false,
                "Provides information about the algorithm that was used to perform detection for a particular User-Agent.",
                null,
                Arrays.asList(
                    new ValueMetaDataDefault("NONE"),
                    new ValueMetaDataDefault("PERFORMANCE"),
                    new ValueMetaDataDefault("COMBINED"),
                    new ValueMetaDataDefault( "PREDICTIVE")),
                new ValueMetaDataDefault("NONE"))
        };
        return Arrays.asList(metricProperties);
    }

    @Override
    public void addDataFile(AspectEngineDataFile dataFile) {
        if (getDataFiles().size() >  0) {
            throw new IllegalArgumentException(
                "DeviceDetectionHashEngine already has a configured data " +
                "source.");
        }
        super.addDataFile(dataFile);
        if (dataFile instanceof  FiftyOneDataFile) {
            FiftyOneDataFile fiftyOneDataFile = (FiftyOneDataFile)dataFile;
            fiftyOneDataFile.setDataUpdateDownloadType("HashV41");
        }
    }

    @Override
    public void setCache(FlowCache cache) {
        throw new UnsupportedOperationException(
            "A results cache cannot be configured in the on-premise Hash engine. " +
                "The overhead of having to manage native object lifetimes when " +
                "a cache is enabled outweighs the benefit of the cache.");
    }
}
