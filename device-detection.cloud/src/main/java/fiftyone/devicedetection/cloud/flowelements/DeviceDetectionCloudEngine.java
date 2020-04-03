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

public class DeviceDetectionCloudEngine
    extends CloudAspectEngineBase<DeviceDataCloud> {
    private List<AspectPropertyMetaData> aspectProperties;
    private String dataSourceTier;
    private CloudRequestEngine cloudRequestEngine;

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
            CloudRequestData requestData = data.getFromElement(cloudRequestEngine);
            String json = "";
            json = requestData.getJsonResponse();

            // Extract data from json to the aspectData instance.
            JSONObject jsonObj = new JSONObject(json);
            JSONObject deviceObj = jsonObj.getJSONObject("device");
            JSONObject nullValueReasonsObj = jsonObj.getJSONObject("nullValueReasons");

            Map<String, Object> deviceMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

            for (AspectPropertyMetaData property : getProperties()) {
                String type = property.getType().getSimpleName();
                switch(type) {
                    case ("List"):
                        deviceMap.put(
                            property.getName(),
                            getListAspectPropertyValue(deviceObj, nullValueReasonsObj, property));
                        break;
                    case ("JavaScript"):
                        deviceMap.put(
                            property.getName(),
                            getJavaScriptAspectPropertyValue(deviceObj, nullValueReasonsObj, property));
                        break;
                    case ("String"):
                        deviceMap.put(
                            property.getName(),
                            getStringAspectPropertyValue(deviceObj, nullValueReasonsObj, property));
                            break;
                    case ("boolean"):
                        deviceMap.put(
                            property.getName(),
                            getBooleanAspectPropertyValue(deviceObj, nullValueReasonsObj, property));
                        break;
                    case ("int"):
                        deviceMap.put(
                            property.getName(),
                            getIntegerAspectPropertyValue(deviceObj, nullValueReasonsObj, property));
                        break;
                    case ("double"):
                        deviceMap.put(
                            property.getName(),
                            getDoubleAspectPropertyValue(deviceObj, nullValueReasonsObj, property));
                        break;
                    default:
                        deviceMap.put(
                            property.getName(),
                            getStringAspectPropertyValue(deviceObj, nullValueReasonsObj, property));
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
    
    private AspectPropertyValue<Integer> getIntegerAspectPropertyValue(JSONObject deviceObj, JSONObject nullValueReasonsObj, AspectPropertyMetaData property) {
        String key = property.getName().toLowerCase();
        AspectPropertyValue<Integer> intValue = new AspectPropertyValueDefault<>();
        if(deviceObj.isNull(key)){
            intValue.setNoValueMessage(getNoValueReason(nullValueReasonsObj, key));
        }
        else {
            intValue.setValue(deviceObj.getInt(key));
        }
        return intValue;
    }

    private AspectPropertyValue<Double> getDoubleAspectPropertyValue(JSONObject deviceObj, JSONObject nullValueReasonsObj, AspectPropertyMetaData property) {
        String key = property.getName().toLowerCase();
        AspectPropertyValue<Double> doubleValue = new AspectPropertyValueDefault<>();
        if(deviceObj.isNull(key)){
            doubleValue.setNoValueMessage(getNoValueReason(nullValueReasonsObj, key));
        }
        else {
            doubleValue.setValue(deviceObj.getDouble(key));
        }
        return doubleValue;
    }

    private AspectPropertyValue<Boolean> getBooleanAspectPropertyValue(JSONObject deviceObj, JSONObject nullValueReasonsObj, AspectPropertyMetaData property) {
        String key = property.getName().toLowerCase();
        AspectPropertyValue<Boolean> booleanValue = new AspectPropertyValueDefault<>();
        if(deviceObj.isNull(key)){
            booleanValue.setNoValueMessage(getNoValueReason(nullValueReasonsObj, key));
        }
        else {
            booleanValue.setValue(deviceObj.getBoolean(key));
        }
        return booleanValue;
    }
    
    private AspectPropertyValue<List<String>> getListAspectPropertyValue(JSONObject deviceObj, JSONObject nullValueReasonsObj, AspectPropertyMetaData property){
        String key = property.getName().toLowerCase();
        AspectPropertyValue<List<String>> listValue = new AspectPropertyValueDefault<>();
        if(deviceObj.isNull(key))
        {
            listValue.setNoValueMessage(getNoValueReason(nullValueReasonsObj, key));
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
    
    private AspectPropertyValue<JavaScript> getJavaScriptAspectPropertyValue(JSONObject deviceObj, JSONObject nullValueReasonsObj, AspectPropertyMetaData property){
        String key = property.getName().toLowerCase();
        AspectPropertyValue<JavaScript> jsValue = new AspectPropertyValueDefault<>();
        if(deviceObj.isNull(key)){
            jsValue.setNoValueMessage(getNoValueReason(nullValueReasonsObj, key));
        } else {
            jsValue.setValue(new JavaScript(deviceObj.getString(key)));
        }
        return jsValue;
    }
    
    private AspectPropertyValue<String> getStringAspectPropertyValue(JSONObject deviceObj, JSONObject nullValueReasonsObj, AspectPropertyMetaData property){
        String key = property.getName().toLowerCase();
        AspectPropertyValue<String> stringValue = new AspectPropertyValueDefault<>();
        if(deviceObj.isNull(key)){
            stringValue.setNoValueMessage(getNoValueReason(nullValueReasonsObj, key));
        }
        else {
            stringValue.setValue(deviceObj.getString(key));
        }
        return stringValue;
    }
    
    private String getNoValueReason(JSONObject nullValueReasonsObj, String key){
        return tryToGet(nullValueReasonsObj, this.getElementDataKey() + "." + key).toString();
    }
    
    private Object tryToGet(JSONObject jsonObj, String key) {
        if (jsonObj.has(key))
            return jsonObj.opt(key);
        return null;
    }
}
