/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package fiftyone.devicedetection.hash.engine.onpremise.interop.swig;

public class CollectionConfigSwig {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected CollectionConfigSwig(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(CollectionConfigSwig obj) {
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
        DeviceDetectionHashEngineModuleJNI.delete_CollectionConfigSwig(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public CollectionConfigSwig() {
    this(DeviceDetectionHashEngineModuleJNI.new_CollectionConfigSwig(), true);
  }

  public void setCapacity(long capacity) {
    DeviceDetectionHashEngineModuleJNI.CollectionConfigSwig_setCapacity(swigCPtr, this, capacity);
  }

  public void setConcurrency(int concurrency) {
    DeviceDetectionHashEngineModuleJNI.CollectionConfigSwig_setConcurrency(swigCPtr, this, concurrency);
  }

  public void setLoaded(long loaded) {
    DeviceDetectionHashEngineModuleJNI.CollectionConfigSwig_setLoaded(swigCPtr, this, loaded);
  }

  public long getCapacity() {
    return DeviceDetectionHashEngineModuleJNI.CollectionConfigSwig_getCapacity(swigCPtr, this);
  }

  public int getConcurrency() {
    return DeviceDetectionHashEngineModuleJNI.CollectionConfigSwig_getConcurrency(swigCPtr, this);
  }

  public long getLoaded() {
    return DeviceDetectionHashEngineModuleJNI.CollectionConfigSwig_getLoaded(swigCPtr, this);
  }

}
