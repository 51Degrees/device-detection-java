/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package fiftyone.devicedetection.hash.engine.onpremise.interop.swig;

public class EngineDeviceDetectionSwig extends EngineBaseSwig {
  private transient long swigCPtr;

  protected EngineDeviceDetectionSwig(long cPtr, boolean cMemoryOwn) {
    super(DeviceDetectionHashEngineModuleJNI.EngineDeviceDetectionSwig_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(EngineDeviceDetectionSwig obj) {
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
        DeviceDetectionHashEngineModuleJNI.delete_EngineDeviceDetectionSwig(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static void setDefaultElementDataKey(String value) {
    DeviceDetectionHashEngineModuleJNI.EngineDeviceDetectionSwig_defaultElementDataKey_set(value);
  }

  public static String getDefaultElementDataKey() {
    return DeviceDetectionHashEngineModuleJNI.EngineDeviceDetectionSwig_defaultElementDataKey_get();
  }

  public ResultsDeviceDetectionSwig processDeviceDetection(EvidenceDeviceDetectionSwig evidence) {
    long cPtr = DeviceDetectionHashEngineModuleJNI.EngineDeviceDetectionSwig_processDeviceDetection__SWIG_0(swigCPtr, this, EvidenceDeviceDetectionSwig.getCPtr(evidence), evidence);
    return (cPtr == 0) ? null : new ResultsDeviceDetectionSwig(cPtr, true);
  }

  public ResultsDeviceDetectionSwig processDeviceDetection(String userAgent) {
    long cPtr = DeviceDetectionHashEngineModuleJNI.EngineDeviceDetectionSwig_processDeviceDetection__SWIG_1(swigCPtr, this, userAgent);
    return (cPtr == 0) ? null : new ResultsDeviceDetectionSwig(cPtr, true);
  }

}
