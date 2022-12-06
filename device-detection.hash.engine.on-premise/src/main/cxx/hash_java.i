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


%include "../device-detection-cxx/src/common-cxx/JavaTypes.i"

nofinalize(EvidenceDeviceDetection);
nofinalize(ResultsDeviceDetection);
nofinalize(ResultsHash);

%include "../device-detection-cxx/src/hash/hash.i"

/* Avoid copying the key and value character by character and pass a pointer instead. */
%apply (char *STRING, size_t LENGTH) { (const char propertyName[], size_t propertyNameLength) }
%apply (char *STRING, size_t LENGTH) { (const char key[], size_t keyLength) }
%apply (char *STRING, size_t LENGTH) { (const char value[], size_t valueLength) }
%inline %{
  void Evidence_AddFromBytes(EvidenceBase *evidence, const char key[], size_t keyLength, const char value[], size_t valueLength) {
    (*evidence)[key] = value;
  }

  Value<std::string> Results_GetValueAsString(ResultsBase *results, const char propertyName[], size_t propertyNameLength) {
    return results->getValueAsString(propertyName);
  }
  Value<std::vector<std::string>> Results_GetValues(ResultsBase *results, const char propertyName[], size_t propertyNameLength) {
    return results->getValues(propertyName);
  }
  Value<bool> Results_GetValueAsBool(ResultsBase *results, const char propertyName[], size_t propertyNameLength) {
    return results->getValueAsBool(propertyName);
  }
  Value<int> Results_GetValueAsInteger(ResultsBase *results, const char propertyName[], size_t propertyNameLength) {
    return results->getValueAsInteger(propertyName);
  }
  Value<double> Results_GetValueAsDouble(ResultsBase *results, const char propertyName[], size_t propertyNameLength) {
    return results->getValueAsDouble(propertyName);
  }
  bool Results_ContainsProperty(ResultsBase *results, const char propertyName[], size_t propertyNameLength) {
    return results->containsProperty(propertyName);
  }
%}

%extend EvidenceBase {
%proxycode %{
  public void addFromBytes(byte[] key, byte[] value) {
    DeviceDetectionHashEngineModule.Evidence_AddFromBytes(this, key, value);
  }
%}
}
%extend ResultsBase {
%proxycode %{
  public StringValueSwig getValueAsString(byte[] bytes) {
    return DeviceDetectionHashEngineModule.Results_GetValueAsString(this, bytes);
  }
  public VectorStringValuesSwig getValues(byte[] bytes) {
    return DeviceDetectionHashEngineModule.Results_GetValues(this, bytes);
  }
  public BoolValueSwig getValueAsBool(byte[] bytes) {
    return DeviceDetectionHashEngineModule.Results_GetValueAsBool(this, bytes);
  }
  public IntegerValueSwig getValueAsInteger(byte[] bytes) {
    return DeviceDetectionHashEngineModule.Results_GetValueAsInteger(this, bytes);
  }
  public DoubleValueSwig getValueAsDouble(byte[] bytes) {
    return DeviceDetectionHashEngineModule.Results_GetValueAsDouble(this, bytes);
  }
  public boolean containsProperty(byte[] bytes) {
    return DeviceDetectionHashEngineModule.Results_ContainsProperty(this, bytes);
  }
%}
}

/* Load the native library automatically. */
%pragma(java) jniclassimports=%{
import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.pipeline.engines.fiftyone.flowelements.interop.LibLoader;
import java.nio.ByteBuffer;
%}
%pragma(java) jniclasscode=%{
  static {
    try {
      LibLoader.load(DeviceDetectionHashEngine.class);
    } catch (Exception e) {
      System.err.println("Native code library failed to load. \n" + e);
      System.exit(1);
    }
  }
%}
