/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package fiftyone.devicedetection.hash.engine.onpremise.interop.swig;

public class ComponentMetaDataSwig {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected ComponentMetaDataSwig(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ComponentMetaDataSwig obj) {
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
        DeviceDetectionHashEngineModuleJNI.delete_ComponentMetaDataSwig(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public int getComponentIdAsInt() {
    return DeviceDetectionHashEngineModuleJNI.ComponentMetaDataSwig_getComponentIdAsInt(swigCPtr, this);
  }

  public byte getComponentId() {
    return (byte)DeviceDetectionHashEngineModuleJNI.ComponentMetaDataSwig_getComponentId(swigCPtr, this);
  }

  public String getName() {
    return DeviceDetectionHashEngineModuleJNI.ComponentMetaDataSwig_getName(swigCPtr, this);
  }

}