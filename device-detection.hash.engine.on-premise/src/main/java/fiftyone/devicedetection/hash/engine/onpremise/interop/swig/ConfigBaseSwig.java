/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package fiftyone.devicedetection.hash.engine.onpremise.interop.swig;

public class ConfigBaseSwig {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected ConfigBaseSwig(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ConfigBaseSwig obj) {
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
        DeviceDetectionHashEngineModuleJNI.delete_ConfigBaseSwig(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setUseUpperPrefixHeaders(boolean use) {
    DeviceDetectionHashEngineModuleJNI.ConfigBaseSwig_setUseUpperPrefixHeaders(swigCPtr, this, use);
  }

  public void setUseTempFile(boolean use) {
    DeviceDetectionHashEngineModuleJNI.ConfigBaseSwig_setUseTempFile(swigCPtr, this, use);
  }

  public void setReuseTempFile(boolean reuse) {
    DeviceDetectionHashEngineModuleJNI.ConfigBaseSwig_setReuseTempFile(swigCPtr, this, reuse);
  }

  public void setTempDirectories(VectorStringSwig tempDirs) {
    DeviceDetectionHashEngineModuleJNI.ConfigBaseSwig_setTempDirectories(swigCPtr, this, VectorStringSwig.getCPtr(tempDirs), tempDirs);
  }

  public boolean getUseUpperPrefixHeaders() {
    return DeviceDetectionHashEngineModuleJNI.ConfigBaseSwig_getUseUpperPrefixHeaders(swigCPtr, this);
  }

  public boolean getUseTempFile() {
    return DeviceDetectionHashEngineModuleJNI.ConfigBaseSwig_getUseTempFile(swigCPtr, this);
  }

  public boolean getReuseTempFile() {
    return DeviceDetectionHashEngineModuleJNI.ConfigBaseSwig_getReuseTempFile(swigCPtr, this);
  }

  public VectorStringSwig getTempDirectories() {
    return new VectorStringSwig(DeviceDetectionHashEngineModuleJNI.ConfigBaseSwig_getTempDirectories(swigCPtr, this), true);
  }

  public int getConcurrency() {
    return DeviceDetectionHashEngineModuleJNI.ConfigBaseSwig_getConcurrency(swigCPtr, this);
  }

}
