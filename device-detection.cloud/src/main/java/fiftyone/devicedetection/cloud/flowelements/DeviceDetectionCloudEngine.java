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

package fiftyone.devicedetection.cloud.flowelements;

import fiftyone.devicedetection.cloud.data.DeviceDataCloud;
import fiftyone.pipeline.cloudrequestengine.data.CloudRequestData;
import fiftyone.pipeline.cloudrequestengine.flowelements.CloudAspectEngineBase;
import fiftyone.pipeline.cloudrequestengine.flowelements.CloudRequestEngine;
import fiftyone.pipeline.core.data.AccessiblePropertyMetaData;
import fiftyone.pipeline.core.data.EvidenceKeyFilter;
import fiftyone.pipeline.core.data.EvidenceKeyFilterWhitelist;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.data.factories.ElementDataFactory;
import fiftyone.pipeline.core.data.types.JavaScript;
import fiftyone.pipeline.core.exceptions.PipelineConfigurationException;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.data.AspectPropertyMetaData;
import fiftyone.pipeline.engines.data.AspectPropertyMetaDataDefault;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.data.AspectPropertyValueDefault;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.json.JSONArray;

/**
 * Engine that takes the JSON response from the {@link CloudRequestEngine} and
 * uses it populate a {@link DeviceDataCloud} instance for easier consumption.
 */
