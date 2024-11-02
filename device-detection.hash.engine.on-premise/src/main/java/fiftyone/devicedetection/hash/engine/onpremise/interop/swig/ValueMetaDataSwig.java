/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package fiftyone.devicedetection.hash.engine.onpremise.interop.swig;

public class ValueMetaDataSwig {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected ValueMetaDataSwig(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ValueMetaDataSwig obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        DeviceDetectionHashEngineModuleJNI.delete_ValueMetaDataSwig(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public String getName() {
    return DeviceDetectionHashEngineModuleJNI.ValueMetaDataSwig_getName(swigCPtr, this);
  }

  public String getDescription() {
    return DeviceDetectionHashEngineModuleJNI.ValueMetaDataSwig_getDescription(swigCPtr, this);
  }

  public String getUrl() {
    return DeviceDetectionHashEngineModuleJNI.ValueMetaDataSwig_getUrl(swigCPtr, this);
  }

}