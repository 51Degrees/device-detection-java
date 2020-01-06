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

%include "../device-detection-cxx/src/hash/hash.i"

/* Avoid copying the key and value character by character and pass a pointer instead. */
%apply (char *STRING, size_t LENGTH, char *STRING, size_t LENGTH) { (const char key[], size_t keyLength, const char value[], size_t valueLength) }
%extend EvidenceBase {
  void addFromBytes(const char key[], size_t keyLength, const char value[], size_t valueLength) {
    (*$self)[key] = value;
  }
}

/* Avoid copying the property name character by character and pass a pointer instead. */
%apply (char *STRING, size_t LENGTH) { (const char propertyName, size_t propertyNameLength) }
%extend ResultsBase {
  Value<std::string> getValueAsString(const char propertyName[], size_t propertyNameLength) {
    return (*$self).getValueAsString(propertyName);
  }
  Value<std::vector<std::string>> getValues(const char propertyName[], size_t propertyNameLength) {
    return (*$self).getValues(propertyName);
  }
  Value<bool> getValueAsBool(const char propertyName[], size_t propertyNameLength) {
    return (*$self).getValueAsBool(propertyName);
  }
  Value<int> getValueAsInteger(const char propertyName[], size_t propertyNameLength) {
    return (*$self).getValueAsInteger(propertyName);
  }
  Value<double> getValueAsDouble(const char propertyName[], size_t propertyNameLength) {
    return (*$self).getValueAsDouble(propertyName);
  }
  bool containsProperty(const char propertyName[], size_t propertyNameLength) {
    return (*$self).containsProperty(propertyName);
  }
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