/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package fiftyone.devicedetection.hash.engine.onpremise.interop.swig;

public class EngineHashSwig extends EngineDeviceDetectionSwig {
  private transient long swigCPtr;

  protected EngineHashSwig(long cPtr, boolean cMemoryOwn) {
    super(DeviceDetectionHashEngineModuleJNI.EngineHashSwig_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(EngineHashSwig obj) {
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
        DeviceDetectionHashEngineModuleJNI.delete_EngineHashSwig(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public EngineHashSwig(String fileName, ConfigHashSwig config, RequiredPropertiesConfigSwig properties) {
    this(DeviceDetectionHashEngineModuleJNI.new_EngineHashSwig__SWIG_0(fileName, ConfigHashSwig.getCPtr(config), config, RequiredPropertiesConfigSwig.getCPtr(properties), properties), true);
  }

  public EngineHashSwig(byte[] data, ConfigHashSwig config, RequiredPropertiesConfigSwig properties) {
    this(DeviceDetectionHashEngineModuleJNI.new_EngineHashSwig__SWIG_1(data, ConfigHashSwig.getCPtr(config), config, RequiredPropertiesConfigSwig.getCPtr(properties), properties), true);
  }

  public Date getPublishedTime() {
    return new Date(DeviceDetectionHashEngineModuleJNI.EngineHashSwig_getPublishedTime(swigCPtr, this), true);
  }

  public Date getUpdateAvailableTime() {
    return new Date(DeviceDetectionHashEngineModuleJNI.EngineHashSwig_getUpdateAvailableTime(swigCPtr, this), true);
  }

  public String getDataFilePath() {
    return DeviceDetectionHashEngineModuleJNI.EngineHashSwig_getDataFilePath(swigCPtr, this);
  }

  public String getDataFileTempPath() {
    return DeviceDetectionHashEngineModuleJNI.EngineHashSwig_getDataFileTempPath(swigCPtr, this);
  }

  public void refreshData() {
    DeviceDetectionHashEngineModuleJNI.EngineHashSwig_refreshData__SWIG_0(swigCPtr, this);
  }

  public void refreshData(String fileName) {
    DeviceDetectionHashEngineModuleJNI.EngineHashSwig_refreshData__SWIG_1(swigCPtr, this, fileName);
  }

  public void refreshData(byte[] data) {
    DeviceDetectionHashEngineModuleJNI.EngineHashSwig_refreshData__SWIG_2(swigCPtr, this, data);
  }

  public ResultsHashSwig process(EvidenceDeviceDetectionSwig evidence) {
    long cPtr = DeviceDetectionHashEngineModuleJNI.EngineHashSwig_process__SWIG_0(swigCPtr, this, EvidenceDeviceDetectionSwig.getCPtr(evidence), evidence);
    return (cPtr == 0) ? null : new ResultsHashSwig(cPtr, true);
  }

  public ResultsHashSwig process(String userAgent) {
    long cPtr = DeviceDetectionHashEngineModuleJNI.EngineHashSwig_process__SWIG_1(swigCPtr, this, userAgent);
    return (cPtr == 0) ? null : new ResultsHashSwig(cPtr, true);
  }

  public ResultsBaseSwig processBase(EvidenceBaseSwig evidence) {
    long cPtr = DeviceDetectionHashEngineModuleJNI.EngineHashSwig_processBase(swigCPtr, this, EvidenceBaseSwig.getCPtr(evidence), evidence);
    return (cPtr == 0) ? null : new ResultsBaseSwig(cPtr, true);
  }

  public ResultsDeviceDetectionSwig processDeviceDetection(EvidenceDeviceDetectionSwig evidence) {
    long cPtr = DeviceDetectionHashEngineModuleJNI.EngineHashSwig_processDeviceDetection__SWIG_0(swigCPtr, this, EvidenceDeviceDetectionSwig.getCPtr(evidence), evidence);
    return (cPtr == 0) ? null : new ResultsDeviceDetectionSwig(cPtr, true);
  }

  public ResultsDeviceDetectionSwig processDeviceDetection(String userAgent) {
    long cPtr = DeviceDetectionHashEngineModuleJNI.EngineHashSwig_processDeviceDetection__SWIG_1(swigCPtr, this, userAgent);
    return (cPtr == 0) ? null : new ResultsDeviceDetectionSwig(cPtr, true);
  }

}