public class DeviceDetectionCloudEngine
    extends CloudAspectEngineBase<DeviceDataCloud> {
    private List<AspectPropertyMetaData> aspectProperties;
    private String dataSourceTier;
    private CloudRequestEngine cloudRequestEngine;

    /**
     * Construct a new instance of the {@link DeviceDetectionCloudEngine}.
     * @param logger logger instance to use for logging
     * @param deviceDataFactory the factory to use when creating a
     *                          {@link DeviceDataCloud} instance
     */
    public DeviceDetectionCloudEngine(
        Logger logger,
        ElementDataFactory<DeviceDataCloud> deviceDataFactory) {
        super(logger, deviceDataFactory);
        this.cloudRequestEngine = null;
    }

    @Override
    public List<AspectPropertyMetaData> getProperties() {
        return aspectProperties;
    }

    @Override
    public String getDataSourceTier() {
        return dataSourceTier;
    }

    @Override
    public String getElementDataKey() {
        return "device";
    }

    @Override
    public EvidenceKeyFilter getEvidenceKeyFilter() {
        // This engine needs no evidence.
        // It works from the cloud request data.
        return new EvidenceKeyFilterWhitelist(new ArrayList<String>());
    }

    @Override
    protected void processEngine(FlowData data, DeviceDataCloud aspectData) {
        if (cloudRequestEngine == null) {
            throw new PipelineConfigurationException(
                "The '" + getClass().getName() + "' requires a " +
                    "'CloudRequestEngine' before it in the Pipeline. This " +
                    "engine will be unable to produce results until this is " +
                    "corrected.");
        }
        else {
            CloudRequestData requestData =
                data.getFromElement(cloudRequestEngine);
            String json = "";
            json = requestData.getJsonResponse();

            // Extract data from json to the aspectData instance.
            JSONObject jsonObj = new JSONObject(json);
            JSONObject deviceObj = jsonObj.getJSONObject("device");

            Map<String, Object> deviceMap =
                new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

            for (AspectPropertyMetaData property : getProperties()) {
                String type = property.getType().getSimpleName();
                switch(type) {
                    case ("List"):
                        deviceMap.put(
                            property.getName(),
                            getListAspectPropertyValue(deviceObj, property));
                        break;
                    case ("JavaScript"):
                        deviceMap.put(
                            property.getName(),
                            getJavaScriptAspectPropertyValue(deviceObj, property));
                        break;
                    case ("String"):
                        deviceMap.put(
                            property.getName(),
                            getStringAspectPropertyValue(deviceObj, property));
                            break;
                    case ("boolean"):
                        deviceMap.put(
                            property.getName(),
                            getBooleanAspectPropertyValue(deviceObj, property));
                        break;
                    case ("int"):
                        deviceMap.put(
                            property.getName(),
                            getIntegerAspectPropertyValue(deviceObj, property));
                        break;
                    case ("double"):
                        deviceMap.put(
                            property.getName(),
                            getDoubleAspectPropertyValue(deviceObj, property));
                        break;
                    default:
                        deviceMap.put(
                            property.getName(),
                            getStringAspectPropertyValue(deviceObj, property));
                            break;
                }
            }

            aspectData.populateFromMap(deviceMap);
        }
    }

    @Override
    protected void unmanagedResourcesCleanup() {
    }

    @Override
    public void addPipeline(Pipeline pipeline) {
        if (cloudRequestEngine == null) {
            cloudRequestEngine = pipeline.getElement(CloudRequestEngine.class);
            if (cloudRequestEngine != null) {
                if (loadAspectProperties(cloudRequestEngine) == false) {
                    logger.error("Failed to load aspect properties");
                }
            }

        }
        super.addPipeline(pipeline);
    }

    /**
     * Load all aspect property meta data using the properties returned by the
     * cloud engine ({@link CloudRequestEngine#getPublicProperties()}.
     * @param engine cloud request engine
     * @return true if the properties were successfully loaded into
     * {@link #aspectProperties}
     */
    private boolean loadAspectProperties(CloudRequestEngine engine) {
        Map<String, AccessiblePropertyMetaData.ProductMetaData> map =
            engine.getPublicProperties();

        if (map != null &&
            map.size() > 0 &&
            map.containsKey(getElementDataKey())) {
            aspectProperties = new ArrayList<>();
            dataSourceTier = map.get(getElementDataKey()).dataTier;

            for (AccessiblePropertyMetaData.PropertyMetaData item :
                map.get(getElementDataKey()).properties) {
                AspectPropertyMetaData property = new AspectPropertyMetaDataDefault(
                    item.name,
                    this,
                    item.category,
                    item.getPropertyType(),
                    new ArrayList<String>(),
                    true);
                aspectProperties.add(property);
            }
            return true;
        }
        else {
            logger.error("Aspect properties could not be loaded for" +
                " the Device Detection cloud engine", this);
            return false;
        }
    }

    /**
     * Get the integer representation of a value from the cloud engine's JSON
     * response, and wrap it in an {@link AspectPropertyValue}.
     * @param deviceObj to get the value from
     * @param property to get the value of
     * @return {@link AspectPropertyValue} with a parsed value, or the reason
     * for the value not being present
     */
    private AspectPropertyValue<Integer> getIntegerAspectPropertyValue(
        JSONObject deviceObj,
        AspectPropertyMetaData property) {
        String key = property.getName().toLowerCase();
        AspectPropertyValue<Integer> intValue = new AspectPropertyValueDefault<>();
        if(deviceObj.isNull(key)){
            intValue.setNoValueMessage(getNoValueReason(deviceObj, key));
        }
        else {
            intValue.setValue(deviceObj.getInt(key));
        }
        return intValue;
    }

    private AspectPropertyValue<Double> getDoubleAspectPropertyValue(JSONObject deviceObj, AspectPropertyMetaData property) {
        String key = property.getName().toLowerCase();
        AspectPropertyValue<Double> doubleValue = new AspectPropertyValueDefault<>();
        if(deviceObj.isNull(key)){
            doubleValue.setNoValueMessage(getNoValueReason(deviceObj, key));
        }
        else {
            doubleValue.setValue(deviceObj.getDouble(key));
        }
        return doubleValue;
    }

    /**
     * Get the boolean representation of a value from the cloud engine's JSON
     * response, and wrap it in an {@link AspectPropertyValue}.
     * @param deviceObj to get the value from
     * @param property to get the value of
     * @return {@link AspectPropertyValue} with a parsed value, or the reason
     * for the value not being present
     */
    private AspectPropertyValue<Boolean> getBooleanAspectPropertyValue(
        JSONObject deviceObj,
        AspectPropertyMetaData property) {
        String key = property.getName().toLowerCase();
        AspectPropertyValue<Boolean> booleanValue = new AspectPropertyValueDefault<>();
        if(deviceObj.isNull(key)){
            booleanValue.setNoValueMessage(getNoValueReason(deviceObj, key));
        }
        else {
            booleanValue.setValue(deviceObj.getBoolean(key));
        }
        return booleanValue;
    }

    /**
     * Get the string list representation of a value from the cloud engine's
     * JSON response, and wrap it in an {@link AspectPropertyValue}.
     * @param deviceObj to get the value from
     * @param property to get the value of
     * @return {@link AspectPropertyValue} with a parsed value, or the reason
     * for the value not being present
     */
    private AspectPropertyValue<List<String>> getListAspectPropertyValue(
        JSONObject deviceObj,
        AspectPropertyMetaData property){
        String key = property.getName().toLowerCase();
        AspectPropertyValue<List<String>> listValue = new AspectPropertyValueDefault<>();
        if(deviceObj.isNull(key))
        {
            listValue.setNoValueMessage(getNoValueReason(deviceObj, key));
        }
        else 
        {
            JSONArray jsonArray = deviceObj.getJSONArray(key);
            List<String> strings = new ArrayList<>(jsonArray.length());
            for (Object object : jsonArray.toList()) {
                strings.add(Objects.toString(object, null));
            }
            listValue.setValue(strings);
        }
        return listValue;
    }

    /**
     * Get the JavaScript representation of a value from the cloud engine's JSON
     * response, and wrap it in an {@link AspectPropertyValue}.
     * @param deviceObj to get the value from
     * @param property to get the value of
     * @return {@link AspectPropertyValue} with a parsed value, or the reason
     * for the value not being present
     */
    private AspectPropertyValue<JavaScript> getJavaScriptAspectPropertyValue(
        JSONObject deviceObj,
        AspectPropertyMetaData property){
        String key = property.getName().toLowerCase();
        AspectPropertyValue<JavaScript> jsValue = new AspectPropertyValueDefault<>();
        if(deviceObj.isNull(key)){
            jsValue.setNoValueMessage(getNoValueReason(deviceObj, key));
        } else {
            jsValue.setValue(new JavaScript(deviceObj.getString(key)));
        }
        return jsValue;
    }

    /**
     * Get the string representation of a value from the cloud engine's JSON
     * response, and wrap it in an {@link AspectPropertyValue}.
     * @param deviceObj to get the value from
     * @param property to get the value of
     * @return {@link AspectPropertyValue} with a parsed value, or the reason
     * for the value not being present
     */
    private AspectPropertyValue<String> getStringAspectPropertyValue(
        JSONObject deviceObj,
        AspectPropertyMetaData property){
        String key = property.getName().toLowerCase();
        AspectPropertyValue<String> stringValue = new AspectPropertyValueDefault<>();
        if(deviceObj.isNull(key)){
            stringValue.setNoValueMessage(getNoValueReason(deviceObj, key));
        }
        else {
            stringValue.setValue(deviceObj.getString(key));
        }
        return stringValue;
    }

    /**
     * Get the reason for the value of a property not being present in the cloud
     * engine's JSON response.
     * @param deviceObj to get the null reason from
     * @param key to get the null reason from
     * @return the reason for the missing property
     */
    private String getNoValueReason(JSONObject deviceObj, String key){
        Object reason = tryToGet(deviceObj, key + "nullreason");
        return reason == null ? null : reason.toString();
    }

    /**
     * Try to get a value from a JSON object.
     * @param jsonObj to get the value from
     * @param key to get the value of
     * @return the value or null
     */
    private Object tryToGet(JSONObject jsonObj, String key) {
        if (jsonObj.has(key))
            return jsonObj.opt(key);
        return null;
    }
}
